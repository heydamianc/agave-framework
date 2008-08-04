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

import java.io.InputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jmock.Expectations;
import org.jmock.Mockery;

public class MultipartParserTest {
    
    @Test
    public void testMultipartScannerWithJettySample() throws Exception {

        InputStream sampleStream = 
            getClass().getClassLoader().getResourceAsStream("multipart-sample-jetty"); 
        Assert.assertNotNull(sampleStream);

        MultipartParser parser = new MultipartParserImpl();
        parser.parseInput(sampleStream);
        
        Assert.assertNotNull(parser.getParameters());
        Assert.assertNotNull(parser.getParts());
        Assert.assertEquals(2, parser.getParameters().size());
        Assert.assertEquals(2, parser.getParts().size());

        for (String paramName : parser.getParameters().keySet()) {
            if ("text1".equals(paramName)) {
                Collection<String> paramValues = parser.getParameters().get(paramName);
                Assert.assertTrue(paramValues.contains("test 1"));
            } else if ("text2".equals(paramName)) {
                Collection<String> paramValues = parser.getParameters().get(paramName);
                Assert.assertTrue(paramValues.contains("test 2"));

            }
        }
    }

    public void testMultipartScannerWithJettyImg() throws Exception {

        InputStream sampleStream = 
           getClass().getClassLoader().getResourceAsStream("multipart-sample-jetty"); 
        MultipartParser parser = new MultipartParserImpl();
        parser.parseInput(sampleStream);

        InputStream basis =
            getClass().getClassLoader().getResourceAsStream("vim.gif");

        Assert.assertNotNull(sampleStream);
        InputStream imgStream = new FileInputStream(parser.getParts().get("file1").getContents());
       
        int bb = -1; 
        int i = 0;
        while ((bb = basis.read()) != -1) {
            int pb = imgStream.read();
            Assert.assertEquals(bb, pb);
            i++;
        }
        Assert.assertTrue(i > 0);
    }
    
}
