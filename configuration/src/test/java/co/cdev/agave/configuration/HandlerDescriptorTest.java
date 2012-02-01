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
package co.cdev.agave.configuration;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import co.cdev.agave.HttpMethod;
import co.cdev.agave.RoutingContext;
import co.cdev.agave.configuration.HandlerDescriptor;
import co.cdev.agave.configuration.HandlerDescriptorImpl;
import co.cdev.agave.configuration.ScanResult;
import co.cdev.agave.configuration.ScanResultImpl;
import co.cdev.agave.sample.LoginForm;
import co.cdev.agave.sample.SampleHandler;


/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class HandlerDescriptorTest {
    private static final String cls = SampleHandler.class.getName();
    private static final String met = "login";
    
    @Test
    public void testConstructor() throws Exception {
        HandlerDescriptor a = new HandlerDescriptorImpl(new ScanResultImpl("/login", cls, met));
        Assert.assertNotNull(a);
        Assert.assertNotNull(a.getPattern());
    }
    
    @Test
    public void testLocateAnnotatedHandlerMethods() throws Exception {
    	ScanResult scanResult = new ScanResultImpl("/login", cls, met);
    	scanResult.setParameterTypes(Arrays.asList(new Class<?>[] {RoutingContext.class, LoginForm.class}));
    	
    	HandlerDescriptor a = new HandlerDescriptorImpl(scanResult);
    	a.locateAnnotatedHandlerMethods(scanResult);
    	
    	Assert.assertEquals(LoginForm.class, a.getFormClass());
    }

    @Test
    public void testEquals() throws Exception {
        HandlerDescriptor a = new HandlerDescriptorImpl(new ScanResultImpl("/login", cls, met));
        HandlerDescriptor b = new HandlerDescriptorImpl(new ScanResultImpl("/login", cls, met));
        HandlerDescriptor c = new HandlerDescriptorImpl(new ScanResultImpl("/notLogin", cls, met));
        HandlerDescriptor d = new HandlerDescriptorImpl(new ScanResultImpl("/login", HttpMethod.GET, cls, met));
        HandlerDescriptor e = new HandlerDescriptorImpl(new ScanResultImpl("/login", HttpMethod.POST, cls, met));
        HandlerDescriptor f = new HandlerDescriptorImpl(new ScanResultImpl("/login", HttpMethod.ANY, cls, met));
        
        Assert.assertEquals(a, b);
        Assert.assertFalse(a.equals(c));
        Assert.assertFalse(a.equals(d));
        Assert.assertFalse(a.equals(e));
        Assert.assertFalse(d.equals(e));
        Assert.assertEquals(a, f);
    }

    @Test
    public void testCompareTo_withDistinctPath() throws Exception {
        HandlerDescriptor a = new HandlerDescriptorImpl(new ScanResultImpl("/login", cls, met));
        HandlerDescriptor b = new HandlerDescriptorImpl(new ScanResultImpl("/login", cls, met));
        Assert.assertEquals(0, a.compareTo(b));
        
        a = new HandlerDescriptorImpl(new ScanResultImpl("/a", cls, met));
        b = new HandlerDescriptorImpl(new ScanResultImpl("/b", cls, met));
        
        Assert.assertTrue(a.compareTo(b) < 0 && 0 < b.compareTo(a));
    }
    
    @Test
    public void testCompareTo_withDuplicatePathAndDistinctMethod() throws Exception {
        HandlerDescriptor a = new HandlerDescriptorImpl(new ScanResultImpl("/login", HttpMethod.GET, cls, met));
        HandlerDescriptor b = new HandlerDescriptorImpl(new ScanResultImpl("/login", HttpMethod.POST, cls, met));
        Assert.assertTrue(a.compareTo(b) < 0 && 0 < b.compareTo(a));
    }
    
    @Test
    public void testCompareTo_withDuplicatePathAndMethodAndDifferentNumberOfParameters() throws Exception {
        HandlerDescriptor a = new HandlerDescriptorImpl(new ScanResultImpl("/login", HttpMethod.POST, cls, met)) {{
            addParameterDescriptor(new ParameterDescriptor(String.class, "a"));
            addParameterDescriptor(new ParameterDescriptor(String.class, "b"));
            addParameterDescriptor(new ParameterDescriptor(String.class, "c"));
        }};
        
        HandlerDescriptor b = new HandlerDescriptorImpl(new ScanResultImpl("/login", HttpMethod.POST, cls, met)) {{
            addParameterDescriptor(new ParameterDescriptor(String.class, "a"));
            addParameterDescriptor(new ParameterDescriptor(String.class, "b"));
        }};
        
        Assert.assertTrue(a.compareTo(b) < 0 && 0 < b.compareTo(a));
    }
    
}

