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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.DelegatingServletInputStream;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import agave.conversion.BooleanConverter;
import agave.internal.HandlerDescriptor;
import agave.internal.HandlerRegistryImpl;
import agave.internal.ParameterBinder;
import agave.internal.ParameterBinderImpl;
import agave.sample.AliasedForm;
import agave.sample.LoginForm;
import agave.sample.MultipleHandler;
import agave.sample.SampleHandler;


/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class AgaveFilterTest {

    Mockery context = new Mockery();
    FilterConfig config;

    @Before
    public void setup() throws Exception {
        this.config = context.mock(FilterConfig.class);
    }

    private AgaveFilter scanRoot() throws Exception {
        URL rootUrl = getClass().getClassLoader().getResource("agave");
        Assert.assertNotNull(rootUrl);
        File root = new File(rootUrl.toURI());
        Assert.assertNotNull(root);

        AgaveFilter filter = new AgaveFilter();
        filter.setHandlerRegistry(new HandlerRegistryImpl());
        filter.scanClassesDirForHandlers(root);
        return filter;
    }

    @Test
    public void testScanClassesDirForHandlers() throws Exception {
        AgaveFilter filter = scanRoot();

        HandlerDescriptor desc = filter.getHandlerRegistry().findMatch("/login");
        Assert.assertNotNull(desc);
        Assert.assertEquals(SampleHandler.class, desc.getHandlerClass());
        desc = filter.getHandlerRegistry().findMatch("/test1");
        Assert.assertEquals(MultipleHandler.class, desc.getHandlerClass());
        desc = filter.getHandlerRegistry().findMatch("/test2");
        Assert.assertEquals(MultipleHandler.class, desc.getHandlerClass());
    }

    @Test
    public void testScanClassesDirForHandlerForms() throws Exception {
        AgaveFilter filter = scanRoot();
        HandlerDescriptor desc = filter.getHandlerRegistry().findMatch("/login");
        Assert.assertNotNull(desc);
        Assert.assertEquals(SampleHandler.class, desc.getHandlerClass());
        Assert.assertEquals(LoginForm.class, desc.getFormClass());
        Assert.assertEquals(LoginForm.class.getMethod("setUsername", String.class), 
            desc.getMutators().get("username"));
        Assert.assertEquals(LoginForm.class.getMethod("setPassword", String.class), 
            desc.getMutators().get("password"));
        Assert.assertEquals(LoginForm.class.getMethod("setRemembered", Boolean.class), 
            desc.getMutators().get("remembered"));
        Assert.assertEquals(BooleanConverter.class, desc.getConverters().get("remembered"));
    }

    @Test
    public void testBindURIParametersWithActualDescriptor() throws Exception {
        AgaveFilter filter = scanRoot();

        final String uri = "/uri-params/damian/password/";
        HandlerDescriptor desc = filter.getHandlerRegistry().findMatch(uri);
        Assert.assertNotNull(desc);

        LoginForm form = new LoginForm();
        ParameterBinder binder = new ParameterBinderImpl(form, desc);

        final HttpServletRequest request = context.mock(HttpServletRequest.class);

        context.checking(new Expectations() {{
            allowing(request).getRequestURI(); will(returnValue(uri));
        }});

        binder.bindURIParameters(request);

        Assert.assertEquals("damian", form.getUsername());
        Assert.assertEquals("password", form.getPassword());
    }

    @Test // HandlerDescriptor.locateAnnotatedFormMethods
    public void testScanClassesDirForHandlerFormsWithAliasedProperties() throws Exception {
        AgaveFilter filter = scanRoot();
        HandlerDescriptor desc = filter.getHandlerRegistry().findMatch("/aliased");
        Assert.assertNotNull(desc);
        Assert.assertEquals(SampleHandler.class, desc.getHandlerClass());
        Assert.assertEquals(AliasedForm.class, desc.getFormClass());
        Assert.assertEquals(AliasedForm.class.getMethod("setSomeProperty", String.class), 
            desc.getMutators().get("someAlias"));
        Assert.assertEquals(AliasedForm.class.getMethod("setAnotherProperty", Boolean.class),
            desc.getMutators().get("anotherAlias"));
        Assert.assertEquals(BooleanConverter.class, desc.getConverters().get("anotherAlias"));
    }

    @Test
    public void testInit() throws Exception {
        AgaveFilter filter = new AgaveFilter();

        URL rootUrl = getClass().getClassLoader().getResource("agave");
        final String realPath = new File(rootUrl.toURI()).getAbsolutePath();

        final ServletContext servletContext = context.mock(ServletContext.class);
        context.checking(new Expectations() {{
            allowing(servletContext).getRealPath("/WEB-INF/classes"); will(returnValue(realPath));
            allowing(config).getServletContext(); will(returnValue(servletContext));
        }});

        filter.init(config);

        Assert.assertNotNull(filter.getHandlerRegistry());

        // from SampleHandler
        Assert.assertNotNull(filter.getHandlerRegistry().findMatch("/login"));
        Assert.assertNotNull(filter.getHandlerRegistry().findMatch("/aliased"));
        Assert.assertNotNull(filter.getHandlerRegistry().findMatch("/uri-params/damian/password"));

        // from MultipleHandler
        Assert.assertNotNull(filter.getHandlerRegistry().findMatch("/test1"));
        Assert.assertNotNull(filter.getHandlerRegistry().findMatch("/test2"));
    }

    @Test
    public void testDoFilter() throws Exception {
        AgaveFilter filter = new AgaveFilter();

        URL rootUrl = getClass().getClassLoader().getResource("agave");
        final String realPath = new File(rootUrl.toURI()).getAbsolutePath();
        final ServletContext servletContext = context.mock(ServletContext.class);
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        final FilterChain chain = context.mock(FilterChain.class);
        final Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        parameterMap.put("username", new String[] {"damian"});
        parameterMap.put("password", new String[] {"password"});
        parameterMap.put("remembered", new String[] {"false"});

        context.checking(new Expectations() {{
            allowing(servletContext).getRealPath("/WEB-INF/classes"); will(returnValue(realPath));
            allowing(config).getServletContext(); will(returnValue(servletContext));
            allowing(request).getRequestURI(); will(returnValue("/login"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
            allowing(request).getParameterMap(); will(returnValue(parameterMap));

            // expect the handler to set an attribute
            one(request).setAttribute("loggedIn", Boolean.TRUE);
            one(response).setStatus(400);
        }});

        filter.init(config);
        filter.doFilter(request, response, chain);

        parameterMap.put("password", new String[] {"secret"});

        context.checking(new Expectations() {{
            allowing(servletContext).getRealPath("/WEB-INF/classes"); will(returnValue(realPath));
            allowing(config).getServletContext(); will(returnValue(servletContext));
            allowing(request).getRequestURI(); will(returnValue("/login"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
            allowing(request).getParameterMap(); will(returnValue(parameterMap));

            // expect the handler to set an attribute
            one(request).setAttribute("loggedIn", Boolean.FALSE);
            one(response).setStatus(400);
        }});

        filter.init(config);
        filter.doFilter(request, response, chain);
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsNullPointerException() throws Exception {
        AgaveFilter filter = new AgaveFilter();

        URL rootUrl = getClass().getClassLoader().getResource("agave");
        final String realPath = new File(rootUrl.toURI()).getAbsolutePath();
        final ServletContext servletContext = context.mock(ServletContext.class);
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        final FilterChain chain = context.mock(FilterChain.class);
        final Map<String, String[]> parameterMap = new HashMap<String, String[]>();

        context.checking(new Expectations() {{
            allowing(servletContext).getRealPath("/WEB-INF/classes"); will(returnValue(realPath));
            allowing(config).getServletContext(); will(returnValue(servletContext));
            allowing(request).getRequestURI(); will(returnValue("/throws/nullPointerException"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
            allowing(request).getParameterMap(); will(returnValue(parameterMap));
        }});

        filter.init(config);
        filter.doFilter(request, response, chain);
    }

    @Test(expected = IOException.class)
    public void testThrowsIOException() throws Exception {
        AgaveFilter filter = new AgaveFilter();

        URL rootUrl = getClass().getClassLoader().getResource("agave");
        final String realPath = new File(rootUrl.toURI()).getAbsolutePath();
        final ServletContext servletContext = context.mock(ServletContext.class);
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        final FilterChain chain = context.mock(FilterChain.class);
        final Map<String, String[]> parameterMap = new HashMap<String, String[]>();

        context.checking(new Expectations() {{
            allowing(servletContext).getRealPath("/WEB-INF/classes"); will(returnValue(realPath));
            allowing(config).getServletContext(); will(returnValue(servletContext));
            allowing(request).getRequestURI(); will(returnValue("/throws/ioException"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
            allowing(request).getParameterMap(); will(returnValue(parameterMap));
        }});

        filter.init(config);
        filter.doFilter(request, response, chain);
    }

    @Test
    public void testWithoutForm() throws Exception {
        AgaveFilter filter = new AgaveFilter();

        URL rootUrl = getClass().getClassLoader().getResource("agave");
        final String realPath = new File(rootUrl.toURI()).getAbsolutePath();
        final ServletContext servletContext = context.mock(ServletContext.class);
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        final FilterChain chain = context.mock(FilterChain.class);
        final Map<String, String[]> parameterMap = new HashMap<String, String[]>();

        context.checking(new Expectations() {{
            allowing(servletContext).getRealPath("/WEB-INF/classes"); will(returnValue(realPath));
            allowing(config).getServletContext(); will(returnValue(servletContext));
            allowing(request).getRequestURI(); will(returnValue("/lacks/form"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
            allowing(request).getParameterMap(); will(returnValue(parameterMap));

            // expect an attribute
            one(request).setAttribute("noErrors", Boolean.TRUE);
        }});

        filter.init(config);
        filter.doFilter(request, response, chain);
    }

    @Test
    public void testWithNoMatchingPattern() throws Exception {
        AgaveFilter filter = new AgaveFilter();

        URL rootUrl = getClass().getClassLoader().getResource("agave");
        final String realPath = new File(rootUrl.toURI()).getAbsolutePath();
        final ServletContext servletContext = context.mock(ServletContext.class);
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        final FilterChain chain = context.mock(FilterChain.class);
        final Map<String, String[]> parameterMap = new HashMap<String, String[]>();

        context.checking(new Expectations() {{
            allowing(servletContext).getRealPath("/WEB-INF/classes"); will(returnValue(realPath));
            allowing(config).getServletContext(); will(returnValue(servletContext));
            allowing(request).getRequestURI(); will(returnValue("/no/matching/pattern"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
            allowing(request).getParameterMap(); will(returnValue(parameterMap));

            // expect an attribute
            one(chain).doFilter(request, response);
        }});

        filter.init(config);
        filter.doFilter(request, response, chain);
    }

    @Test
    public void testWithNoMatchingEncodingType() throws Exception {
        AgaveFilter filter = new AgaveFilter();

        URL rootUrl = getClass().getClassLoader().getResource("agave");
        final String realPath = new File(rootUrl.toURI()).getAbsolutePath();
        final ServletContext servletContext = context.mock(ServletContext.class);
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        final FilterChain chain = context.mock(FilterChain.class);
        final Map<String, String[]> parameterMap = new HashMap<String, String[]>();

        context.checking(new Expectations() {{
            allowing(servletContext).getRealPath("/WEB-INF/classes"); will(returnValue(realPath));
            allowing(config).getServletContext(); will(returnValue(servletContext));
            allowing(request).getRequestURI(); will(returnValue("/login"));
            allowing(request).getContentType(); will(returnValue("text/plain"));
            allowing(request).getParameterMap(); will(returnValue(parameterMap));

            // expect an attribute
            one(chain).doFilter(request, response);
        }});

        filter.init(config);
        filter.doFilter(request, response, chain);
    }

    @Test
    public void testURIParamsOverrideRequestParams() throws Exception {
        AgaveFilter filter = new AgaveFilter();

        URL rootUrl = getClass().getClassLoader().getResource("agave");
        final String realPath = new File(rootUrl.toURI()).getAbsolutePath();
        final ServletContext servletContext = context.mock(ServletContext.class);
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        final FilterChain chain = context.mock(FilterChain.class);
        final Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        parameterMap.put("username", new String[] {"damian"});
        parameterMap.put("password", new String[] {"password"});

        context.checking(new Expectations() {{
            allowing(servletContext).getRealPath("/WEB-INF/classes"); will(returnValue(realPath));
            allowing(config).getServletContext(); will(returnValue(servletContext));
            allowing(request).getRequestURI(); will(returnValue("/uri-params/damian/secret"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
            allowing(request).getParameterMap(); will(returnValue(parameterMap));

            // expect a couple of attributes
            one(request).setAttribute("username", "damian");
            one(request).setAttribute("password", "secret");
        }});

        filter.init(config);
        filter.doFilter(request, response, chain);
    }
    
    @Test
    public void testMultipartRequest() throws Exception {
        AgaveFilter filter = new AgaveFilter();

        URL rootUrl = getClass().getClassLoader().getResource("agave");
        final String realPath = new File(rootUrl.toURI()).getAbsolutePath();
        final ServletContext servletContext = context.mock(ServletContext.class);
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        final FilterChain chain = context.mock(FilterChain.class);
        final InputStream in = getClass().getClassLoader().getResourceAsStream("multipart-sample-tomcat");

        context.checking(new Expectations() {{
            allowing(servletContext).getRealPath("/WEB-INF/classes"); will(returnValue(realPath));
            allowing(config).getServletContext(); will(returnValue(servletContext));
            allowing(request).getParameterMap(); will(returnValue(new HashMap<String, String[]>()));
            allowing(request).getRequestURI(); will(returnValue("/upload/file"));
            allowing(request).getContentType(); will(returnValue("multipart/form-data"));
            allowing(request).getInputStream(); will(returnValue(new DelegatingServletInputStream(in)));
            
            one(request).setAttribute("file", false);
            one(response).setStatus(400);
        }});

        filter.init(config);
        filter.doFilter(request, response, chain);
    }
    
    @Test
    public void testNullDestination() throws Exception {
        AgaveFilter filter = new AgaveFilter();
        
        URL rootUrl = getClass().getClassLoader().getResource("agave");
        final String realPath = new File(rootUrl.toURI()).getAbsolutePath();
        final ServletContext servletContext = context.mock(ServletContext.class);
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        final FilterChain chain = context.mock(FilterChain.class);

        context.checking(new Expectations() {{
            allowing(servletContext).getRealPath("/WEB-INF/classes"); will(returnValue(realPath));
            allowing(config).getServletContext(); will(returnValue(servletContext));
            allowing(request).getParameterMap(); will(returnValue(new HashMap<String, String[]>()));
            allowing(request).getRequestURI(); will(returnValue("/shout/hello"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
            allowing(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
            allowing(response).isCommitted(); will(returnValue(true));
        }});

        filter.init(config);
        filter.doFilter(request, response, chain);
    }
    
    @Test
    public void testReturnDestinationWithForward() throws Exception {
        AgaveFilter filter = new AgaveFilter();
        
        URL rootUrl = getClass().getClassLoader().getResource("agave");
        final String realPath = new File(rootUrl.toURI()).getAbsolutePath();
        final ServletContext servletContext = context.mock(ServletContext.class);
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        final FilterChain chain = context.mock(FilterChain.class);
        final RequestDispatcher requestDispatcher = context.mock(RequestDispatcher.class);

        context.checking(new Expectations() {{
            allowing(servletContext).getRealPath("/WEB-INF/classes"); will(returnValue(realPath));
            allowing(config).getServletContext(); will(returnValue(servletContext));
            allowing(request).getParameterMap(); will(returnValue(new HashMap<String, String[]>()));
            allowing(request).getRequestURI(); will(returnValue("/say/hello"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(response).isCommitted(); will(returnValue(false));
            
            allowing(servletContext).getContextPath(); will(returnValue("/app"));
            allowing(request).getRequestDispatcher("/app/say.jsp?said=hello"); will(returnValue(requestDispatcher)); 
            allowing(requestDispatcher).forward(request, response);
        }});

        filter.init(config);
        filter.doFilter(request, response, chain);
    }
    
    @Test
    public void testReturnDestinationWithRedirectAfterPost() throws Exception {
        AgaveFilter filter = new AgaveFilter();
        
        URL rootUrl = getClass().getClassLoader().getResource("agave");
        final String realPath = new File(rootUrl.toURI()).getAbsolutePath();
        final ServletContext servletContext = context.mock(ServletContext.class);
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        final FilterChain chain = context.mock(FilterChain.class);

        context.checking(new Expectations() {{
            allowing(servletContext).getRealPath("/WEB-INF/classes"); will(returnValue(realPath));
            allowing(config).getServletContext(); will(returnValue(servletContext));
            allowing(request).getParameterMap(); will(returnValue(new HashMap<String, String[]>()));
            allowing(request).getRequestURI(); will(returnValue("/say/hello"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
            allowing(request).getMethod(); will(returnValue("POST"));
            allowing(response).isCommitted(); will(returnValue(false));
            
            allowing(servletContext).getContextPath(); will(returnValue("/app"));
            allowing(response).sendRedirect("/app/say.jsp?said=hello");
        }});

        filter.init(config);
        filter.doFilter(request, response, chain);
    }
    
    @Test
    public void testReturnDestinationWithExplicitRedirect() throws Exception {
        AgaveFilter filter = new AgaveFilter();
        
        URL rootUrl = getClass().getClassLoader().getResource("agave");
        final String realPath = new File(rootUrl.toURI()).getAbsolutePath();
        final ServletContext servletContext = context.mock(ServletContext.class);
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        final FilterChain chain = context.mock(FilterChain.class);

        context.checking(new Expectations() {{
            allowing(servletContext).getRealPath("/WEB-INF/classes"); will(returnValue(realPath));
            allowing(config).getServletContext(); will(returnValue(servletContext));
            allowing(request).getParameterMap(); will(returnValue(new HashMap<String, String[]>()));
            allowing(request).getRequestURI(); will(returnValue("/whisper/hello"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(response).isCommitted(); will(returnValue(false));
            
            allowing(servletContext).getContextPath(); will(returnValue("/app"));
            allowing(response).sendRedirect("/app/whisper.jsp?how=very%20softly%20&amp;%20sweetly&said=hello");
        }});

        filter.init(config);
        filter.doFilter(request, response, chain);
    }
    
    @Test
    public void testReturnURI() throws Exception {
        AgaveFilter filter = new AgaveFilter();
        
        URL rootUrl = getClass().getClassLoader().getResource("agave");
        final String realPath = new File(rootUrl.toURI()).getAbsolutePath();
        final ServletContext servletContext = context.mock(ServletContext.class);
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        final FilterChain chain = context.mock(FilterChain.class);

        context.checking(new Expectations() {{
            allowing(servletContext).getRealPath("/WEB-INF/classes"); will(returnValue(realPath));
            allowing(config).getServletContext(); will(returnValue(servletContext));
            allowing(request).getParameterMap(); will(returnValue(new HashMap<String, String[]>()));
            allowing(request).getRequestURI(); will(returnValue("/proclaim/hello"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(response).isCommitted(); will(returnValue(false));
            
            allowing(response).sendRedirect("http://www.utexas.edu/");
        }});

        filter.init(config);
        filter.doFilter(request, response, chain);
    }

}
