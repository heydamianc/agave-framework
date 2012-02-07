/*
 * Copyright (c) 2008, Damian Carrillo
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of 
 *     conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of 
 *     conditions and the following disclaimer in the documentation and/or other materials 
 *     provided with the distribution.
 *   * Neither the name of the copyright holder's organization nor the names of its contributors 
 *     may be used to endorse or promote products derived from this software without specific 
 *     prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package co.cdev.agave;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import co.cdev.agave.configuration.HandlerDescriptor;
import co.cdev.agave.configuration.ParamDescriptor;
import co.cdev.agave.configuration.RoutingContext;
import co.cdev.agave.conversion.AgaveConversionException;
import co.cdev.agave.exception.AgaveWebException;
import co.cdev.agave.exception.DestinationException;
import co.cdev.agave.exception.FormException;
import co.cdev.agave.exception.HandlerException;
import co.cdev.agave.internal.DefaultMultipartRequest;
import co.cdev.agave.internal.DestinationImpl;
import co.cdev.agave.internal.FileMultipartParser;
import co.cdev.agave.internal.FormFactoryImpl;
import co.cdev.agave.internal.FormPopulator;
import co.cdev.agave.internal.HandlerFactoryImpl;
import co.cdev.agave.internal.MapPopulator;
import co.cdev.agave.internal.MapPopulatorImpl;
import co.cdev.agave.internal.RequestMatcher;
import co.cdev.agave.internal.RequestMatcherImpl;
import co.cdev.agave.internal.RequestParameterFormPopulator;
import co.cdev.agave.internal.RequestPartFormPopulator;
import co.cdev.agave.internal.URIParamFormPopulator;

public class AgaveFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(AgaveFilter.class.getName());
    private static final String WORKFLOW_HANDLER_SUFFIX = "-handler";
    private static final String WORKFLOW_FORM_SUFFIX = "-form";
    
    private FilterConfig filterConfig;
    private ConfigGenerator configGenerator;
    private Config config;
    private LifecycleHooks lifecycleHooks;
    private File classesDirectory;
    private HandlerFactory handlerFactory;
    private FormFactory formFactory;
    private RequestMatcher requestMatcher;

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
    
    protected ConfigGenerator provideConfigGenerator(FilterConfig filterConfig)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        ConfigGenerator configGenerator = null;
        String configReaderParameter = filterConfig.getInitParameter("configReader");

        if (configReaderParameter != null) {
            configGenerator = (ConfigGenerator) Class.forName(configReaderParameter).newInstance();
        } else {
            configGenerator = new ConfigGeneratorImpl(classesDirectory);
        }

        return configGenerator;
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
    
    protected RequestMatcher provideRequestMatcher(FilterConfig filterConfig)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        RequestMatcher requestMatcher = null;

        String requestMatcherParameter = filterConfig.getInitParameter("requestMatcher");

        if (requestMatcherParameter != null) {
            requestMatcher = (RequestMatcher) Class.forName(requestMatcherParameter).newInstance();
        } else {
            requestMatcher = new RequestMatcherImpl(config);
        }

        return requestMatcher;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;

        try {
            classesDirectory = provideClassesDirectory(filterConfig);
            configGenerator = provideConfigGenerator(filterConfig);
            config = configGenerator.generateConfig();
            
            lifecycleHooks = provideLifecycleHooks(filterConfig);
            requestMatcher = provideRequestMatcher(filterConfig);
            handlerFactory = provideHandlerFactory(filterConfig);
            handlerFactory.initialize();
            formFactory = provideFormFactory(filterConfig);            
            formFactory.initialize();
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    /**
     * Destroys this filter.
     */
    @Override
    public void destroy() {
        classesDirectory = null;
        configGenerator = null;
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
                    throw (AgaveWebException) ex.getCause();
                } else if (ex.getCause() instanceof IOException) {
                    throw (IOException) ex.getCause();
                } else if (ex.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) ex.getCause();
                } else {
                    throw new HandlerException(ex.getMessage(), ex.getCause());
                }
            } catch (IllegalAccessException ex) {
                throw new HandlerException(handlerDescriptor, ex);
            }

            // Complete a workflow and flushes the referenced attributes from
            // the session
            if (handlerDescriptor.completesWorkflow()) {
                session.removeAttribute(handlerDescriptor.getWorkflowName() + WORKFLOW_HANDLER_SUFFIX);
                session.removeAttribute(handlerDescriptor.getWorkflowName() + WORKFLOW_FORM_SUFFIX);
            }

            // Determine a destination
            if (handlerDescriptor.getHandlerMethod().getReturnType() != null && result != null && !response.isCommitted()) {
                URI uri = null;
                boolean redirect = false;

                if (result instanceof DestinationImpl) {
                    Destination destination = (Destination) result;

                    if (lifecycleHooks.afterHandlingRequest(handlerDescriptor, handlerInstance, destination, routingContext)) {
                        return;
                    }

                    try {
                        uri = new URI(null, destination.encode(filterConfig.getServletContext()), null);
                        if (destination.getRedirect() == null) {
                            if (HttpMethod.POST.name().equalsIgnoreCase(request.getMethod())) {
                                redirect = true;
                            }
                        } else {
                            redirect = destination.getRedirect();
                        }
                    } catch (URISyntaxException ex) {
                        throw new DestinationException(destination, handlerDescriptor, ex);
                    }
                } else if (result instanceof URI) {
                    uri = (URI) result;

                    if (lifecycleHooks.afterHandlingRequest(handlerDescriptor, handlerInstance, uri, routingContext)) {
                        return;
                    }

                    redirect = true;
                } else {
                    throw new DestinationException(String.format("Invalid destination type (%s); expected either %s or %s",
                            result.getClass().getName(), Destination.class.getName(), URI.class.getName()));
                }

                if (redirect) {
                    String location = uri.toASCIIString();
                    if (location.startsWith("/")) { // absolute URI
                        location = request.getContextPath() + location;
                    }
                    response.sendRedirect(location);
                } else {
                    request.getRequestDispatcher(uri.toASCIIString()).forward(request, response);
                }
            } else {
                if (lifecycleHooks.afterHandlingRequest(handlerDescriptor, handlerInstance, routingContext)) {
                    return;
                }
            }
        } else {
            chain.doFilter(req, resp);
        }
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
    
    public ConfigGenerator getConfigGenerator() {
        return configGenerator;
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
