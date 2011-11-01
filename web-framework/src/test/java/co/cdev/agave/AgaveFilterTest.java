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
package co.cdev.agave;

import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.Test;

import co.cdev.agave.internal.HandlerDescriptor;
import co.cdev.agave.sample.AliasedForm;
import co.cdev.agave.sample.LoginForm;
import co.cdev.agave.sample.SimpleHandler;
import co.cdev.agave.sample.SampleHandler;
import java.io.File;


/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class AgaveFilterTest extends AbstractFunctionalTest {
    
    @Test
    public void testScanClassesDirForLoginHandler() throws Exception {
        AgaveFilter filter = scanRoot();

        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/login"));
            allowing(request).getMethod(); will(returnValue("GET"));
        }});
        
        filter.init(filterConfig);
        HandlerDescriptor desc = filter.getHandlerRegistry().findMatch(request);
        Assert.assertNotNull(desc);
        Assert.assertEquals(SampleHandler.class, desc.getHandlerClass());
    }
    
    @Test
    public void testScanClassesDirForSimpleHandler() throws Exception {
        AgaveFilter filter = scanRoot();
        
        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/test1"));
            allowing(request).getMethod(); will(returnValue("GET"));
        }});
        
        HandlerDescriptor desc = filter.getHandlerRegistry().findMatch(request);
        Assert.assertEquals(SimpleHandler.class, desc.getHandlerClass());
    }

    @Test
    public void testScanClassesDirForHandlerForms() throws Exception {
        AgaveFilter filter = scanRoot();
        
        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/login"));
            allowing(request).getMethod(); will(returnValue("GET"));
        }});
        
        HandlerDescriptor desc = filter.getHandlerRegistry().findMatch(request);
        
        Assert.assertNotNull(desc);
        Assert.assertEquals(SampleHandler.class, desc.getHandlerClass());
        Assert.assertEquals(LoginForm.class, desc.getFormClass());
    }

    @Test
    public void testScanClassesDirForHandlerFormsWithAliasedProperties() throws Exception {
        AgaveFilter filter = scanRoot();
        
        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/aliased"));
            allowing(request).getMethod(); will(returnValue("GET"));
        }});
        
        HandlerDescriptor desc = filter.getHandlerRegistry().findMatch(request);
        
        Assert.assertNotNull(desc);
        Assert.assertEquals(SampleHandler.class, desc.getHandlerClass());
        Assert.assertEquals(AliasedForm.class, desc.getFormClass());
    }

	@Test
	public void testOverrideClassesDirectory() throws Exception {
		File targetDir = new File(getClass().getResource("/").toURI());
		AgaveFilter filter = scanRoot();
		Assert.assertFalse(targetDir.getAbsolutePath().equals(filter.getClassesDirectory().getAbsolutePath()));

		System.setProperty("classesDirectory", targetDir.getAbsolutePath());
		filter = scanRoot();
		Assert.assertEquals(targetDir.getAbsolutePath(), filter.getClassesDirectory().getAbsolutePath());
	}
    
}
