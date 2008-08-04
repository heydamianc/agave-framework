/**
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
package agave;

import agave.internal.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.objectweb.asm.ClassReader;

/**
 * Scans the classes directory of a deployed context for any configured handlers and forwards HTTP requests 
 * to the handlers if they match the requested URI. 
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class AgaveFilter implements Filter {

    private FilterConfig config;
    private ClassEnvironment classEnvironment;
    private HandlerRegistry handlerRegistry;
    
    /**
     * Initializes the {@code AgaveFilter} by scanning for handler classes and populating a 
     * {@link agave.internal.HandlerRegistry HandlerRegistry} with them.  Then, this initializes the 
     * dependency injection container (if any) by instantiation a {@link agave.internal.ClassEnvironment}.
     * @param config the supplied filter configuration object
     * @throws ServletException
     */
    public void init(FilterConfig config) throws ServletException {
        this.config = config;
        try {
            setHandlerRegistry(new HandlerRegistryImpl());
            
            // 1. Scan the /WEB-INF/classes directory for any handlers
            scanClassesDirForHandlers(new File(config.getServletContext().getRealPath("/WEB-INF/classes")));
            
            // 2. initialize the dependency injection container (if any)
            // TODO use a context-param as this or something
            classEnvironment = new SimpleClassEnvironment();
            classEnvironment.initializeEnvironment();
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    public void destroy() {
        config = null;
        handlerRegistry = null;
        classEnvironment = null;
    }

    /**
     * <p>
     * Handles the routing of HTTP requests through the framework. The algorithm used internally is 
     * as follows:
     * </p>
     *
     * <ol>
     *   <li>
     *      {@link agave.internal.ClassEnvironment#createFormInstance Instantiate a form if necessary}
     *      <ol>
     *          <li>{@link agave.internal.ParameterBinder#bindRequestParameters
     *              Bind request parameters if necessary}</a></li>
     *          <li>{@link agave.internal.ParameterBinder#bindURIParameters
     *              Bind URI parameters if necessary}</li>
     *      </ol>
     *    </li>
     *    <li>{@link agave.internal.ClassEnvironment#createHandlerInstance Instantiate a handler}</li> 
     *    <li>Bind the request to the handler if necessary</li>
     *    <li>Bind the response to the handler if necessary</li>
     *    <li>Invoke the handler method with the instantiated form as the only argument</li>
     * </ol>
     *
     * <p>
     * When one of the two supported encoding methods is selected, then this filter will field the HTTP
     * request that was made and will prevent future execution of the filter chain.  If an unsupported 
     * encoding type is requested or if a handler is not configured for the requested URI, this filter will
     * simply continue with execution of the filter chain.  The two encoding types that are supported by 
     * this method are:
     * </p>
     *
     * <ul>
     *   <li><a href="http://www.w3.org/TR/html401/interact/forms.html#h-17.13.4.1">
     *      application/x-www-form-urlencoded</a></li>
     *   <li><a href="http://www.w3.org/TR/html401/interact/forms.html#h-17.13.4.2">
     *      multipart/form-data</a></li>
     * </ul>
     *
     * @param req the Servlet request object; it will be cast to an {@code HttpServletRequest}
     * @param resp the Servlet response object; it will be cast to an {@code HttpServletResponse}
     * @param chain the filter chain this filter is a member of
     * @throws IOException if an I/O error occurs
     * @throws ServletException if a Servlet error occurs
     * @see <a href="http://www.w3.org/TR/html401/interact/forms.html#h-17.13.4">W3C Form Encoding Types</a>
     * @see agave.HandlesRequestsTo
     * @see agave.BindsParameter
     * @see agave.ConvertWith
     * @see agave.BindsRequest
     * @see agave.BindsResponse
     */
    public final void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;
        
        HandlerDescriptor descriptor = handlerRegistry.findMatch(request.getRequestURI());
        if (descriptor != null
            && (MultipartRequestImpl.isMultipart(request) || MultipartRequestImpl.isFormURLEncoded(request))) {

            if (MultipartRequestImpl.isMultipart(request)) {
                request = new MultipartRequestImpl(request);
            }

            // 1. instantiate form and bind parameters

            Object formInstance = null;
            try {
                formInstance = classEnvironment.createFormInstance(descriptor);
                if (formInstance != null) {

                    // 2. Bind parameters if necessary

                    ParameterBinder binder = new ParameterBinderImpl(formInstance, descriptor);
                    binder.bindRequestParameters(request);
                    binder.bindURIParameters(request);

                    if (MultipartRequestImpl.isMultipart(request)) {
                        MultipartReqeust multipartRequest = (MultipartRequest)request;
                        
                    }
                }
            } catch (ClassNotFoundException ex) {
                throw new ServletException("Unable to create a form instance for: " + 
                    descriptor.getFormClass().getName(), ex);
            } catch (InstantiationException ex) {
                throw new ServletException("Unable to create a form instance for: " + 
                    descriptor.getFormClass().getName(), ex);
            } catch (IllegalAccessException ex) {
                throw new ServletException("Unable to create a form instance for: " + 
                    descriptor.getFormClass().getName(), ex);
            }

            // 3. instantiate handler

            Object handlerInstance = null;
            try {
                handlerInstance = classEnvironment.createHandlerInstance(descriptor);
            } catch (ClassNotFoundException ex) {
                throw new ServletException("Unable to create a handler instance for: " + 
                    descriptor.getHandlerClass().getName(), ex);
            } catch (InstantiationException ex) {
                throw new ServletException("Unable to create a handler instance for: " + 
                    descriptor.getHandlerClass().getName(), ex);
            } catch (IllegalAccessException ex) {
                throw new ServletException("Unable to create a handler instance for: " + 
                    descriptor.getHandlerClass().getName(), ex);
            }

            // 4. bind request to handler

            if (descriptor.getRequestSetter() != null) {
                try {
                    descriptor.getRequestSetter().invoke(handlerInstance, request);
                } catch (IllegalAccessException ex) {
                    throw new ServletException("Unable to set request for: " + 
                        descriptor.getHandlerClass().getName(), ex);
                } catch (InvocationTargetException ex) {
                    throw new ServletException("Unable to set request for: " + 
                        descriptor.getHandlerClass().getName(), ex.getCause());
                }
            }

            // 5. bind response to handler

            if (descriptor.getResponseSetter() != null) {
                try {
                    descriptor.getResponseSetter().invoke(handlerInstance, response);
                } catch (IllegalAccessException ex) {
                    throw new ServletException("Unable to set response for: " +
                        descriptor.getHandlerClass().getName(), ex);
                } catch (InvocationTargetException ex) {
                    throw new ServletException("Unable to set response for: " + 
                        descriptor.getHandlerClass().getName(), ex.getCause());
                }
            }

            // 6. invoke handler method

            try {
                if (formInstance != null) {
                    descriptor.getHandlerMethod().invoke(handlerInstance, formInstance);
                } else {
                    descriptor.getHandlerMethod().invoke(handlerInstance);
                }
            } catch (InvocationTargetException ex) {
                if (ex.getCause() instanceof ServletException) {
                    throw (ServletException)ex.getCause(); 
                } else if (ex.getCause() instanceof IOException) {
                    throw (IOException)ex.getCause();
                } else if (ex.getCause() instanceof RuntimeException) {
                    throw (RuntimeException)ex.getCause();
                } else {
                    throw new ServletException(ex.getCause());
                }
            } catch (IllegalAccessException ex) {
                throw new ServletException("Unable to invoke handler method: " + 
                    descriptor.getHandlerMethod().getName() + " on class: " + 
                    descriptor.getHandlerClass().getName(), ex);
            }
        } else {
            chain.doFilter(req, resp);
        }
    }

    /**
     * Scans the supplied directory for handlers.  Handlers in turn are inspected and have a 
     * {@link agave.internal.HandlerDescriptor HandlerDescriptor} generated for them which then gets 
     * registered in the {@link agave.internal.HandlerRegistry HandlerRegistry} as handlers are found.
     * @param root the root directory to scan files for, typically {@code /WEB-INF/classes}
     */
    protected void scanClassesDirForHandlers(File root) throws FileNotFoundException, IOException,
        ClassNotFoundException {
        if (root != null && root.canRead()) {
            for (File node : root.listFiles()) {
                if (node.isDirectory()) {
                    scanClassesDirForHandlers(node);
                } else if (node.isFile()) {
                    ClassReader classReader = new ClassReader(new FileInputStream(node));
                    Collection<HandlerIdentifier> handlerIdentifiers = new ArrayList<HandlerIdentifier>();
                    classReader.accept(new HandlerScanner(handlerIdentifiers), ClassReader.SKIP_CODE);
                    for (HandlerIdentifier handlerIdentifier : handlerIdentifiers) {
                        handlerRegistry.addDescriptor(new HandlerDescriptorImpl(handlerIdentifier));
                    }
                }
            }
        }
    }

    protected void setConfig(FilterConfig config) {
        this.config = config;
    }

    protected FilterConfig getConfig() {
        return config;
    }
    
    protected void setHandlerRegistry(HandlerRegistry handlerRegistry) {
        this.handlerRegistry = handlerRegistry;
    }

    protected HandlerRegistry getHandlerRegistry() {
        return handlerRegistry;
    }
}
