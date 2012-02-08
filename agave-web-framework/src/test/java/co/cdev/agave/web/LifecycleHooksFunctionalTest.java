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
package co.cdev.agave.web;

import java.util.HashMap;
import java.util.Map;
import org.jmock.Expectations;
import org.junit.Test;

import co.cdev.agave.web.AgaveFilter;

/**
 * @author <a href="mailto:damianarrillo@gmail.com">Damian Carrillo</a>
 * @version $Rev$ $Date$
 */
public class LifecycleHooksFunctionalTest extends AbstractFunctionalTest {
    
    @Test
    public void testInit() throws Exception {
        AgaveFilter filter = createSilentAgaveFilter();
        final Map<String, String[]> parameterMap = new HashMap<String, String[]>();

        emulateServletContainer(parameterMap);

        context.checking(new Expectations() {{
            allowing(servletContext).setAttribute("beforeHandlerIsDiscovered", Boolean.TRUE);
            allowing(servletContext).setAttribute("afterHandlerIsDiscovered", Boolean.TRUE);
        }});

        filter.init(filterConfig);
    }
    
    @Test
    public void testDoFilter() throws Exception {
        AgaveFilter filter = createSilentAgaveFilter();
        final Map<String, String[]> parameterMap = new HashMap<String, String[]>();

        emulateServletContainer(parameterMap);

        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/login"));
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
            
            // from init
            allowing(servletContext).setAttribute("beforeHandlerIsDiscovered", Boolean.TRUE);
            allowing(servletContext).setAttribute("afterHandlerIsDiscovered", Boolean.TRUE);
            
            // from doFilter
            allowing(servletContext).setAttribute("beforeFilteringRequest", Boolean.TRUE);
            allowing(servletContext).setAttribute("afterHandlingRequest", Boolean.TRUE);
            allowing(servletContext).setAttribute("afterInitializingForm", Boolean.TRUE);
            allowing(servletContext).setAttribute("afterSettingRequest", Boolean.TRUE);
            allowing(servletContext).setAttribute("afterSettingResponse", Boolean.TRUE);
            allowing(servletContext).setAttribute("afterSettingServletContext", Boolean.TRUE);
            allowing(servletContext).setAttribute("beforeHandlingRequest", Boolean.TRUE);
            allowing(servletContext).setAttribute("beforeInitializingForm", Boolean.TRUE);
            allowing(servletContext).setAttribute("beforeSettingRequest", Boolean.TRUE);
            allowing(servletContext).setAttribute("beforeSettingResponse", Boolean.TRUE);
            allowing(servletContext).setAttribute("beforeSettingServletContext", Boolean.TRUE);
            
            allowing(request).setAttribute("loggedIn", Boolean.FALSE);
            allowing(response).setStatus(400);
        }});

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }
    
}
