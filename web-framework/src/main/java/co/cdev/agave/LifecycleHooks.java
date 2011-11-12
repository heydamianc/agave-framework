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
import java.net.URI;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import co.cdev.agave.internal.HandlerMethodDescriptor;

/**
 * <p>
 * Hooks into Agave's lifecycle.  Each method is executed by the {@link AgaveFilter} prior to
 * doing some task.  If the return value of the hook is true, then execution of the
 * {@link AgaveFilter} is halted and execution returns.
 * </p>
 *
 * <p>
 * The default implementation of this is {@link DefaultLifecycleHooks}, but you can override it
 * by specifying an initialization param to the {@link AgaveFilter}.  An example of this is:
 * </p>
 * 
 * <pre>&lt;web-app&gt;
 * ...
 * &lt;filter&gt;
 *   &lt;filter-name>AgaveFilter&lt;/filter-name&gt;
 *   &lt;filter-class>agave.AgaveFilter&lt;/filter-class&gt;
 *   &lt;init-param&gt;
 *     &lt;param-name&gt;lifecycleHooks&lt;/param-name&gt;
 *     &lt;param-value&gt;com.domain.package.LifecycleHooks&lt;/param-value&gt;
 *   &lt;/init-param&gt;
 * &lt;/filter&gt;
 * ...
 * &lt;/web-app&gt;</pre>
 *
 * <p>
 * Note that only a single init-param value is supported, so there is no way to have multiple
 * {@code InstanceCreator}s, unless the value named by the parameter fronts multiple others.
 * This is intentional, and was designed to be this way so that the conceptual overhead of using
 * Agave is shallow.
 * </p>
 * 
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public interface LifecycleHooks {

    /**
     * Executed by the {@link AgaveFilter} prior to each potential handler is discovered.
     *
     * @param potentalHandlerClassFile The actual {@code File} object that a potential handler
     *		  class is stored in.  It is potentially a handler class because at this point, it
     *		  has not been inspected and could just be a regular file (or even a random resource).
     * @return a truth value that indicates whether execution should halt.  See the class-level
     *         description for more information.
     * @throws ServletException
     * @throws IOException
     */
    public boolean beforeHandlerIsDiscovered(File potentalHandlerClassFile)
            throws ServletException, IOException;

    /**
     * Executed by {@link AgaveFilter} after a handler has been discovered.
     *
     * @param descriptor the generated {@link HandlerDescriptor}
     * @param servletContext the {@code ServletContext}
     * @return a truth value that indicates whether execution should halt.  See the class-level
     *         description for more information.
     * @throws ServletException
     * @throws IOException
     */
    public boolean afterHandlerIsDiscovered(HandlerMethodDescriptor descriptor,
            ServletContext servletContext) throws ServletException, IOException;

    /**
     * Executed by the {@link AgaveFilter} before actually performing the work of filtering the
     * request.
     *
     * @param descriptor the generated {@link HandlerDescriptor}
     * @param context the handler context
     * @return a truth value that indicates whether execution should halt.  See the class-level
     *         description for more information.
     * @throws ServletException
     * @throws IOException
     */
    public boolean beforeFilteringRequest(HandlerMethodDescriptor descriptor,
            HandlerContext context) throws ServletException, IOException;

    /**
     * Executed by the {@link AgaveFilter} before reading request parameters and URI parameters
     * and initializing form values.
     *
     * @param descriptor the generated {@link HandlerDescriptor}
     * @param formInstance the instance of the form object
     * @param context the handler context
     * @return a truth value that indicates whether execution should halt.  See the class-level
     *         description for more information.
     * @throws ServletException
     * @throws IOException
     */
    public boolean beforeInitializingForm(HandlerMethodDescriptor descriptor,
            Object formInstance, HandlerContext context)
            throws ServletException, IOException;

    /**
     * Executed by the {@link AgaveFilter} after initializing the form with request and URI
     * parameters
     *
     * @param descriptor the generated {@link HandlerDescriptor}
     * @param formInstance the instance of the form object
     * @param context the handler context
     * @return a truth value that indicates whether execution should halt.  See the class-level
     *         description for more information.
     * @throws ServletException
     * @throws IOException
     */
    public boolean afterInitializingForm(HandlerMethodDescriptor descriptor,
            Object formInstance, HandlerContext context)
            throws ServletException, IOException;

    /**
     * Executed by the {@link AgaveFilter} prior to routing execution to the handler method.
     *
     * @param descriptor the generated {@link HandlerDescriptor}
     * @param handlerInstance The instance of the handler class
     * @param context the handler context
     * @return a truth value that indicates whether execution should halt.  See the class-level
     *         description for more information.
     * @throws ServletException
     * @throws IOException
     */
    public boolean beforeHandlingRequest(HandlerMethodDescriptor descriptor,
            Object handlerInstance, HandlerContext context)
            throws ServletException, IOException;

    /**
     * Executed by the {@link AgaveFilter} after execution has been routed through the handler
     * method, and when a destination was not requested.
     *
     * @param descriptor the generated {@link HandlerDescriptor}
     * @param handlerInstance The instance of the handler class
     * @param context the handler context
     * @return a truth value that indicates whether execution should halt.  See the class-level
     *         description for more information.
     * @throws ServletException
     * @throws IOException
     */
    public boolean afterHandlingRequest(HandlerMethodDescriptor descriptor,
            Object handlerInstance, HandlerContext context)
            throws ServletException, IOException;

    /**
     * Executed by the {@link AgaveFilter} after execution has been routed through the handler
     * method, and when a destination was requested.
     *
     * @param descriptor the generated {@link HandlerDescriptor}
     * @param handlerInstance The instance of the handler class
     * @param context the handler context
     * @param destination The destination that the handler method requested
     * @return a truth value that indicates whether execution should halt.  See the class-level
     *         description for more information.
     * @throws ServletException
     * @throws IOException
     */
    public boolean afterHandlingRequest(HandlerMethodDescriptor descriptor,
            Object handlerInstance, Destination destination,
            HandlerContext context) throws ServletException, IOException;

    /**
     * Executed by the {@link AgaveFilter} after execution has been routed through the handler
     * method, and when a resultant URL was requested.
     *
     * @param descriptor the generated {@link HandlerDescriptor}
     * @param handlerInstance The instance of the handler class
     * @param destination the destination (redirect or forward) to end up at
     * @param context the handler context
     * @return a truth value that indicates whether execution should halt.  See the class-level
     *         description for more information.
     * @throws ServletException
     * @throws IOException
     */
    public boolean afterHandlingRequest(HandlerMethodDescriptor descriptor,
            Object handlerInstance, URI destination, HandlerContext context)
            throws ServletException, IOException;
}
