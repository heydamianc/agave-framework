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
package agave;

import agave.exception.FormException;
import agave.exception.HandlerException;
import agave.internal.HandlerDescriptor;

/**
 * Creates instances of handlers and forms for use by the {@link AgaveFilter}.  The default implementation
 * of this is {@link agave.internal.ReflectionInstanceCreator}, but you can override it by specifying an
 * initialization param to the {@link AgaveFilter}.  An example of this is:
 * 
 * <pre>&lt;web-app&gt;
 * ...
 * &lt;filter&gt;
 *   &lt;filter-name>AgaveFilter&lt;/filter-name&gt;
 *   &lt;filter-class>agave.AgaveFilter&lt;/filter-class&gt;
 *   &lt;init-param&gt;
 *     &lt;param-name&gt;instanceCreator&lt;/param-name&gt;
 *     &lt;param-value&gt;com.domain.package.DefaultInstanceFactory&lt;/param-value&gt;
 *   &lt;/init-param&gt;
 * &lt;/filter&gt;
 * ...
 * &lt;/web-app&gt;</pre>
 * 
 * Note that only a single value is supported, so there is no way to have multiple {@code LifecycleHooks}s, unless
 * the value named by the parameter fronts multiple others.  This is intentional, and was designed to be this way so
 * that the conceptual overhead of using Agave is shallow.
 * 
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public interface InstanceCreator {

	/**
	 * Initializes this {@code InstanceCreator} if necessary.  This method is called in the 
	 * {@link AgaveFilter#init(javax.servlet.FilterConfig)} method, so it is an effective way to 
	 * set up a mechanism for providing dependency injection or hooking into an IOC library.
	 */
	public void initialize(); 

	/**
	 * Creates instances of form objects.
	 * 
	 * @param descriptor The {@link HandlerDescriptor} of the request handler the form object is being created for
	 * @return an instance of the described form class
	 * @throws FormException if construction fails
	 */
    public Object createFormInstance(HandlerDescriptor descriptor) throws FormException;

    /**
     * Creates instances of handler objects. 
     * 
     * @param descriptor The {@link HandlerDescriptor} that describes the request handler
     * @return an instance of the described request handler
     * @throws HandlerException if construction fails
     */
    public Object createHandlerInstance(HandlerDescriptor descriptor) throws HandlerException;

}
