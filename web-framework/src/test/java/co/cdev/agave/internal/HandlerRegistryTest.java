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

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import co.cdev.agave.HttpMethod;
import co.cdev.agave.configuration.HandlerDescriptor;
import co.cdev.agave.configuration.HandlerDescriptorImpl;
import co.cdev.agave.configuration.ParamDescriptorImpl;
import co.cdev.agave.configuration.ScanResultImpl;
import co.cdev.agave.exception.DuplicateDescriptorException;
import co.cdev.agave.sample.SampleHandler;

public class HandlerRegistryTest {
    
    private Mockery context = new Mockery();
    private HandlerRegistry registry;
    private HttpServletRequest request;

    @Before
    public void setup() {
        registry = new HandlerRegistryImpl();
        request = context.mock(HttpServletRequest.class);
    }

    @Test(expected = DuplicateDescriptorException.class)
    public void testAddDescriptor() throws Exception {
        registry.addDescriptor(new HandlerDescriptorImpl(new ScanResultImpl("/pattern", SampleHandler.class.getName(), "login")));
        registry.addDescriptor(new HandlerDescriptorImpl(new ScanResultImpl("/pattern", SampleHandler.class.getName(), "login")));
    }

    @Test
    public void testAddDescriptor_withUniqueDescriptors() throws Exception {
        registry.addDescriptor(new HandlerDescriptorImpl(new ScanResultImpl("/pattern", HttpMethod.GET, SampleHandler.class.getName(), "login")));
        registry.addDescriptor(new HandlerDescriptorImpl(new ScanResultImpl("/pattern", HttpMethod.POST, SampleHandler.class.getName(), "login")));

        assertEquals(2, registry.getDescriptors().size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddToUnmodifiable() throws Exception {
        registry.getDescriptors().add(new HandlerDescriptorImpl(new ScanResultImpl("/pattern", SampleHandler.class.getName(), "login")));
    }

    @Test
    public void testMatches() throws Exception {
        registry.addDescriptor(new HandlerDescriptorImpl(new ScanResultImpl("/some/path", SampleHandler.class.getName(), "login")));
        registry.addDescriptor(new HandlerDescriptorImpl(new ScanResultImpl("/other/path", SampleHandler.class.getName(), "login")));

        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/some/path"));
            allowing(request).getMethod(); will(returnValue("GET"));
        }});

        HandlerDescriptor descriptor = registry.findMatch(request);
        
        assertNotNull(descriptor);
        assertEquals("/some/path", descriptor.getURIPattern().toString());
    }
    
    @Test
    public void testMatches_withOverloadedHandlerDescriptors() throws Exception {
        registry.addDescriptor(new HandlerDescriptorImpl(new ScanResultImpl("/overloaded", SampleHandler.class.getName(), "overloaded")));
        registry.addDescriptor(new HandlerDescriptorImpl(new ScanResultImpl("/overloaded/${param}", SampleHandler.class.getName(), "overloaded")));

        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/overloaded/blah"));
            allowing(request).getMethod(); will(returnValue("GET"));
        }});
        
        HandlerDescriptor descriptor = registry.findMatch(request);
        
        assertNotNull(descriptor);
        assertEquals("/overloaded/${param}", descriptor.getURIPattern().toString());
    }
        
    @Test
    public void testMatches_withOverloadedHandlerDescriptors2() throws Exception {
        registry.addDescriptor(new HandlerDescriptorImpl(new ScanResultImpl("/overloaded/${param}", SampleHandler.class.getName(), "overloaded")));
        registry.addDescriptor(new HandlerDescriptorImpl(new ScanResultImpl("/overloaded", SampleHandler.class.getName(), "overloaded")));
        
        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/overloaded"));
            allowing(request).getMethod(); will(returnValue("GET"));
        }});
        
        HandlerDescriptor descriptor = registry.findMatch(request);
        
        assertNotNull(descriptor);
        assertEquals("/overloaded", descriptor.getURIPattern().toString());   
    }
    
    @Test
    public void testMatches_withAnyHttpMethodAndMatchingURI() throws Exception {
        registry.addDescriptor(new HandlerDescriptorImpl(new ScanResultImpl("/login", SampleHandler.class.getName(), "login")));
        
        context.checking(new Expectations() {{
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getServletPath(); will(returnValue("/login"));
        }});
        
        HandlerDescriptor descriptor = registry.findMatch(request);
        
        assertNotNull(descriptor);
        assertEquals("/login", descriptor.getURIPattern().toString());
    }
  
    @Test
    public void testMatches_withMatchingMethodAndMatchingURI() throws Exception {
        registry.addDescriptor(new HandlerDescriptorImpl(new ScanResultImpl("/login", HttpMethod.GET, SampleHandler.class.getName(), "login")));
    
        context.checking(new Expectations() {{
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getServletPath(); will(returnValue("/login"));
        }});
      
        HandlerDescriptor descriptor = registry.findMatch(request);
    
        assertNotNull(descriptor);
        assertEquals("/login", descriptor.getURIPattern().toString());
    }
  
  @Test
  public void testMatches_withNonMatchingMethodAndMatchingURI() throws Exception {    
      registry.addDescriptor(new HandlerDescriptorImpl(new ScanResultImpl("/login", HttpMethod.POST, SampleHandler.class.getName(), "login")));
    
      context.checking(new Expectations() {{
          allowing(request).getMethod(); will(returnValue("GET"));
          allowing(request).getServletPath(); will(returnValue("/login"));
      }});
  
      HandlerDescriptor descriptor = registry.findMatch(request);

      assertNull(descriptor);
  }

    @Test
    public void testMatches_withMatchingMethodAndNonMatchingURI() throws Exception {
        registry.addDescriptor(new HandlerDescriptorImpl(new ScanResultImpl("/login", HttpMethod.GET, SampleHandler.class.getName(), "login")));
        
        context.checking(new Expectations() {{
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getServletPath(); will(returnValue("/logout"));
        }});
        
        HandlerDescriptor descriptor = registry.findMatch(request);

        assertNull(descriptor);
    }
  
    @Test
    public void testMatches_withParameterDescriptors() throws Exception {
        final Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("color", "orange");
        parameterMap.put("always", Boolean.FALSE);
      
        context.checking(new Expectations() {{
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getServletPath(); will(returnValue("/favorites"));
            allowing(request).getParameterMap(); will(returnValue(parameterMap));
        }});
      
        HandlerDescriptorImpl a = new HandlerDescriptorImpl(new ScanResultImpl("/favorites", HttpMethod.GET, SampleHandler.class.getName(), "login"))
        {
            private static final long serialVersionUID = 1L;
        {
            addParamDescriptor(new ParamDescriptorImpl(String.class, "color"));
            addParamDescriptor(new ParamDescriptorImpl(Boolean.class, "always"));
        }};
        
        registry.addDescriptor(a);
        
        HandlerDescriptor descriptor = registry.findMatch(request);
        
        assertNotNull(descriptor);
        assertEquals("/favorites", descriptor.getURIPattern().toString());
    }
  
  @Test
  public void testMatches_withURIParameters() throws Exception {
      final Map<String, Object> parameterMap = new HashMap<String, Object>();
      
      context.checking(new Expectations() {{
          allowing(request).getMethod(); will(returnValue("GET"));
          allowing(request).getServletPath(); will(returnValue("/favorites/orange"));
          allowing(request).getParameterMap(); will(returnValue(parameterMap));
      }});
      
      HandlerDescriptorImpl a = new HandlerDescriptorImpl(new ScanResultImpl("/favorites/${color}", HttpMethod.GET, SampleHandler.class.getName(), "login"))
      {
        private static final long serialVersionUID = 1L;
      {
          addParamDescriptor(new ParamDescriptorImpl(String.class, "color"));
      }};
      
      registry.addDescriptor(a);
      
      HandlerDescriptor descriptor = registry.findMatch(request);
      
      assertNotNull(descriptor);
      assertEquals("/favorites/${color}", descriptor.getURIPattern().toString());
  }
    
}
