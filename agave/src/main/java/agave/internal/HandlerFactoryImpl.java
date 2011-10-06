/*
 * Copyright (c) 2011, Damian Carrillo
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
package agave.internal;

import javax.servlet.ServletContext;

import agave.AgaveFilter;
import agave.HandlerFactory;
import agave.exception.HandlerException;

/**
 * 
 * @author <a href="damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class HandlerFactoryImpl implements HandlerFactory {

	/**
	 * Initializes this {@code HandlerFactory} if necessary. This method is
	 * called in the {@link AgaveFilter#init(javax.servlet.FilterConfig)}
	 * method, so it is an effective way to set up a mechanism for providing
	 * dependency injection or hooking into an IOC library.
	 */
	public void initialize() {
		// do nothing
	}

	/**
	 * Creates a new instance of a handler object for the handler class
	 * specified in the supplied descriptor by calling its default constructor.
	 * 
	 * @param descriptor
	 *            the handler descriptor that describes which handler to
	 *            instantiate.
	 * @throws FormError
	 *             when a handler instance failed to be instantiated
	 */
	public Object createHandlerInstance(ServletContext servletContext,
			HandlerDescriptor descriptor) throws HandlerException {
		Object handlerInstance = null;
		if (descriptor.getHandlerClass() != null) {
			try {
				handlerInstance = descriptor.getHandlerClass().newInstance();
			} catch (InstantiationException ex) {
				throw new HandlerException(descriptor, ex);
			} catch (IllegalAccessException ex) {
				throw new HandlerException(descriptor, ex);
			}
		}
		return handlerInstance;
	}

}
