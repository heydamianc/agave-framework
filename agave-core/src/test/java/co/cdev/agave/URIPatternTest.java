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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class URIPatternTest {

    @Test // transitively checks testNormalizePattern
    public void testConstructor() throws Exception {
        assertEquals("/", new URIPatternImpl("/").toString());
        assertEquals("/", new URIPatternImpl("/").toString());
        assertEquals("/one", new URIPatternImpl("/one").toString());
        assertEquals("/one", new URIPatternImpl("/one/").toString());
        assertEquals("/one", new URIPatternImpl("/one/.").toString());
        assertEquals("/one", new URIPatternImpl("/one/./").toString());
        assertEquals("/one", new URIPatternImpl("/one/two/..").toString());
        assertEquals("/one", new URIPatternImpl("/one/two/../").toString());
        assertEquals("/one", new URIPatternImpl("/one/two/../three/four/../../").toString());
        assertEquals("/one", new URIPatternImpl("/one").toString());
        assertEquals("/one/*", new URIPatternImpl("/one/*").toString());
        assertEquals("/one/*", new URIPatternImpl("/one/*/").toString());
        assertEquals("/one/**", new URIPatternImpl("/one/**").toString());
        assertEquals("/one/**", new URIPatternImpl("/one/**/").toString());
        assertEquals("/one/**", new URIPatternImpl("/one/**/*").toString());
        assertEquals("/one/**", new URIPatternImpl("/one/**/**").toString());
        assertEquals("/one/**", new URIPatternImpl("/one/*/**/*/").toString());
    }
    
    @Test(expected = NullPointerException.class)
    public void testConstructor_withNullPattern() throws Exception  {
        new URIPatternImpl(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_withIllegalArgument() throws Exception {
        new URIPatternImpl("");
    }
    
    @Test // for posterity
    public void testNormalizeURI() throws Exception {
        // widen visibility of normalizeURI so we can call it directly
        URIPattern pattern = new URIPatternImpl("/") {
            private static final long serialVersionUID = 1L;

            public String normalizeURI(String uri) {
                return super.normalizeURI(uri);
            }
        };
        assertEquals("/", pattern.normalizeURI("/"));
        assertEquals("/one", pattern.normalizeURI("/one"));
        assertEquals("/one", pattern.normalizeURI("/one/"));
        assertEquals("/one", pattern.normalizeURI("/one/."));
        assertEquals("/one", pattern.normalizeURI("/one/./"));
        assertEquals("/one", pattern.normalizeURI("/one/two/.."));
        assertEquals("/one", pattern.normalizeURI("/one/two/../"));
        assertEquals("/one", pattern.normalizeURI("/one/two/../three/four/../../"));
        assertEquals("/one", pattern.normalizeURI("/one"));
    }
    
    @Test // just making sure this works as expected
    public void testSplit() throws Exception {
        Assert.assertArrayEquals(new String[] {}, "/".split("/"));
    }
    
    @Test(expected = IllegalArgumentException.class) 
    public void testMatchesWithIllegalURI() throws Exception {
        new URIPatternImpl("one/two"); // this is a malformed URI
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testMatchesWithNondeterministicURI() throws Exception {
        new URIPatternImpl("/one/two/**/${var}/seven"); // this is a nondeterministic URI
    }
    
    @Test
    public void testCompareTo() throws Exception {
        assertTrue(new URIPatternImpl("/").compareTo(new URIPatternImpl("/")) == 0);
        assertTrue(new URIPatternImpl("/one/two/three").compareTo(new URIPatternImpl("/")) < 0);
        assertTrue(new URIPatternImpl("/").compareTo(new URIPatternImpl("/one/two/three")) > 0);
        assertTrue(new URIPatternImpl("/one/two/${var}").compareTo(new URIPatternImpl("/one/${var}/three")) < 0);
        assertTrue(new URIPatternImpl("/one/two/${var}").compareTo(new URIPatternImpl("/one/${var}/")) < 0);
        assertTrue(new URIPatternImpl("/one/two/*").compareTo(new URIPatternImpl("/one/")) < 0);
        assertTrue(new URIPatternImpl("/one/two/*").compareTo(new URIPatternImpl("/one/two/")) < 0);
        assertTrue(new URIPatternImpl("/one/two/*").compareTo(new URIPatternImpl("/one/two/three")) > 0);
        assertTrue(new URIPatternImpl("/one/two/*/three").compareTo(new URIPatternImpl("/one/two/three/*")) > 0);
        assertTrue(new URIPatternImpl("/one/two/three/*").compareTo(new URIPatternImpl("/one/two/*/three")) < 0);
        assertTrue(new URIPatternImpl("/").compareTo(new URIPatternImpl("/*")) > 0);
        assertTrue(new URIPatternImpl("/*").compareTo(new URIPatternImpl("/")) < 0);
        assertTrue(new URIPatternImpl("/one/*").compareTo(new URIPatternImpl("/blah/*")) > 0);
        assertTrue(new URIPatternImpl("/**").compareTo(new URIPatternImpl("/")) < 0);
        assertTrue(new URIPatternImpl("/one/**").compareTo(new URIPatternImpl("/one/")) < 0);
        assertTrue(new URIPatternImpl("/one/**").compareTo(new URIPatternImpl("/one/two")) > 0);
        assertTrue(new URIPatternImpl("/one/**").compareTo(new URIPatternImpl("/blah/**")) > 0);
        assertTrue(new URIPatternImpl("/one/**/two/three/*").compareTo(new URIPatternImpl("/blah/**")) < 0);
        assertTrue(new URIPatternImpl("/one/**/two/*").compareTo(new URIPatternImpl("/blah/**/blah")) < 0);
        assertTrue(new URIPatternImpl("/a").compareTo(new URIPatternImpl("/b")) < 0);
        assertTrue(new URIPatternImpl("/b").compareTo(new URIPatternImpl("/a")) > 0);
        assertTrue(new URIPatternImpl("/A").compareTo(new URIPatternImpl("/a")) == 0);
        assertTrue(new URIPatternImpl("/init").compareTo(new URIPatternImpl("/${uniqueId}")) < 0);
        assertTrue(new URIPatternImpl("/${uniqueId}").compareTo(new URIPatternImpl("/init")) > 0);
        assertTrue(new URIPatternImpl("/${uniqueId1}").compareTo(new URIPatternImpl("/${uniqueId2}")) == 0);
    }

    @Test
    public void testEquals() throws Exception {
        assertTrue(new URIPatternImpl("/").equals(new URIPatternImpl("/")));
        assertTrue(new URIPatternImpl("/test1").equals(new URIPatternImpl("/test1")));
        assertTrue(!new URIPatternImpl("/test1").equals(new URIPatternImpl("/test2")));
    }
    
    @Test
    public void testSerialize() throws Exception {
        URIPattern uriPattern = new URIPatternImpl("/some/pattern");
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(bout));
        out.writeObject(uriPattern);
    }
    
    @Test
    public void testDeserialize() throws Exception {
        assertDeserialize(new URIPatternImpl("/"));
        assertDeserialize(new URIPatternImpl("/some/pattern"));
    }
    
    private void assertDeserialize(URIPattern expected) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(bout));
        out.writeObject(expected);
        out.close();
        
        byte[] bytes = bout.toByteArray();
        
        assertTrue(bytes.length > 0);
        
        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(bin));
        URIPattern deserializedURIPattern = (URIPattern) in.readObject();
        in.close();
        
        assertEquals(expected, deserializedURIPattern);
    }
    
}

