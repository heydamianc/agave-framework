/*
 * Copyright (c) 2007 Damian Carrillo
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *	 * Redistributions of source code must retain the above copyright notice,
 *	   this list of conditions and the following disclaimer.
 *	 * Redistributions in binary form must reproduce the above copyright
 *	   notice, this list of conditions and the following disclaimer in the
 *	   documentation and/or other materials provided with the distribution.
 *	 * Neither the name of the <ORGANIZATION> nor the names of its
 *	   contributors may be used to endorse or promote products derived from
 *	   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dcarrillo.image;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class ImageServiceTest extends ImageServiceImpl {
	
	final String FNAME = "banksy_losangeles_6.jpg";
	String msg = "Hello, World!";
	
	@Test
	public void testInterpolation() {
		int[] pixels = new int[5];
		int codePoint = (int)'a';
		ImageServiceImpl isvc = new ImageServiceImpl();
		isvc.interpolate(codePoint, pixels, 0);
		
		assertEquals(codePoint, 97);
		assertEquals(pixels[0], 0x1);
		assertEquals(pixels[1], 0x10100);
		assertEquals(pixels[2], 0x0);
		assertEquals(pixels[3], 0x0);
		assertEquals(pixels[4], 0x0);
	}
	
	@Test
	public void testExtraction() {
		int[] pixels = new int[5];
		ImageServiceImpl isvc = new ImageServiceImpl();

		isvc.interpolate((int)'a', pixels, 0);
		int codePoint = isvc.extract(pixels, 0);
		assertEquals(codePoint, 97);
		assertEquals((char)codePoint, 'a');

		pixels = new int[5];
		isvc.interpolate((int)'~', pixels, 0);
		codePoint = isvc.extract(pixels, 0);
		assertEquals((char)codePoint, '~');
	}
	
	@Test
	public void testEncoding() throws Exception {
		URL url = getClass().getClassLoader().getResource(FNAME);
		assertNotNull(url);
		
		File image = new File(url.getPath());
		assertNotNull(image);
		
		ImageService isvc = new ImageServiceImpl();
		File encoded = isvc.encode(image, msg);
		assertNotNull(encoded);
		assertEquals("enc." + FNAME, encoded.getName());
	}
	
	@Test
	public void testDecoding() throws Exception {
		URL url = getClass().getClassLoader().getResource(FNAME);
			
		assertNotNull(url);
		
		File image = new File(url.getPath());
		assertNotNull(image);
		
		ImageService isvc = new ImageServiceImpl();
		File encoded = isvc.encode(image, msg);
		assertNotNull(encoded);
		assertEquals("enc." + FNAME, encoded.getName());

		String decodedMsg = isvc.decode(encoded);

		assertEquals(msg, decodedMsg);
		assertEquals(decodedMsg.length(), msg.length());
	}
	
}
