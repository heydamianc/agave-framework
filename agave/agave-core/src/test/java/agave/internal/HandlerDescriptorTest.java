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
package agave.internal;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class HandlerDescriptorTest {
    private static final String cls = "agave.sample.SampleHandler";
    private static final String met = "login";

    @Test
    public void testConstructor() throws Exception {
        HandlerDescriptor a = new HandlerDescriptorImpl(new HandlerIdentifierImpl("/login", cls, met));
        Assert.assertNotNull(a);
        Assert.assertNotNull(a.getPattern());
        Assert.assertNotNull(a.getHandlerClass());
        Assert.assertNotNull(a.getFormClass());
        Assert.assertNotNull(a.getHandlerMethod());
        Assert.assertNotNull(a.getRequestSetter());
        Assert.assertNotNull(a.getResponseSetter());
        Assert.assertNotNull(a.getParameterSetters());
        Assert.assertEquals(3, a.getParameterSetters().size());
        Assert.assertTrue(a.getParameterSetters().containsKey("username"));
        Assert.assertTrue(a.getParameterSetters().containsKey("password"));
        Assert.assertTrue(a.getParameterSetters().containsKey("remembered"));
        Assert.assertEquals(1, a.getParameterConverters().size());
        Assert.assertTrue(a.getParameterConverters().containsKey("remembered"));
    }

    @Test
    public void testEquals() throws Exception {
        HandlerDescriptor a = new HandlerDescriptorImpl(new HandlerIdentifierImpl("/login", cls, met));
        HandlerDescriptor b = new HandlerDescriptorImpl(new HandlerIdentifierImpl("/login", cls, met));
        HandlerDescriptor c = new HandlerDescriptorImpl(new HandlerIdentifierImpl("/notLogin", cls, met));
        Assert.assertEquals(a, b);
        Assert.assertFalse(a.equals(c));
    }

    @Test
    public void testCompareTo() throws Exception {
        HandlerDescriptor a = new HandlerDescriptorImpl(new HandlerIdentifierImpl("/login", cls, met));
        HandlerDescriptor b = new HandlerDescriptorImpl(new HandlerIdentifierImpl("/login", cls, met));
        Assert.assertEquals(0, a.compareTo(b));
        
        a = new HandlerDescriptorImpl(new HandlerIdentifierImpl("/a", cls, met));
        b = new HandlerDescriptorImpl(new HandlerIdentifierImpl("/b", cls, met));
        Assert.assertEquals(-1, a.compareTo(b));
        Assert.assertEquals(1, b.compareTo(a));
    }

}

