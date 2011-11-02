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
package co.cdev.agave.guice;

import co.cdev.agave.Destination;
import co.cdev.agave.HandlerContext;
import co.cdev.agave.LifecycleHooks;
import co.cdev.agave.internal.HandlerDescriptor;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Aggregates handler classes into the set supplied in the constructor.  This class functions as a 
 * wrapper around the supplied lifecycle hooks so that operation can continue even if the the 
 * web.xml names another lifecycle hooks object.
 *
 * @author <a href="damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class HandlerLifecycleHooks implements LifecycleHooks {

    private final Set<Class<?>> handlerClasses;
    private final LifecycleHooks wrappedHooks;
    
    public HandlerLifecycleHooks(Set<Class<?>> handlerClasses, LifecycleHooks lifecycleHooks) {
        this.handlerClasses = handlerClasses;
        this.wrappedHooks = lifecycleHooks;
    }
    
    @Override
    public boolean beforeHandlerIsDiscovered(File potentalHandlerClassFile)
            throws ServletException, IOException {
        return wrappedHooks == null ? false : wrappedHooks.beforeHandlerIsDiscovered(potentalHandlerClassFile);
    }

    @Override
    public boolean afterHandlerIsDiscovered(HandlerDescriptor descriptor,
            ServletContext servletContext) throws ServletException, IOException {
        
        // Save the handler class so that it can be configured later
        handlerClasses.add(descriptor.getHandlerClass());
        
        return wrappedHooks == null ? false : wrappedHooks.afterHandlerIsDiscovered(descriptor, servletContext);
    }

    @Override
    public boolean beforeFilteringRequest(HandlerDescriptor descriptor,
            HandlerContext context) throws ServletException, IOException {
        return wrappedHooks == null ? false : wrappedHooks.beforeFilteringRequest(descriptor, context);
    }

    @Override
    public boolean beforeInitializingForm(HandlerDescriptor descriptor,
            Object formInstance, HandlerContext context)
            throws ServletException, IOException {
        return wrappedHooks == null ? false : wrappedHooks.beforeInitializingForm(descriptor, formInstance, context);
    }

    @Override
    public boolean afterInitializingForm(HandlerDescriptor descriptor,
            Object formInstance, HandlerContext context)
            throws ServletException, IOException {
        return wrappedHooks == null ? false : wrappedHooks.afterInitializingForm(descriptor, formInstance, context);
    }

    @Override
    public boolean beforeHandlingRequest(HandlerDescriptor descriptor,
            Object handlerInstance, HandlerContext context)
            throws ServletException, IOException {
        return wrappedHooks == null ? false : wrappedHooks.beforeHandlingRequest(descriptor, handlerInstance, context);
    }

    @Override
    public boolean afterHandlingRequest(HandlerDescriptor descriptor,
            Object handlerInstance, HandlerContext context)
            throws ServletException, IOException {
        return wrappedHooks == null ? false : wrappedHooks.afterHandlingRequest(descriptor, handlerInstance, context);
    }

    @Override
    public boolean afterHandlingRequest(HandlerDescriptor descriptor,
            Object handlerInstance, Destination destination,
            HandlerContext context) throws ServletException, IOException {
        return wrappedHooks == null ? false : wrappedHooks.afterHandlingRequest(descriptor, handlerInstance, destination, context);
    }

    @Override
    public boolean afterHandlingRequest(HandlerDescriptor descriptor,
            Object handlerInstance, URI destination, HandlerContext context)
            throws ServletException, IOException {
        return wrappedHooks == null ? false : wrappedHooks.afterHandlingRequest(descriptor, handlerInstance, destination, context);
    }
    
}
