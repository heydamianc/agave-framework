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
package co.cdev.agave.internal;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import co.cdev.agave.HttpMethod;

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
     * Locates annotated handler methods to field requests with
     * @param identifier
     */
    public void locateAnnotatedHandlerMethods(HandlerIdentifier identifier);
    
    /**
     * Gets the {@link agave.internal.URIPattern URIPattern} that this {@code HandlerDescriptor} describes.
     */
    public URIPattern getPattern();

    /**
     * Gets  the {@link agave.HttpMethod HttpMethod} that this {@code HandlerDescriptor} describes.
     */
    public HttpMethod getMethod();
    
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
     * Indicates whether the handler method this {@code HandlerDescriptor} describes initiates the named workflow.
     */
    public boolean initiatesWorkflow();

    /**
     * Indicates whether the handler method this {@code HandlerDescriptor} describes completes the named workflow.
     */
    public boolean completesWorkflow();

    /**
     * Returns the workflow name.
     */
    public String getWorkflowName();

    /**
     * Whether or not this {@code HandlerDescriptor} matches the supplied request.  The URI string 
     * should be the result of calling {@code HttpServletRequest.getRequestURI()}.
     */
    public boolean matches(HttpServletRequest request);

    /**
     * Whether or not this {@code HandlerDescriptor} is equivalent to the supplied object.
     */
    public boolean equals(Object that);

    /**
     * Uniquely hashes this {@code HandlerDescriptor}.
     */
    public int hashCode();

}
