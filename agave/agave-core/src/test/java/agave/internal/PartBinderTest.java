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

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import agave.MultipartRequest;
import agave.conversion.BufferedImageConverter;
import agave.conversion.Converter;
import agave.sample.ImageUploadForm;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class PartBinderTest {

    Mockery context; 
    final HttpServletRequest request;
    final HandlerDescriptor descriptor;
    final Map<String, Method> mutators;
    final Map<String, String[]> parameters;
    final Map<String, Class<? extends Converter<?,?>>> converters;
    final Map<String, Part> parts;
    final File sampleImage;
    final MultipartRequest multipartRequest;

    public PartBinderTest() throws Exception {
        context = new Mockery();
        request = context.mock(HttpServletRequest.class);
        descriptor = context.mock(HandlerDescriptor.class);
        mutators = new HashMap<String, Method>();
        parameters = new HashMap<String, String[]>();
        converters = new HashMap<String, Class<? extends Converter<?,?>>>();
        parts = new HashMap<String, Part>();
        sampleImage = new File(getClass().getClassLoader().getResource("vim.gif").toURI());
        multipartRequest = context.mock(MultipartRequest.class);
    }

    @Before
    public void setup() throws Exception {
        mutators.clear();
        parameters.clear();
        converters.clear();
    }

    @Test
    public void testBindRequestPartsWithoutConversion() throws Exception {
    	ImageUploadForm uploadForm = new ImageUploadForm();
    	PartBinder binder = new PartBinderImpl(uploadForm, descriptor);
    	
    	String identifier = "file";
    	
    	mutators.put(identifier, ImageUploadForm.class.getMethod("setFile1", File.class));
    	
    	Part part = new PartImpl();
    	part.setContents(sampleImage);
    	part.setName(identifier);
    	part.setFilename("vim.gif");
    	part.setContentType("image/gif");
    	parts.put(part.getName(), part);
    	
    	final URIPattern pattern = new URIPatternImpl("/fileUpload");
    	
    	context.checking(new Expectations() {{
            allowing(descriptor).getMutators(); will(returnValue(mutators));
            allowing(descriptor).getConverters(); will(returnValue(converters));
            allowing(descriptor).getPattern(); will(returnValue(pattern));
            allowing(request).getRequestURI(); will(returnValue("/fileUpload/"));
            allowing(multipartRequest).getParts(); will(returnValue(parts));
    	}});
    	
    	binder.bindParts(multipartRequest);
    	Assert.assertNotNull(uploadForm.getFile1());
    	Assert.assertEquals(sampleImage, uploadForm.getFile1());
    	Assert.assertTrue(uploadForm.getFile1().getTotalSpace() > 0);
    }
    
    @Test
    public void testBindRequestPartsWithConversion() throws Exception {
    	ImageUploadForm uploadForm = new ImageUploadForm();
    	PartBinder binder = new PartBinderImpl(uploadForm, descriptor);
    	
    	String identifier = "image";
    	
    	mutators.put(identifier, ImageUploadForm.class.getMethod("setFile2", BufferedImage.class));
    	converters.put(identifier, BufferedImageConverter.class);
    	
    	Part part = new PartImpl();
    	part.setContents(sampleImage);
    	part.setName(identifier);
    	part.setFilename("vim.gif");
    	part.setContentType("image/gif");
    	parts.put(part.getName(), part);
    	
    	final URIPattern pattern = new URIPatternImpl("/imgUpload");
    	
    	context.checking(new Expectations() {{
            allowing(descriptor).getMutators(); will(returnValue(mutators));
            allowing(descriptor).getConverters(); will(returnValue(converters));
            allowing(descriptor).getPattern(); will(returnValue(pattern));
            allowing(request).getRequestURI(); will(returnValue("/imgUpload/"));
            allowing(multipartRequest).getParts(); will(returnValue(parts));
    	}});
    	
    	binder.bindParts(multipartRequest);
    	Assert.assertNotNull(uploadForm.getFile2());
    	Assert.assertEquals(32, uploadForm.getFile2().getHeight(null));
    	Assert.assertEquals(32, uploadForm.getFile2().getWidth(null));
    }

}
