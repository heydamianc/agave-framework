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

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;

import co.cdev.agave.configuration.HandlerDescriptor;
import co.cdev.agave.configuration.RoutingContext;


/**
 * Stub implementations of all the lifecycle hooks.  All methods return false, indicating that
 * execution of the {@link AgaveFilter} should continue.
 * 
 * @author <a href="mailto:damianarrillo@gmail.com">Damian Carrillo</a>
 */
public class DefaultLifecycleHooks implements LifecycleHooks {
    
    @Override
    public boolean beforeFilteringRequest(HandlerDescriptor descriptor,
            RoutingContext context) throws ServletException, IOException {
        return false;
    }

    @Override
    public boolean beforeInitializingForm(HandlerDescriptor descriptor,
            Object formInstance, RoutingContext context)
            throws ServletException, IOException {
        return false;
    }

    @Override
    public boolean afterInitializingForm(HandlerDescriptor descriptor,
            Object formInstance, RoutingContext context)
            throws ServletException, IOException {
        return false;
    }

    @Override
    public boolean beforeHandlingRequest(HandlerDescriptor descriptor,
            Object handlerInstance, RoutingContext context)
            throws ServletException, IOException {
        return false;
    }

    @Override
    public boolean afterHandlingRequest(HandlerDescriptor descriptor,
            Object handlerInstance, RoutingContext context)
            throws ServletException, IOException {
        return false;
    }

    @Override
    public boolean afterHandlingRequest(HandlerDescriptor descriptor,
            Object handlerInstance, Destination destination,
            RoutingContext context) throws ServletException, IOException {
        return false;
    }

    @Override
    public boolean afterHandlingRequest(HandlerDescriptor descriptor,
            Object handlerInstance, URI destination, RoutingContext context)
            throws ServletException, IOException {
        return false;
    }
}
