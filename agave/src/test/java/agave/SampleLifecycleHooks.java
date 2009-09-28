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

import agave.internal.HandlerDescriptor;
import java.io.File;
import java.net.URI;
import javax.servlet.ServletContext;

/**
 * @author <a href="mailto:damianarrillo@gmail.com">Damian Carrillo</a>
 * @version $Rev$ $Date$
 */
public class SampleLifecycleHooks extends DefaultLifecycleHooks {

    // from init
    
    @Override
    public boolean beforeHandlerIsDiscovered(File potentalHandlerClassFile) {
        return false;
    }
    
    @Override
    public boolean afterHandlerIsDiscovered(HandlerDescriptor descriptor, ServletContext servletContext) {
        servletContext.setAttribute("afterHandlerIsDiscovered", Boolean.TRUE);
        return false;
    }
    
    // from doFilter
    
    @Override
    public boolean beforeFilteringRequest(HandlerDescriptor descriptor, HandlerContext context) {
        context.getServletContext().setAttribute("beforeFilteringRequest", Boolean.TRUE);
        return false;
    }

    @Override
    public boolean afterHandlingRequest(HandlerDescriptor descriptor, Object handlerInstance, HandlerContext context) {
        context.getServletContext().setAttribute("afterHandlingRequest", Boolean.TRUE);
        return false;
    }

    @Override
    public boolean afterHandlingRequest(HandlerDescriptor descriptor, Object handlerInstance,
        Destination destination, HandlerContext context) {
        context.getServletContext().setAttribute("afterHandlingRequest", Boolean.TRUE);
        return false;
    }

    @Override
    public boolean afterHandlingRequest(HandlerDescriptor descriptor, Object handlerInstance,
        URI destination, HandlerContext context) {
        context.getServletContext().setAttribute("afterHandlingRequest", Boolean.TRUE);
        return false;
    }

    @Override
    public boolean afterInitializingForm(HandlerDescriptor descriptor, Object formInstance, HandlerContext context) {
        context.getServletContext().setAttribute("afterInitializingForm", Boolean.TRUE);
        return false;
    }

    @Override
    public boolean beforeHandlingRequest(HandlerDescriptor descriptor, Object handlerInstance, HandlerContext context) {
        context.getServletContext().setAttribute("beforeHandlingRequest", Boolean.TRUE);
        return false;
    }

    @Override
    public boolean beforeInitializingForm(HandlerDescriptor descriptor, Object formInstance, HandlerContext context) {
        context.getServletContext().setAttribute("beforeInitializingForm", Boolean.TRUE);
        return false;
    }

}
