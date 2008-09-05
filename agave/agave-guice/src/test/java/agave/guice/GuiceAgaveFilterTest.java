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
package agave.guice;

import java.io.File;
import java.net.URL;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import agave.AgaveFilter;


/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class GuiceAgaveFilterTest {

    Mockery context = new Mockery();
    FilterConfig filterConfig;
    HttpServletRequest request;
    HttpServletResponse response;
    ServletContext servletContext;

    @Before
    public void setup() throws Exception {
        filterConfig = context.mock(FilterConfig.class);
        request = context.mock(HttpServletRequest.class);
        response = context.mock(HttpServletResponse.class);
        servletContext = context.mock(ServletContext.class);
    }
    
    @Test
    public void testWithNoUserSuppliedInstanceFactory() throws Exception {
        AgaveFilter filter = new AgaveFilter();
        
        URL rootUrl = getClass().getClassLoader().getResource("agave");
        final String realPath = new File(rootUrl.toURI()).getAbsolutePath();

        context.checking(new Expectations() {{
            allowing(servletContext).getRealPath("/WEB-INF/classes"); will(returnValue(realPath));
            allowing(filterConfig).getServletContext(); will(returnValue(servletContext));
            allowing(filterConfig).getInitParameter("instanceFactory"); will(returnValue("agave.guice.PastebinInstanceFactory"));
            allowing(filterConfig).getInitParameter("classesDirectory"); will(returnValue(null));
        }});

        filter.init(filterConfig);
        Assert.assertTrue(filter.getInstanceFactory() instanceof PastebinInstanceFactory);
    }
    
}
