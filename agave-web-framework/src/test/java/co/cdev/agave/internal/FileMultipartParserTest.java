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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class FileMultipartParserTest {

    Mockery context = new Mockery();
    HttpServletRequest request;

    @Before
    public void setup() throws Exception {
        request = context.mock(HttpServletRequest.class);
    }

    @Test
    public void testParseParameters() throws Exception {
        final String contentType =
                "multipart/form-data; boundary=---------------------------2746393686911676941624173958";

        final InputStream sampleStream =
                new DelegatingServletInputStream(getClass().getClassLoader().getResourceAsStream("multipart-sample-jetty"));
        try {
            context.checking(new Expectations() {{
                allowing(request).getContentType(); will(returnValue(contentType));
                allowing(request).getInputStream(); will(returnValue(sampleStream));
            }});

            MultipartParser<File> parser = new FileMultipartParser();
            parser.parseInput(request);
            
            Assert.assertNotNull(parser.getParameters());
            Assert.assertTrue(!parser.getParameters().isEmpty());
            Assert.assertEquals(1, parser.getParameters().get("text1").size());
            Assert.assertTrue(parser.getParameters().get("text1").contains("test 1"));
            Assert.assertEquals(1, parser.getParameters().get("text2").size());
            Assert.assertTrue(parser.getParameters().get("text2").contains("test 2"));

            Assert.assertEquals(2, parser.getParameters().size());
        } finally {
            sampleStream.close();
        }
    }

    @Test
    public void testParsePart() throws Exception {
        final String contentType =
                "multipart/form-data; boundary=---------------------------2746393686911676941624173958";

        final InputStream sampleStream = new DelegatingServletInputStream(
                getClass().getClassLoader().getResourceAsStream("multipart-sample-jetty"));
        try {
            context.checking(new Expectations() {{
                allowing(request).getContentType(); will(returnValue(contentType));
                allowing(request).getInputStream(); will(returnValue(sampleStream));
            }});

            MultipartParser<File> parser = new FileMultipartParser();
            parser.parseInput(request);

            Assert.assertNotNull(parser.getParts());
            Assert.assertEquals(2, parser.getParts().size());
            Assert.assertNotNull(parser.getParts().get("file1"));
            Assert.assertNotNull(parser.getParts().get("file2"));

            InputStream imgStream = getClass().getClassLoader().getResourceAsStream("vim.gif");
            InputStream file1Stream = new FileInputStream(parser.getParts().get("file1").getContents());
            InputStream file2Stream = new FileInputStream(parser.getParts().get("file2").getContents());

            int b = -1;
            while ((b = imgStream.read()) != -1) {
                Assert.assertEquals(file1Stream.read(), b);
                Assert.assertEquals(file2Stream.read(), b);
            }
        } finally {
            sampleStream.close();
        }
    }
}
