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
package co.cdev.agave.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import co.cdev.agave.HttpMethod;
import co.cdev.agave.configuration.Config;
import co.cdev.agave.configuration.HandlerDescriptor;
import co.cdev.agave.configuration.HandlerDescriptorImpl;
import co.cdev.agave.configuration.ParamDescriptorImpl;
import co.cdev.agave.configuration.ScanResult;
import co.cdev.agave.configuration.ScanResultImpl;
import co.cdev.agave.sample.SampleHandler;

@SuppressWarnings("serial")
public class HandlerRegistryTest {
    
    private Mockery context = new Mockery();
    private Config config;
    private RequestMatcher registry;
    private HttpServletRequest request;

    @Before
    public void setup() {
        request = context.mock(HttpServletRequest.class);
        config = context.mock(Config.class);
        registry = new RequestMatcherImpl(config);
    }

    @Test
    public void testMatches() throws Exception {
        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/some/path"));
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(config).iterator(); will(returnValue(new TreeSet<HandlerDescriptor>() {{
                add(new HandlerDescriptorImpl(getClass().getClassLoader(), new ScanResultImpl("/some/path", SampleHandler.class.getName(), "login")));
                add(new HandlerDescriptorImpl(getClass().getClassLoader(), new ScanResultImpl("/other/path", SampleHandler.class.getName(), "login")));
            }}.iterator()));
        }});

        HandlerDescriptor descriptor = registry.findMatch(request);
        
        assertNotNull(descriptor);
        assertEquals("/some/path", descriptor.getURIPattern().toString());
    }
    
    @Test
    public void testMatches_withOverloadedHandlerDescriptors() throws Exception {
        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/overloaded/blah"));
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(config).iterator(); will(returnValue(new TreeSet<HandlerDescriptor>() {{
                add(new HandlerDescriptorImpl(getClass().getClassLoader(), new ScanResultImpl("/overloaded", SampleHandler.class.getName(), "overloaded")));
                add(new HandlerDescriptorImpl(getClass().getClassLoader(), new ScanResultImpl("/overloaded/${param}", SampleHandler.class.getName(), "overloaded")));
            }}.iterator()));
        }});
        
        HandlerDescriptor descriptor = registry.findMatch(request);
        
        assertNotNull(descriptor);
        assertEquals("/overloaded/${param}", descriptor.getURIPattern().toString());
    }
        
    @Test
    public void testMatches_withOverloadedHandlerDescriptors2() throws Exception {
        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/overloaded"));
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(config).iterator(); will(returnValue(new TreeSet<HandlerDescriptor>() {{
                add(new HandlerDescriptorImpl(getClass().getClassLoader(), new ScanResultImpl("/overloaded/${param}", SampleHandler.class.getName(), "overloaded")));
                add(new HandlerDescriptorImpl(getClass().getClassLoader(), new ScanResultImpl("/overloaded", SampleHandler.class.getName(), "overloaded")));
            }}.iterator()));
        }});
        
        HandlerDescriptor descriptor = registry.findMatch(request);
        
        assertNotNull(descriptor);
        assertEquals("/overloaded", descriptor.getURIPattern().toString());   
    }
    
    @Test
    public void testMatches_withAnyHttpMethodAndMatchingURI() throws Exception {
        context.checking(new Expectations() {{
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getServletPath(); will(returnValue("/login"));
            allowing(config).iterator(); will(returnValue(new TreeSet<HandlerDescriptor>() {{
                add(new HandlerDescriptorImpl(getClass().getClassLoader(), new ScanResultImpl("/login", SampleHandler.class.getName(), "login")));
            }}.iterator()));
        }});
        
        HandlerDescriptor descriptor = registry.findMatch(request);
        
        assertNotNull(descriptor);
        assertEquals("/login", descriptor.getURIPattern().toString());
    }
  
    @Test
    public void testMatches_withMatchingMethodAndMatchingURI() throws Exception {
        context.checking(new Expectations() {{
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getServletPath(); will(returnValue("/login"));
            allowing(config).iterator(); will(returnValue(new TreeSet<HandlerDescriptor>() {{
                add(new HandlerDescriptorImpl(getClass().getClassLoader(), new ScanResultImpl("/login", HttpMethod.GET, SampleHandler.class.getName(), "login")));
            }}.iterator()));
        }});
      
        HandlerDescriptor descriptor = registry.findMatch(request);
    
        assertNotNull(descriptor);
        assertEquals("/login", descriptor.getURIPattern().toString());
    }
  
  @Test
  public void testMatches_withNonMatchingMethodAndMatchingURI() throws Exception {
      context.checking(new Expectations() {{
          allowing(request).getMethod(); will(returnValue("GET"));
          allowing(request).getServletPath(); will(returnValue("/login"));
          allowing(config).iterator(); will(returnValue(new TreeSet<HandlerDescriptor>() {{
              add(new HandlerDescriptorImpl(getClass().getClassLoader(), new ScanResultImpl("/login", HttpMethod.POST, SampleHandler.class.getName(), "login")));
          }}.iterator()));
      }});
  
      HandlerDescriptor descriptor = registry.findMatch(request);

      assertNull(descriptor);
  }

    @Test
    public void testMatches_withMatchingMethodAndNonMatchingURI() throws Exception {
        context.checking(new Expectations() {{
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getServletPath(); will(returnValue("/logout"));
            allowing(config).iterator(); will(returnValue(new TreeSet<HandlerDescriptor>() {{
                add(new HandlerDescriptorImpl(getClass().getClassLoader(), new ScanResultImpl("/login", HttpMethod.GET, SampleHandler.class.getName(), "login")));
            }}.iterator()));
        }});
        
        HandlerDescriptor descriptor = registry.findMatch(request);

        assertNull(descriptor);
    }
  
    @Test
    public void testMatches_withParameterDescriptors() throws Exception {
        final Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("color", "orange");
        parameterMap.put("always", Boolean.FALSE);
      
        ScanResult scanResult = new ScanResultImpl("/favorites", HttpMethod.GET, SampleHandler.class.getName(), "login");
        final HandlerDescriptorImpl favorites = new HandlerDescriptorImpl(getClass().getClassLoader(), scanResult) {{
            addParamDescriptor(new ParamDescriptorImpl(String.class, "color"));
            addParamDescriptor(new ParamDescriptorImpl(Boolean.class, "always"));
        }};
        
        context.checking(new Expectations() {{
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getServletPath(); will(returnValue("/favorites"));
            allowing(request).getParameterMap(); will(returnValue(parameterMap));
            allowing(config).iterator(); will(returnValue(new TreeSet<HandlerDescriptor>() {{
                add(favorites);
            }}.iterator()));
        }});
        
        HandlerDescriptor descriptor = registry.findMatch(request);
        
        assertNotNull(descriptor);
        assertEquals("/favorites", descriptor.getURIPattern().toString());
    }
  
  @Test
  public void testMatches_withURIParameters() throws Exception {
      final Map<String, Object> parameterMap = new HashMap<String, Object>();
      
      ScanResult scanResult = new ScanResultImpl("/favorites/${color}", HttpMethod.GET, SampleHandler.class.getName(), "login");
      final HandlerDescriptorImpl favoriteColor = new HandlerDescriptorImpl(getClass().getClassLoader(), scanResult) {{
          addParamDescriptor(new ParamDescriptorImpl(String.class, "color"));
      }};
      
      context.checking(new Expectations() {{
          allowing(request).getMethod(); will(returnValue("GET"));
          allowing(request).getServletPath(); will(returnValue("/favorites/orange"));
          allowing(request).getParameterMap(); will(returnValue(parameterMap));
          allowing(config).iterator(); will(returnValue(new TreeSet<HandlerDescriptor>() {{
              add(favoriteColor);
          }}.iterator()));
      }});
      
      HandlerDescriptor descriptor = registry.findMatch(request);
      
      assertNotNull(descriptor);
      assertEquals("/favorites/${color}", descriptor.getURIPattern().toString());
  }
    
}
