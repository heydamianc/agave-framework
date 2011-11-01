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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.DelegatingServletInputStream;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import co.cdev.agave.MultipartRequest;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class MultipartRequestTest {

    final static String CONTENT_TYPE =
            "multipart/form-data; boundary=---------------------------2746393686911676941624173958";
    final InputStream testStream = getClass().getClassLoader().getResourceAsStream("multipart-parameter-test");
    final ServletInputStream delegatingStream = new DelegatingServletInputStream(testStream);
    Mockery context = new Mockery();
    HttpServletRequest request;

    @Before
    public void setup() {
        request = context.mock(HttpServletRequest.class);
    }

    @Test
    public void testGetParameter() throws Exception {
        context.checking(new Expectations() {

            {
                allowing(request).getContentType();
                will(returnValue(CONTENT_TYPE));
                allowing(request).getInputStream();
                will(returnValue(delegatingStream));
                allowing(request).getParameter("where");
                will(returnValue("over there"));
            }
        });

        MultipartRequest multipartRequest = new MultipartRequestImpl(request);
        Assert.assertNotNull(multipartRequest);
        Assert.assertEquals("because", multipartRequest.getParameter("why"));
        Assert.assertEquals("forever", multipartRequest.getParameter("when"));
        Assert.assertEquals("over there", multipartRequest.getParameter("where"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetParameterMap() throws Exception {
        final Map<String, String[]> spm = new HashMap<String, String[]>(); // like the one in servlet request
        spm.put("a", new String[]{"a", "b"});
        spm.put("b", new String[]{"1", "2"});

        context.checking(new Expectations() {

            {
                allowing(request).getContentType();
                will(returnValue(CONTENT_TYPE));
                allowing(request).getInputStream();
                will(returnValue(delegatingStream));
                allowing(request).getParameterMap();
                will(returnValue(spm));
            }
        });

        MultipartRequest multipartRequest = new MultipartRequestImpl(request);
        Map<String, String[]> parameterMap = multipartRequest.getParameterMap();

        Assert.assertNotNull(parameterMap);
        Assert.assertEquals(4, parameterMap.size());
        Assert.assertArrayEquals(new String[]{"a", "b"}, parameterMap.get("a"));
        Assert.assertArrayEquals(new String[]{"1", "2"}, parameterMap.get("b"));
        Assert.assertArrayEquals(new String[]{"because"}, parameterMap.get("why"));
        Assert.assertArrayEquals(new String[]{"forever"}, parameterMap.get("when"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetParameterNames() throws Exception {
        ArrayList<String> names = new ArrayList<String>();
        names.add("a");
        names.add("b");
        names.add("why");
        names.add("when");

        final Map<String, String[]> spm = new HashMap<String, String[]>();
        spm.put("a", new String[]{"a", "b"});
        spm.put("b", new String[]{"1", "2"});

        context.checking(new Expectations() {

            {
                allowing(request).getContentType();
                will(returnValue(CONTENT_TYPE));
                allowing(request).getInputStream();
                will(returnValue(delegatingStream));
                allowing(request).getParameterMap();
                will(returnValue(spm));
            }
        });

        MultipartRequest multipartRequest = new MultipartRequestImpl(request);
        Enumeration<String> parameterNames = multipartRequest.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            Assert.assertTrue(names.contains(parameterName));
        }
    }

    @Test
    public void testGetParameterValues() throws Exception {
        final Map<String, String[]> spm = new HashMap<String, String[]>(); // like the one in servlet request
        spm.put("a", new String[]{"a", "b"});
        spm.put("b", new String[]{"1", "2"});

        context.checking(new Expectations() {

            {
                allowing(request).getContentType();
                will(returnValue(CONTENT_TYPE));
                allowing(request).getInputStream();
                will(returnValue(delegatingStream));
                allowing(request).getParameterMap();
                will(returnValue(spm));
            }
        });

        MultipartRequest multipartRequest = new MultipartRequestImpl(request);
        Assert.assertArrayEquals(new String[]{"a", "b"}, multipartRequest.getParameterValues("a"));
        Assert.assertArrayEquals(new String[]{"1", "2"}, multipartRequest.getParameterValues("b"));
        Assert.assertArrayEquals(new String[]{"because"}, multipartRequest.getParameterValues("why"));
        Assert.assertArrayEquals(new String[]{"forever"}, multipartRequest.getParameterValues("when"));
    }

    protected void finalize() throws Exception {
        testStream.close();
        delegatingStream.close();
    }
}
