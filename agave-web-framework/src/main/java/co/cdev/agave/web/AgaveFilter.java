package co.cdev.agave.web;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import co.cdev.agave.configuration.Config;
import co.cdev.agave.configuration.ConfigGenerator;
import co.cdev.agave.configuration.ConfigGeneratorImpl;
import co.cdev.agave.configuration.ConfigImpl;
import co.cdev.agave.configuration.HandlerDescriptor;
import co.cdev.agave.configuration.ParamDescriptor;
import co.cdev.agave.configuration.RoutingContext;
import co.cdev.agave.conversion.AgaveConversionException;

public class AgaveFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(AgaveFilter.class.getName());
    private static final String WORKFLOW_HANDLER_SUFFIX = "-handler";
    private static final String WORKFLOW_FORM_SUFFIX = "-form";
    private static final String DEFAULT_CONFIG_FILE_NAME = "agave.conf";
    
    private FilterConfig filterConfig;
    private Config config;
    private LifecycleHooks lifecycleHooks;
    private File classesDirectory;
    private HandlerFactory handlerFactory;
    private FormFactory formFactory;
    private RequestMatcher requestMatcher;
    private SortedSet<ResultProcessor> resultProcessors;

    protected File provideClassesDirectory(FilterConfig filterConfig)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        File classesDir = null;

        if (System.getProperty("classesDirectory") != null) {
            classesDir = new File(System.getProperty("classesDirectory"));
        } else {
            classesDir = new File(filterConfig.getServletContext().getRealPath("/WEB-INF/classes"));
        }

        return classesDir;
    }
    
    protected LifecycleHooks provideLifecycleHooks(FilterConfig filterConfig)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        LifecycleHooks hooks = null;

        String lifecycleHooksParameter = filterConfig.getInitParameter("lifecycleHooks");
        if (lifecycleHooksParameter != null) {
            hooks = (LifecycleHooks) Class.forName(lifecycleHooksParameter).newInstance();
        } else {
            hooks = new DefaultLifecycleHooks();
        }

        return hooks;
    }

    protected HandlerFactory provideHandlerFactory(FilterConfig filterConfig)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        HandlerFactory factory = null;

        String handlerFactoryParameter = filterConfig.getInitParameter("handlerFactory");

        if (handlerFactoryParameter != null) {
            factory = (HandlerFactory) Class.forName(handlerFactoryParameter).newInstance();
        } else {
            factory = new HandlerFactoryImpl();
        }

        return factory;
    }

    protected FormFactory provideFormFactory(FilterConfig filterConfig)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        FormFactory factory = null;

        String formFactoryParameter = filterConfig.getInitParameter("formFactory");

        if (formFactoryParameter != null) {
            factory = (FormFactory) Class.forName(formFactoryParameter).newInstance();
        } else {
            factory = new FormFactoryImpl();
        }

        return factory;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        try {
            classesDirectory = provideClassesDirectory(filterConfig);
            lifecycleHooks = provideLifecycleHooks(filterConfig);
            
            File configFile = new File(classesDirectory, DEFAULT_CONFIG_FILE_NAME);
            
            if (configFile.exists() && configFile.canRead()) {
                config = new ConfigImpl();
                config.readFromFile(configFile);
            } else {
                ConfigGenerator configGenerator = new ConfigGeneratorImpl(classesDirectory);
                config = configGenerator.generateConfig();
            }
            
            requestMatcher = new RequestMatcherImpl(config);
            
            // These need to support dependency injection
            
            handlerFactory = provideHandlerFactory(filterConfig);
            handlerFactory.initialize();
            formFactory = provideFormFactory(filterConfig);            
            formFactory.initialize();
            
            resultProcessors = new TreeSet<ResultProcessor>(new Comparator<ResultProcessor>() {
                @Override
                public int compare(ResultProcessor a, ResultProcessor b) {
                    Class<?> aa = a.getClass();
                    Class<?> bb = b.getClass();
                    
                    while (true) {
                        if (aa == null) {
                            return 1;
                        } else if (bb == null) {
                            return -1;
                        }
                        
                        aa = aa.getSuperclass();
                        bb = bb.getSuperclass();
                    }
                }
            });
            addResultProcessor(new DestinationProcessor());
            addResultProcessor(new HTTPResponseProcessor());
            addResultProcessor(new URIProcessor());
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }
    
    protected void addResultProcessor(ResultProcessor resultProcessor) {
        resultProcessors.add(resultProcessor);
    }

    /**
     * Destroys this filter.
     */
    @Override
    public void destroy() {
        classesDirectory = null;
        config = null;
        filterConfig = null;
        requestMatcher = null;
        handlerFactory = null;
        formFactory = null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) 
            throws IOException, ServletException {
        
        ServletContext servletContext = filterConfig.getServletContext();
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        HandlerDescriptor handlerDescriptor = requestMatcher.findMatch(request);
        
        if (handlerDescriptor != null) {
            
            // Wrap the request if necessary so that the uploaded content can be accessed like
            // regular string parameters
            
            if (RequestUtils.isMultipart(request)) {
                try {
                    request = wrapMultipartRequest(request);
                } catch (Exception e) {
                    throw new ServletException(e);
                }
            }
            
            HttpSession session = request.getSession(true);
            RoutingContext routingContext = new RoutingContext(servletContext, request, response, session);

            if (lifecycleHooks.beforeFilteringRequest(handlerDescriptor, routingContext)) {
                return;
            }

            LOGGER.log(Level.FINE, "Handling requests to \"{0}\" with \"{1}\"", new Object[] {
                request.getServletPath(),
                handlerDescriptor.getHandlerMethod()
            });
            
            URIParamExtractor uriParamExtractor = new URIParamExtractorImpl(handlerDescriptor.getURIPattern());
            Map<String, String> uriParams = uriParamExtractor.extractParams(request);

            Object formInstance = null;

            // Attempt to pull a form instance out of the session, stored from a
            // previous workflow phase
            
            if (handlerDescriptor.getWorkflowName() != null && !handlerDescriptor.initiatesWorkflow()) {
                formInstance = session.getAttribute(handlerDescriptor.getWorkflowName() + WORKFLOW_FORM_SUFFIX);
            }

            // Create a form instance
            
            if (formInstance == null) {
                formInstance = formFactory.createFormInstance(servletContext, handlerDescriptor);

                if (handlerDescriptor.getFormClass() != null && formInstance == null) {
                    throw new FormException(String.format("Unable to create instance of \"%s\" with \"%s\"",
                            handlerDescriptor.getFormClass().getName(),
                            handlerFactory.getClass().getName()));
                }
            }

            // Populate the form if necessary.  If the handler method only has one additional argument
            // beyond the HandlerContext, it is assumed that it will be a form object.
            
            if (formInstance != null) {
                if (handlerDescriptor.initiatesWorkflow()) {
                    session.setAttribute(handlerDescriptor.getWorkflowName() + WORKFLOW_FORM_SUFFIX, formInstance);
                }

                if (lifecycleHooks.beforeHandlingRequest(handlerDescriptor, formInstance, routingContext)) {
                    return;
                }

                try {
                    
                    // Populate a form and converts it into the target types if they can be 
                    // described by the standard suite of converters out of the agave.conversion
                    // package
                    
                    FormPopulator formPopulator = new RequestParameterFormPopulator(request);
                    formPopulator.populate(formInstance);
                    
                    if (RequestUtils.isMultipart(request)) {
                        formPopulator = new RequestPartFormPopulator<Object>((MultipartRequest<Object>) request);
                        formPopulator.populate(formInstance);
                    }
                    
                    formPopulator = new URIParamFormPopulator(request, handlerDescriptor, uriParams);
                    formPopulator.populate(formInstance);
                } catch (NoSuchMethodException ex) {
                    throw new FormException(ex);
                } catch (IllegalAccessException ex) {
                    throw new FormException(ex);
                } catch (InvocationTargetException ex) {
                    throw new FormException(ex.getCause());
                } catch (InstantiationException ex) {
                    throw new FormException(ex);
                } catch (AgaveConversionException ex) {
                    throw new FormException(ex);
                }

                if (lifecycleHooks.afterInitializingForm(handlerDescriptor, formInstance, routingContext)) {
                    return;
                }
            }
            
            // If no form was found, attempt to supply arguments by taking the parameterized values 
            // from either the URI path or the request params.  URI params override request params.
            
            LinkedHashMap<String, Object> arguments = null;
            List<ParamDescriptor> paramDescriptors = handlerDescriptor.getParamDescriptors();
            
            if (formInstance == null && !paramDescriptors.isEmpty()) {
                
                // A LinkedHashMap is used because the iteration order will match the arguments that
                // the handler method is expecting.  Reinsertion into the map is negligible
                
                arguments = new LinkedHashMap<String, Object>();
                
                // Establish the order of the parameter so the params can be looked up
                
                for (ParamDescriptor paramDescriptor : paramDescriptors) {
                    String value = uriParams.get(paramDescriptor.getName());
                    
                    if (value == null) {
                		value = request.getParameter(paramDescriptor.getName());
                    }
                    
                    arguments.put(paramDescriptor.getName(), null);
                }
                
                // Now that the argument order has been established, populate
                // the actual values
                
                MapPopulator argumentPopulator = new MapPopulatorImpl(request, uriParams, handlerDescriptor);
                
                try {
                    argumentPopulator.populate(arguments);
                } catch (AgaveConversionException ex) {
                    throw new FormException(ex);
                }
            }

            Object handlerInstance = null;

            // Attempt to pull a handler from a previous workflow phase out of
            // the session
            
            if (handlerDescriptor.getWorkflowName() != null && !handlerDescriptor.initiatesWorkflow()) {
                handlerInstance = session.getAttribute(handlerDescriptor.getWorkflowName() + WORKFLOW_HANDLER_SUFFIX);
            }

            // Create a handler
            
            if (handlerInstance == null) {
                handlerInstance = handlerFactory.createHandlerInstance(servletContext, handlerDescriptor);

                if (handlerInstance == null) {
                    throw new HandlerException(String.format("Unable to create instance of \"%s\" with \"%s\"",
                            handlerDescriptor.getHandlerClass().getName(), handlerFactory.getClass().getName()));
                }
            }

            // Initiate a new workflow if necessary
            
            if (handlerDescriptor.initiatesWorkflow()) {
                session.setAttribute(handlerDescriptor.getWorkflowName() + WORKFLOW_HANDLER_SUFFIX, handlerInstance);
            }

            if (lifecycleHooks.beforeHandlingRequest(handlerDescriptor, handlerInstance, routingContext)) {
                return;
            }

            Object result = null;

            // Invoke the handler method, by either supplying a context and a form
            // instance, a context and a string of named parameters, or a single
            // HandlerContext
            
            try {
                if (formInstance != null) {
                    if (handlerDescriptor.getHandlerMethod().getReturnType() != null) {
                        result = handlerDescriptor.getHandlerMethod().invoke(handlerInstance, routingContext, formInstance);
                    } else {
                        handlerDescriptor.getHandlerMethod().invoke(handlerInstance, routingContext, formInstance);
                    }
                } else if (arguments != null) {
                    Object[] actualArguments = new Object[arguments.size() + 1];
                    
                    int i = 0;
                    
                    actualArguments[i++] = routingContext;
                    
                    for (String name : arguments.keySet()) {
                        actualArguments[i++] = arguments.get(name);
                    }
                    
                    if (handlerDescriptor.getHandlerMethod().getReturnType() != null) {
                        result = handlerDescriptor.getHandlerMethod().invoke(handlerInstance, actualArguments);
                    } else {
                        handlerDescriptor.getHandlerMethod().invoke(handlerInstance, actualArguments);
                    }
                } else {
                    if (handlerDescriptor.getHandlerMethod().getReturnType() != null) {
                        result = handlerDescriptor.getHandlerMethod().invoke(handlerInstance, routingContext);
                    } else {
                        handlerDescriptor.getHandlerMethod().invoke(handlerInstance, routingContext);
                    }
                }
            } catch (InvocationTargetException ex) {
                if (ex.getCause() instanceof AgaveWebException) {
                    logRequestInformation(request);
                    throw (AgaveWebException) ex.getCause();
                } else if (ex.getCause() instanceof IOException) {
                    logRequestInformation(request);
                    throw (IOException) ex.getCause();
                } else if (ex.getCause() instanceof RuntimeException) {
                    logRequestInformation(request);
                    throw (RuntimeException) ex.getCause();
                } else {
                    logRequestInformation(request);
                    throw new HandlerException(ex.getMessage(), ex.getCause());
                }
            } catch (IllegalAccessException ex) {
                logRequestInformation(request); 
                throw new HandlerException(handlerDescriptor, ex);
            }

            // Complete a workflow and flushes the referenced attributes from
            // the session
            
            if (handlerDescriptor.completesWorkflow()) {
                session.removeAttribute(handlerDescriptor.getWorkflowName() + WORKFLOW_HANDLER_SUFFIX);
                session.removeAttribute(handlerDescriptor.getWorkflowName() + WORKFLOW_FORM_SUFFIX);
            }

            if (handlerDescriptor.getHandlerMethod().getReturnType() != null && result != null && !response.isCommitted()) {
                for (ResultProcessor resultProcessor : resultProcessors) {
                    if (resultProcessor.canProcessResult(result, routingContext, handlerDescriptor)) {
                        resultProcessor.process(result, routingContext, handlerDescriptor);
                        break;
                    }
                }
            }
            
            if (lifecycleHooks.afterHandlingRequest(handlerDescriptor, handlerInstance, routingContext)) {
                return;
            }
        } else {
            chain.doFilter(req, resp);
        }
    }
    
    private void logRequestInformation(HttpServletRequest request) {
        LOGGER.log(Level.INFO, "Remote details for exception: {0}@{1} ({2}:{3,number,#})", new Object[] {
                request.getRemoteUser(),
                request.getRemoteHost(),
                request.getRemoteAddr(),
                request.getRemotePort()});
    }
    
    protected HttpServletRequest wrapMultipartRequest(HttpServletRequest request) throws Exception {
        return new DefaultMultipartRequest<File>(request, new FileMultipartParser());
    }

    public FilterConfig getFilterConfig() {
        return filterConfig;
    }
    
    public Config getConfig() {
        return config;
    }
    
    public RequestMatcher getRequestMatcher() {
        return requestMatcher;
    }

    public HandlerFactory getHandlerFactory() {
        return handlerFactory;
    }

    public FormFactory getFormFactory() {
        return formFactory;
    }

    public File getClassesDirectory() {
        return classesDirectory;
    }
}
