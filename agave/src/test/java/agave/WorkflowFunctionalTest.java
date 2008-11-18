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

import java.util.HashMap;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import agave.sample.WorkflowForm;
import agave.sample.WorkflowHandler;

/**
 * @author <a href="mailto:damianarrillo@gmail.com">Damian Carrillo</a>
 */
public class WorkflowFunctionalTest extends AbstractFunctionalTest {

    @Before
    public void setup() throws Exception {
        super.setup();
        context.checking(new Expectations() {{
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
        }}); 
    }
    
    @Test
    public void testInitialize() throws Exception {
        AgaveFilter filter = createSilentAgaveFilter();

        emulateServletContainer(new HashMap<String, String[]>());

        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/wizard/step1/"));
            
            one(session).setAttribute("wizard-handler", new WorkflowHandler());
            one(session).setAttribute("wizard-form", new WorkflowForm());
        }});

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }
    
    @Test
    public void testResume() throws Exception {
        AgaveFilter filter = createSilentAgaveFilter();
        
        final WorkflowHandler handler = new WorkflowHandler();
        final WorkflowForm form = new WorkflowForm();
        
        emulateServletContainer(new HashMap<String, String[]>());

        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/wizard/step2/"));
            
            one(session).getAttribute("wizard-handler"); will(returnValue(handler));
            one(session).getAttribute("wizard-form"); will(returnValue(form));
        }});

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }
    
    @Test
    public void testCompletion() throws Exception {
        AgaveFilter filter = createSilentAgaveFilter();
        
        final WorkflowHandler handler = new WorkflowHandler();
        final WorkflowForm form = new WorkflowForm();
        
        emulateServletContainer(new HashMap<String, String[]>());

        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/wizard/step3/"));
            
            one(session).getAttribute("wizard-handler"); will(returnValue(handler));
            one(session).getAttribute("wizard-form"); will(returnValue(form));
            
            one(session).removeAttribute("wizard-handler");
            one(session).removeAttribute("wizard-form");
        }});

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }

}
