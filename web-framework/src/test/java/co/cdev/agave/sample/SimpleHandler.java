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
package co.cdev.agave.sample;

import java.io.IOException;

import javax.servlet.ServletException;

import co.cdev.agave.Route;
import co.cdev.agave.configuration.RoutingContext;

/**
 * A simple example of a handler class that fields requests to a single URL.  This class is 
 * instantiated whenever the AgaveFilter determines that it will be fielding a request, so 
 * multiple requests will create multiple handler instances.  It does nothing useful, and is only 
 * used to illustrate the way that handler methods are invoked whenever their designated URL 
 * is matched.
 */
public class SimpleHandler {

    /**
     * Handles requests to the URL http://localhost:8080/webapp/test1. Note that it takes an optional
     * form as an argument, but never uses it.
     *
     * @param context the context that the handler runs under; contains the request, response, 
     *                servlet context, and session objects
     * @param loginForm an unused form object
     */        
    @Route("/test1")
    public void test1(RoutingContext context, LoginForm loginForm) throws ServletException, IOException {
    	context.getRequest().setAttribute("x", "x");
    	context.getResponse().setStatus(400);
    }
   
}
