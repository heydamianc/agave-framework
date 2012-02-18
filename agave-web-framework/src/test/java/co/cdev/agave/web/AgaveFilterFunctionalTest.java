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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.Test;

import co.cdev.agave.configuration.Config;
import co.cdev.agave.configuration.ConfigGenerator;
import co.cdev.agave.configuration.ConfigGeneratorImpl;
import co.cdev.agave.sample.StringResponseProcessor;

/**
 * @author <a href="mailto:damianarrillo@gmail.com">Damian Carrillo</a>
 */
public class AgaveFilterFunctionalTest extends AbstractFunctionalTest {

    @Test
    public void testInit() throws Exception {
        AgaveFilter filter = createSilentAgaveFilter();

        emulateServletContainer(new HashMap<String, String[]>());

        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/login/"));
            allowing(request).getMethod(); will(returnValue("GET"));
        }});

        filter.init(filterConfig);

        Assert.assertNotNull(filter.getRequestMatcher());
        Assert.assertNotNull(filter.getRequestMatcher().findMatch(request));
    }

    @Test
    public void testDoFilter() throws Exception {
        AgaveFilter filter = createSilentAgaveFilter();

        final Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        parameterMap.put("username", new String[] { "damian" });
        parameterMap.put("password", new String[] { "password" });
        parameterMap.put("remembered", new String[] { "false" });

        emulateServletContainer(parameterMap);

        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/login"));
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));

            one(request).setAttribute("loggedIn", Boolean.TRUE);
            one(response).setStatus(400);
        }});

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);

        parameterMap.put("password", new String[] { "secret" });

        emulateServletContainer(parameterMap);

        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/login"));
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));

            one(request).setAttribute("loggedIn", Boolean.FALSE);
            one(response).setStatus(400);
        }});

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsNullPointerException() throws Exception {
        AgaveFilter filter = createSilentAgaveFilter();
        final Map<String, String[]> parameterMap = new HashMap<String, String[]>();

        emulateServletContainer(parameterMap);
        
        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/throws/nullPointerException"));
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
        }});
        
        expectDiagnosticInformationOnException();

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }

    @Test(expected = IOException.class)
    public void testThrowsIOException() throws Exception {
            AgaveFilter filter = new AgaveFilter();
            final Map<String, String[]> parameterMap = new HashMap<String, String[]>();

            emulateServletContainer(parameterMap);

            context.checking(new Expectations() {{
                allowing(request).getServletPath(); will(returnValue("/throws/ioException"));
                allowing(request).getMethod(); will(returnValue("GET"));
                allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
            }});

            expectDiagnosticInformationOnException();
            
            filter.init(filterConfig);
            filter.doFilter(request, response, filterChain);
    }

    @Test
    public void testWithoutForm() throws Exception {
        AgaveFilter filter = new AgaveFilter();
        final Map<String, String[]> parameterMap = new HashMap<String, String[]>();

        emulateServletContainer(parameterMap);

        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/lacks/form"));
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));

            one(request).setAttribute("noErrors", Boolean.TRUE);
        }});

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }

    @Test
    public void testWithNoMatchingPattern() throws Exception {
        AgaveFilter filter = new AgaveFilter();
        final Map<String, String[]> parameterMap = new HashMap<String, String[]>();

        emulateServletContainer(parameterMap);

        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/no/matching/pattern"));
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));

            one(filterChain).doFilter(request, response);
        }});

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }

    @Test
    public void testURIParamsOverrideRequestParams() throws Exception {
        AgaveFilter filter = new AgaveFilter();
        final Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        parameterMap.put("username", new String[] { "damian" });
        parameterMap.put("password", new String[] { "password" });

        emulateServletContainer(parameterMap);

        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/uri-params/damian/secret"));
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
            allowing(request).getParameterMap(); will(returnValue(parameterMap));

            one(request).setAttribute("username", "damian");
            one(request).setAttribute("password", "secret");
        }});

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }
    
    @Test
    public void testNamedParams() throws Exception {
        AgaveFilter filter = new AgaveFilter();
        final Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        
        emulateServletContainer(parameterMap);
        
        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/has/named/params/someValue/5"));
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
            
            one(request).setAttribute("something", "someValue");
            one(request).setAttribute("aNumber", Integer.valueOf(5));
        }});
        
        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }
    
    @Test
    public void testPotentiallyAmbiguousHandlerMethods_expectShorterMatch() throws Exception {
        AgaveFilter filter = new AgaveFilter();
        final Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        
        emulateServletContainer(parameterMap);
        
        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/overloaded"));
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
            
            one(request).setAttribute("overloadedWithNoAdditionalParams", Boolean.TRUE);
        }});
        
        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }
    
    @Test
    public void testPotentiallyAmbiguousHandlerMethods_expectLongerMatch() throws Exception {
        AgaveFilter filter = new AgaveFilter();
        final Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        emulateServletContainer(parameterMap);
        
        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/overloaded/something"));
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
            
            one(request).setAttribute("overloadedWithAdditionalParams", "something");
        }});
        
        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }

    @Test
    public void testMultipartRequest() throws Exception {
        AgaveFilter filter = new AgaveFilter();
        final InputStream in = getClass().getClassLoader().getResourceAsStream("multipart-sample-tomcat");
        try {
            final String contentType = 
                    "multipart/form-data; boundary=---------------------------979094395854168939825384612";

            emulateServletContainer(new HashMap<String, String[]>());

            context.checking(new Expectations() {{
                allowing(request).getContentType(); will(returnValue(contentType));
                allowing(request).getParameterMap(); will(returnValue(new HashMap<String, String[]>()));
                allowing(request).getServletPath(); will(returnValue("/upload/file"));
                allowing(request).getMethod(); will(returnValue("GET"));
                allowing(request).getContentType(); will(returnValue("multipart/form-data"));
                allowing(request).getInputStream(); will(returnValue(new DelegatingServletInputStream(in)));

                one(request).setAttribute("file", false);
                one(response).setStatus(400);
            }});

            filter.init(filterConfig);
            filter.doFilter(request, response, filterChain);
        } finally {
            in.close();
        }
    }

    @Test
    public void testNullDestination() throws Exception {
        AgaveFilter filter = new AgaveFilter();

        emulateServletContainer(new HashMap<String, String[]>());

        context.checking(new Expectations() {{
            allowing(request).getParameterMap(); will(returnValue(new HashMap<String, String[]>()));
            allowing(request).getServletPath(); will(returnValue("/shout/hello"));
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
            allowing(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
            allowing(response).isCommitted(); will(returnValue(true));
        }});

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }

    @Test
    public void testReturnDestinationWithForward() throws Exception {
        AgaveFilter filter = new AgaveFilter();

        emulateServletContainer(new HashMap<String, String[]>());

        context.checking(new Expectations() {{
            allowing(request).getParameterMap(); will(returnValue(new HashMap<String, String[]>()));
            allowing(request).getServletPath(); will(returnValue("/say/hello"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(response).isCommitted(); will(returnValue(false));
            
            one(request).getRequestDispatcher("/say.jsp?said=hello"); will(returnValue(requestDispatcher));
            one(requestDispatcher).forward(request, response);
        }});

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }

    @Test
    public void testReturnDestinationWithRedirectAfterPost() throws Exception {
        AgaveFilter filter = new AgaveFilter();

        emulateServletContainer(new HashMap<String, String[]>());

        context.checking(new Expectations() {{
            allowing(request).getContextPath(); will(returnValue("/app"));
            allowing(request).getParameterMap(); will(returnValue(new HashMap<String, String[]>()));
            allowing(request).getServletPath(); will(returnValue("/say/hello"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
            allowing(request).getMethod(); will(returnValue("POST"));
            allowing(response).isCommitted(); will(returnValue(false));

            one(response).sendRedirect("/app/say.jsp?said=hello");
        }});

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }

    @Test
    public void testReturnDestinationWithExplicitRedirect() throws Exception {
        AgaveFilter filter = new AgaveFilter();

        emulateServletContainer(new HashMap<String, String[]>());

        context.checking(new Expectations() {{
            allowing(request).getContextPath(); will(returnValue("/app"));
            allowing(request).getParameterMap(); will(returnValue(new HashMap<String, String[]>()));
            allowing(request).getServletPath(); will(returnValue("/whisper/hello"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(response).isCommitted(); will(returnValue(false));

            one(response).sendRedirect("/app/whisper.jsp?said=hello&how=very%20softly%20&amp;%20sweetly");

        }});

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }

    @Test
    public void testReturnURI() throws Exception {
        AgaveFilter filter = new AgaveFilter();

        emulateServletContainer(new HashMap<String, String[]>());

        context.checking(new Expectations() {{
            allowing(request).getParameterMap(); will(returnValue(new HashMap<String, String[]>()));
            allowing(request).getServletPath(); will(returnValue("/proclaim/hello"));
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
            allowing(response).isCommitted(); will(returnValue(false));

            one(response).sendRedirect("http://www.utexas.edu/");
        }});

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }

    @Test
    public void testWithNoUserSuppliedInstanceFactory() throws Exception {
        AgaveFilter filter = new AgaveFilter();
        emulateServletContainer(new HashMap<String, String[]>());
        filter.init(filterConfig);
        Assert.assertTrue(filter.getHandlerFactory() instanceof HandlerFactoryImpl);
        Assert.assertTrue(filter.getFormFactory() instanceof FormFactoryImpl);
    }
    
    private void generateConfigFile() throws Exception {
        File rootDir = new File(getClass().getResource("/").toURI());
        File configFile = new File(rootDir, "agave.conf");
        
        ConfigGenerator configGenerator = new ConfigGeneratorImpl(rootDir);
        Config config = configGenerator.generateConfig();
        config.writeToFile(configFile);
    }
    
    private void deleteConfigFile() throws Exception {
        File rootDir = new File(getClass().getResource("/").toURI());
        File configFile = new File(rootDir, "agave.conf");
        configFile.delete();
    }
    
    @Test
    public void testInit_withConfigFilePresent() throws Exception {
        generateConfigFile();
        testInit();
        deleteConfigFile();
    }
    
    @Test
    public void testDoFilter_withConfigFilePresent() throws Exception {
        generateConfigFile();
        testDoFilter();
        deleteConfigFile();
    }
    
    @Test
    public void testMultipartRequest_withConfigFilePresent() throws Exception {
        generateConfigFile();
        testMultipartRequest();
        deleteConfigFile();
    }

    @Test
    public void testNamedParams_withConfigFilePresent() throws Exception {
        generateConfigFile();
        testNamedParams();
        deleteConfigFile();
    }
    
    @Test
    public void testNullDestination_withConfigFilePresent() throws Exception {
        generateConfigFile();
        testNullDestination();
        deleteConfigFile();
    }
    
    @Test
    public void testPotentiallyAmbiguousHandlerMethods_expectLongerMatch_withConfigFilePresent() throws Exception {
        generateConfigFile();
        testPotentiallyAmbiguousHandlerMethods_expectLongerMatch();
        deleteConfigFile();
    }
    
    @Test
    public void testPotentiallyAmbiguousHandlerMethods_expectShorterMatch_withConfigFilePresent() throws Exception {
        generateConfigFile();
        testPotentiallyAmbiguousHandlerMethods_expectShorterMatch();
        deleteConfigFile();
    }
    
    @Test
    public void testReturnDestinationWithExplicitRedirect_withConfigFilePresent() throws Exception {
        generateConfigFile();
        testReturnDestinationWithExplicitRedirect();
        deleteConfigFile();
    }
    
    @Test
    public void testReturnDestinationWithForward_withConfigFilePresent() throws Exception {
        generateConfigFile();
        testReturnDestinationWithForward();
        deleteConfigFile();
    }
    
    @Test
    public void testReturnDestinationWithRedirectAfterPost_withConfigFilePresent() throws Exception {
        generateConfigFile();
        testReturnDestinationWithRedirectAfterPost();
        deleteConfigFile();
    }
    
    @Test
    public void testReturnURI_withConfigFilePresent() throws Exception {
        generateConfigFile();
        testReturnURI();
        deleteConfigFile();
    }
    
    @Test(expected = IOException.class)
    public void testThrowsIOException_withConfigFilePresent() throws Exception {
        generateConfigFile();
        testThrowsIOException();
        deleteConfigFile();
    }
    
    @Test(expected = NullPointerException.class)
    public void testThrowsNullPointerException_withConfigFilePresent() throws Exception {
        generateConfigFile();
        testThrowsNullPointerException();
        deleteConfigFile();
    }
    
    @Test
    public void testURIParamsOverrideRequestParams_withConfigFilePresent() throws Exception {
        generateConfigFile();
        testURIParamsOverrideRequestParams();
        deleteConfigFile();
    }
    
    @Test
    public void testWithNoMatchingPattern_withConfigFilePresent() throws Exception {
        generateConfigFile();
        testWithNoMatchingPattern();
        deleteConfigFile();
    }
    
    @Test
    public void testWithNoUserSuppliedInstanceFactory_withConfigFilePresent() throws Exception {
        generateConfigFile();
        testWithNoUserSuppliedInstanceFactory();
        deleteConfigFile();
    }
    
    @Test
    public void testWithoutForm_withConfigFilePresent() throws Exception {
        generateConfigFile();
        testWithoutForm();
        deleteConfigFile();
    }
    
    @Test
    public void testWithCustomResponseProcessor() throws Exception {
        AgaveFilter filter = new AgaveFilter() {
            @Override
            public void init(FilterConfig filterConfig) throws ServletException {
                super.init(filterConfig);
                super.addResultProcessor(new StringResponseProcessor());
            }
        };
        
        emulateServletContainer(new HashMap<String, String[]>());
        filter.init(filterConfig);
        
        final StringWriter resultWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(resultWriter);
        
        context.checking(new Expectations() {{
            allowing(request).getParameterMap(); will(returnValue(new HashMap<String, String[]>()));
            allowing(request).getServletPath(); will(returnValue("/text"));
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
            allowing(response).isCommitted(); will(returnValue(false));

            allowing(response).setContentType("text/plain");
            allowing(response).setStatus(StatusCode._200_Ok.getNumericCode());
            allowing(response).getWriter(); will(returnValue(printWriter));
        }});

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
        
        assertEquals("Text!", resultWriter.toString());
    }
    
}
