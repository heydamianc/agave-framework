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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import co.cdev.agave.HttpMethod;
import co.cdev.agave.URIPatternImpl;
import co.cdev.agave.configuration.Config;
import co.cdev.agave.configuration.HandlerDescriptor;
import co.cdev.agave.configuration.HandlerDescriptorImpl;
import co.cdev.agave.configuration.ParamDescriptor;
import co.cdev.agave.configuration.ParamDescriptorImpl;
import co.cdev.agave.configuration.RoutingContext;
import co.cdev.agave.sample.LoginForm;
import co.cdev.agave.sample.SampleHandler;
import co.cdev.agave.web.RequestMatcher;
import co.cdev.agave.web.RequestMatcherImpl;

@SuppressWarnings("serial")
public class HandlerRegistryTest {
    
    private Mockery context = new Mockery();
    private Config config;
    private RequestMatcher registry;
    private HttpServletRequest request;
    private Class<?> handlerClass;

    @Before
    public void setUp() throws SecurityException, NoSuchMethodException {
        request = context.mock(HttpServletRequest.class);
        config = context.mock(Config.class);
        registry = new RequestMatcherImpl(config);
        handlerClass = SampleHandler.class;
    }

    @Test
    public void testMatches() throws Exception {
        final Method handlerMethod = handlerClass.getMethod("login", RoutingContext.class, LoginForm.class);
        
        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/some/path"));
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(config).iterator(); will(returnValue(new TreeSet<HandlerDescriptor>() {{
                add(new HandlerDescriptorImpl(
                        handlerClass, 
                        handlerMethod, 
                        new URIPatternImpl("/some/path"), 
                        HttpMethod.GET, 
                        false,
                        false,
                        (String) null, 
                        (Class<?>) null, 
                        new ArrayList<ParamDescriptor>()));
                add(new HandlerDescriptorImpl(
                        handlerClass, 
                        handlerMethod, 
                        new URIPatternImpl("/other/path"), 
                        HttpMethod.GET, 
                        false,
                        false,
                        (String) null, 
                        (Class<?>) null, 
                        new ArrayList<ParamDescriptor>()));
            }}.iterator()));
        }});

        HandlerDescriptor descriptor = registry.findMatch(request);
        
        assertNotNull(descriptor);
        assertEquals("/some/path", descriptor.getURIPattern().toString());
    }
    
    @Test
    public void testMatches_withOverloadedHandlerDescriptors() throws Exception {
        final Method handlerMethod = handlerClass.getMethod("overloaded", RoutingContext.class);
        
        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/overloaded/blah"));
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(config).iterator(); will(returnValue(new TreeSet<HandlerDescriptor>() {{
                add(new HandlerDescriptorImpl(
                        handlerClass, 
                        handlerMethod, 
                        new URIPatternImpl("/overloaded"), 
                        HttpMethod.GET, 
                        false,
                        false,
                        (String) null, 
                        (Class<?>) null, 
                        new ArrayList<ParamDescriptor>()));
                add(new HandlerDescriptorImpl(
                        handlerClass, 
                        handlerMethod, 
                        new URIPatternImpl("/overloaded/${param}"), 
                        HttpMethod.GET, 
                        false,
                        false,
                        (String) null, 
                        (Class<?>) null, 
                        new ArrayList<ParamDescriptor>()));
            }}.iterator()));
        }});
        
        HandlerDescriptor descriptor = registry.findMatch(request);
        
        assertNotNull(descriptor);
        assertEquals("/overloaded/${param}", descriptor.getURIPattern().toString());
    }
        
    @Test
    public void testMatches_withOverloadedHandlerDescriptors2() throws Exception {
        final Method handlerMethod = handlerClass.getMethod("overloaded", RoutingContext.class);
        
        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/overloaded"));
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(config).iterator(); will(returnValue(new TreeSet<HandlerDescriptor>() {{
                add(new HandlerDescriptorImpl(
                        handlerClass, 
                        handlerMethod, 
                        new URIPatternImpl("/overloaded/${param}"), 
                        HttpMethod.GET, 
                        false,
                        false,
                        (String) null, 
                        (Class<?>) null, 
                        new ArrayList<ParamDescriptor>()));
                add(new HandlerDescriptorImpl(
                        handlerClass, 
                        handlerMethod, 
                        new URIPatternImpl("/overloaded"), 
                        HttpMethod.GET, 
                        false,
                        false,
                        (String) null, 
                        (Class<?>) null, 
                        new ArrayList<ParamDescriptor>()));
            }}.iterator()));
        }});
        
        HandlerDescriptor descriptor = registry.findMatch(request);
        
        assertNotNull(descriptor);
        assertEquals("/overloaded", descriptor.getURIPattern().toString());   
    }
    
    @Test
    public void testMatches_withAnyHttpMethodAndMatchingURI() throws Exception {
        final Method handlerMethod = handlerClass.getMethod("login", RoutingContext.class, LoginForm.class);
        
        context.checking(new Expectations() {{
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getServletPath(); will(returnValue("/login"));
            allowing(config).iterator(); will(returnValue(new TreeSet<HandlerDescriptor>() {{
                add(new HandlerDescriptorImpl(
                        handlerClass, 
                        handlerMethod, 
                        new URIPatternImpl("/login"), 
                        HttpMethod.ANY, 
                        false,
                        false,
                        (String) null, 
                        (Class<?>) null, 
                        new ArrayList<ParamDescriptor>()));
            }}.iterator()));
        }});
        
        HandlerDescriptor descriptor = registry.findMatch(request);
        
        assertNotNull(descriptor);
        assertEquals("/login", descriptor.getURIPattern().toString());
    }

    @Test
    public void testMatches_withMatchingMethodAndMatchingURI() throws Exception {
        final Method handlerMethod = handlerClass.getMethod("login", RoutingContext.class, LoginForm.class);
        
        context.checking(new Expectations() {{
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getServletPath(); will(returnValue("/login"));
            allowing(config).iterator(); will(returnValue(new TreeSet<HandlerDescriptor>() {{
                add(new HandlerDescriptorImpl(
                        handlerClass, 
                        handlerMethod, 
                        new URIPatternImpl("/login"), 
                        HttpMethod.ANY, 
                        false,
                        false,
                        (String) null, 
                        (Class<?>) null, 
                        new ArrayList<ParamDescriptor>()));
            }}.iterator()));
        }});
      
        HandlerDescriptor descriptor = registry.findMatch(request);
    
        assertNotNull(descriptor);
        assertEquals("/login", descriptor.getURIPattern().toString());
    }
  
  @Test
  public void testMatches_withNonMatchingMethodAndMatchingURI() throws Exception {
      final Method handlerMethod = handlerClass.getMethod("login", RoutingContext.class, LoginForm.class);
      
      context.checking(new Expectations() {{
          allowing(request).getMethod(); will(returnValue("GET"));
          allowing(request).getServletPath(); will(returnValue("/login"));
          allowing(config).iterator(); will(returnValue(new TreeSet<HandlerDescriptor>() {{
              add(new HandlerDescriptorImpl(
                      handlerClass, 
                      handlerMethod, 
                      new URIPatternImpl("/login"), 
                      HttpMethod.POST, 
                      false,
                      false,
                      (String) null, 
                      (Class<?>) null, 
                      new ArrayList<ParamDescriptor>()));
          }}.iterator()));
      }});
  
      HandlerDescriptor descriptor = registry.findMatch(request);

      assertNull(descriptor);
  }

    @Test
    public void testMatches_withMatchingMethodAndNonMatchingURI() throws Exception {
        final Method handlerMethod = handlerClass.getMethod("login", RoutingContext.class, LoginForm.class);
        
        context.checking(new Expectations() {{
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getServletPath(); will(returnValue("/logout"));
            allowing(config).iterator(); will(returnValue(new TreeSet<HandlerDescriptor>() {{
                add(new HandlerDescriptorImpl(
                        handlerClass, 
                        handlerMethod, 
                        new URIPatternImpl("/login"), 
                        HttpMethod.GET, 
                        false,
                        false,
                        (String) null, 
                        (Class<?>) null, 
                        new ArrayList<ParamDescriptor>()));
            }}.iterator()));
        }});
        
        HandlerDescriptor descriptor = registry.findMatch(request);

        assertNull(descriptor);
    }
  
    @Test
    public void testMatches_withParams() throws Exception {
        final Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("something", "something");
        parameterMap.put("aNumber", Integer.valueOf(32));
      
        final Method handlerMethod = handlerClass.getMethod("hasNamedParams", RoutingContext.class, String.class, int.class);
        
        context.checking(new Expectations() {{
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getServletPath(); will(returnValue("/has/params"));
            allowing(request).getParameterMap(); will(returnValue(parameterMap));
            allowing(config).iterator(); will(returnValue(new TreeSet<HandlerDescriptor>() {{
                add(new HandlerDescriptorImpl(
                        handlerClass, 
                        handlerMethod, 
                        new URIPatternImpl("/has/params"), 
                        HttpMethod.ANY, 
                        false, 
                        false, 
                        (String) null, 
                        (Class<?>) null, 
                        new ArrayList<ParamDescriptor>() {{
                            add(new ParamDescriptorImpl(String.class, "something", null));
                            add(new ParamDescriptorImpl(int.class, "aNumber", null));
                        }}));
            }}.iterator()));
        }});
        
        HandlerDescriptor descriptor = registry.findMatch(request);
        
        assertNotNull(descriptor);
        assertEquals("/has/params", descriptor.getURIPattern().toString());
    }
    
    @Test
    public void testMatches_withURIParametersDescriptors() throws Exception {
        final Map<String, Object> parameterMap = new HashMap<String, Object>();
        final Method handlerMethod = handlerClass.getMethod("hasNamedParams", RoutingContext.class, String.class, int.class);
        
        context.checking(new Expectations() {{
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getServletPath(); will(returnValue("/has/named/params/something/32"));
            allowing(request).getParameterMap(); will(returnValue(parameterMap));
            allowing(config).iterator(); will(returnValue(new TreeSet<HandlerDescriptor>() {{
                add(new HandlerDescriptorImpl(
                        handlerClass, 
                        handlerMethod, 
                        new URIPatternImpl("/has/named/params/${something}/${aNumber}"), 
                        HttpMethod.ANY, 
                        false, 
                        false, 
                        (String) null, 
                        (Class<?>) null, 
                        new ArrayList<ParamDescriptor>() {{
                            add(new ParamDescriptorImpl(String.class, "something", null));
                            add(new ParamDescriptorImpl(int.class, "aNumber", null));
                        }}));
            }}.iterator()));
        }});
        
        HandlerDescriptor descriptor = registry.findMatch(request);
        
        assertNotNull(descriptor);
        assertEquals("/has/named/params/${something}/${aNumber}", descriptor.getURIPattern().toString());
    }
    
}
