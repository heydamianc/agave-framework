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
package agave.internal;

import agave.BindsParameter;
import agave.BindsRequest;
import agave.BindsResponse;
import agave.ConvertWith;
import agave.conversion.Converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;

/**
 * A descriptor that aggregates information about the handler so that the configured filter can 
 * efficiently route an HTTP request into the handler that this object describes.
 *
 * The specific information that implementing objects of this interface aggregate are:
 *
 * <ul>
 *   <li>{@link agave.internal.URIPattern URIPattern}</li>
 *   <li>Handler Class</li>
 *   <li>
 *      Handler Method
 *      <ul>
 *        <li>
 *          Form Class
 *          <ul>
 *            <li>
 *              Parameter Binders
 *              <ul>
 *                <li>Converters</li>
 *              </ul>
 *            </li>
 *          </ul>
 *        </li>
 *      </ul>
 *   </li>
 *   <li>{@link javax.servlet.http.HttpServletRequest HttpServletRequest} Binder</li>
 *   <li>{@link javax.servlet.http.HttpServletResponse HttpServletResponse} Binder</li>
 * </ul>
 *
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public interface HandlerDescriptor extends Comparable<HandlerDescriptor> {
    
    /**
     * Gets the {@link agave.internal.URIPattern URIPattern} that this {@code HandlerDescriptor} describes.
     */
    public URIPattern getPattern();

    /**
     * Gets the handler class that this {@code HandlerDescriptor} describes.
     */
    public Class<?> getHandlerClass();

    /**
     * Gets the form class that this {@code HandlerDescriptor} describes.
     */
    public Class<?> getFormClass();

    /**
     * Gets the method object that this {@code HandlerDescriptor} describes.
     */
    public Method getHandlerMethod();

    /**
     * Gets the request setter method that this {@code HandlerDescriptor} describes.
     */
    public Method getRequestSetter();

    /**
     * Gets the response setter method that this {@code HandlerDescriptor} describes.
     */
    public Method getResponseSetter();

    /**
     * Gets the collection of parameter setters on the form class that this {@code HandlerDescriptor} describes.
     */
    public Map<String, Method> getParameterSetters();

    /**
     * Gets the collection of parameter converters for the setters on the form class that this
     * {@code HandlerDescriptor} describes.
     */
    public Map<String, Class<? extends Converter<?,?>>> getParameterConverters();

    /**
     * Whether or not this {@code HandlerDescriptor} matches the URI string supplied.  The URI string 
     * should be the result of calling {@code HttpServletRequest.getRequestURI()}.
     */
    public boolean matches(String uri);

    /**
     * Whether or not this {@code HandlerDescriptor} is equivalent to the supplied object.
     */
    public boolean equals(Object that);

    /**
     * Uniquely hashes this {@code HandlerDescriptor}.
     */
    public int hashCode();

}
