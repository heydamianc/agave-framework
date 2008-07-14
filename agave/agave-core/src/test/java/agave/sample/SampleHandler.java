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
package agave.sample;

import agave.BindsRequest;
import agave.BindsResponse;
import agave.HandlesRequestsTo;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SampleHandler {
    
    private HttpServletRequest request;
    private HttpServletResponse response;

    
    @HandlesRequestsTo("/login")
    public void login(LoginForm loginForm) throws ServletException, IOException {
        if ("damian".equals(loginForm.getUsername()) && "password".equals(loginForm.getPassword())) {
            request.setAttribute("loggedIn", Boolean.TRUE);
        } else {
            request.setAttribute("loggedIn", Boolean.FALSE);
        }
    }

    @HandlesRequestsTo("/aliased")
    public void aliased(AliasedForm aliasedForm) throws ServletException, IOException {
    }

    @HandlesRequestsTo("/uri-params/${username}/${password}/")
    public void uriParams(LoginForm loginForm) throws ServletException, IOException {
        request.setAttribute("username", loginForm.getUsername());
        request.setAttribute("password", loginForm.getPassword());
    }

    @HandlesRequestsTo("/throws/nullPointerException")
    public void throwsNullPointerException(LoginForm loginForm) throws ServletException, IOException {
        throw new NullPointerException();
    }

    @HandlesRequestsTo("/throws/ioException")
    public void throwsIOException(LoginForm loginForm) throws ServletException, IOException {
        throw new IOException();
    }


    @HandlesRequestsTo("/lacks/form")
    public void lacksForm() throws ServletException, IOException {
        request.setAttribute("noErrors", Boolean.TRUE);
    }
    
    @BindsRequest
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
    
    @BindsResponse
    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

}
