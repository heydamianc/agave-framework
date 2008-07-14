/*
 * Copyright (c) 2007 Damian Carrillo.	All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *	* Redistributions of source code must retain the above copyright notice,
 *	  this list of conditions and the following disclaimer.
 *	* Redistributions in binary form must reproduce the above copyright notice,
 *	  this list of conditions and the following disclaimer in the documentation
 *	  and/or other materials provided with the distribution.
 *	* Neither the name of the <ORGANIZATION> nor the names of its contributors
 *	  may be used to endorse or promote products derived from this software
 *	  without specific prior written permission.
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
package agave;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interprets a multipart form post input stream. This is typically obtained
 * by calling {@code request.getInputStream()}.	 A multipart request has
 * very limited support since the Servlet API appears to do nothing with a
 * multipart request.  The typical form properties that are not file uploads
 * need to be interpreted as well or else they are lost.  They DO NOT appear
 * in the parameter map that you get from the request object.  So, this
 * class interprets those properties and also the uploaded files.
 * @author <a href="damiancarrillo@gmail.com">Damian Carrillo</a>
 */
class SimpleMultipartInterpreter {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleMultipartInterpreter.class);

	private static final String FILENAME = "filename";
	private static final String NAME = "name";

	private Pattern boundaryPattern;

	/*
	 * A simple grouping of headers and either a string or a file value.
	 */
	private class Part {

		Map<String, String> headers;
		String parameterValue;
		File fileValue;
	}

	private class CoupledLine {

		List<Integer> bytes;
		StringBuilder text;

		CoupledLine() {
			bytes = new ArrayList<Integer>();
			text = new StringBuilder();
		}
	}

	// Properties ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	private Map<String, File> files;
	private Map<String, String> parameters;
	private String boundary;
	private InputStream in;

	// Constructor ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	/**
	 *
	 * @param in
	 * @param boundary
	 * @throws java.io.IOException
	 */
	SimpleMultipartInterpreter(InputStream in, String boundary) throws IOException {
		if (in == null) {
			throw new IllegalArgumentException("Unable to read supplied InputStream");
		}

		files = new LinkedHashMap<String, File>();
		parameters = new LinkedHashMap<String, String>();
		this.boundary = boundary;
		this.in = in;

		// position the stream to be just after the intial boundary marker
		CoupledLine line = readLine();
		if (!line.text.toString().contains(boundary)) {
			throw new IOException("Input is missing an initial boundary marker");
		}
		
		/*
		while ((line = readLine()) != null) {
			LOGGER.info(line.text.toString());
		}
		*/

		Part p = null;
		while ((p = readPart()) != null) {
			String filename = p.headers.get(FILENAME);
			
			if (filename != null) {
				files.put(p.headers.get(NAME), p.fileValue);
			} else {
				parameters.put(p.headers.get(NAME), p.parameterValue);
			}
		}
	}

	// Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	/*
	 *
	 */
	private Part readPart() throws IOException {
		CoupledLine line = null;
		Map<String, String> headers = new HashMap<String, String>();
		while ((line = readLine()) != null && line.text != null && 
			!line.text.toString().trim().equals("")) {
			parseHeader(line.text.toString(), headers);
		}

		Part p = null;

		if (!headers.isEmpty()) {
			p = new Part();
			p.headers = headers;
			String filename = headers.get(FILENAME);
			
			//if (headers.containsKey(FILENAME) && 
			//	(filename == null || filename.equals(""))) {
			//	// skip over an empty file upload
			//	LOGGER.debug("Skipping empty file upload");
			//	return p;
			//}
			
			File f = null;
			FileOutputStream fos = null;
			StringBuilder t = null;
			
			if (filename != null) {
				Matcher fnm = Pattern.compile("(.*)\\.(.*)").matcher(filename);
				if (fnm.matches() && fnm.groupCount() >= 2) {
					String prefix = fnm.group(1);
					if (prefix.length() < 3) {
						prefix += "000";
					}
					String suffix = fnm.group(2);
					f = File.createTempFile(prefix + ".", "." + suffix);
					f.deleteOnExit();
					fos = new FileOutputStream(f);
				}
			} else {
				t = new StringBuilder();
			}
			
			while ((line = readLine()) != null && !line.text.toString().contains(boundary)) {
				if (headers.containsKey(FILENAME)) {
					// If we are not just reading the part section for posterity
					if (fos != null) {
						for (Integer i : line.bytes) {
							fos.write(i);
						}
					}
				} else {
					t.append(line.text);
				}
			}
			
			if (headers.containsKey(FILENAME)) {
				if (fos != null) {
					fos.close();
				}
				p.fileValue = f;
			} else {
				p.parameterValue = t.toString().trim();
			}
		}

		return p;
	}

	/*
	 *
	 */
	private CoupledLine readLine() throws IOException {
		CoupledLine cl = new CoupledLine();
		int b = -1;
		
		while ((b = in.read()) != -1) {
			cl.bytes.add(b);
			cl.text.append((char)b);
			
			if ((char)b == '\n') {
				break;
			}
		}
		return (cl.bytes.isEmpty() ? null : cl);
	}

	/*
	 *
	 */
	private void parseHeader(String line, Map<String, String> headers) {
		Pattern hp = Pattern.compile("\\s*(.*){1}?\\s*:\\s*(.*)\\s*");
		Matcher hm = hp.matcher(line);
		if (hm.matches() && hm.groupCount() >= 2) {
			String name = hm.group(1);
			String value = hm.group(2);
			
			String[] additional = value.split(";");
			if (additional.length >= 1) {
				value = additional[0];
			
				headers.put(name, value);
				
				Pattern ap = Pattern.compile("\\s*(.*)=\"(.*)\";?");
				for (int i = 1; i < additional.length; i++) {
					Matcher am = ap.matcher(additional[i]);
					if (am.matches() && am.groupCount() >= 2) {
						name = am.group(1).trim();
						value = am.group(2).trim();
						headers.put(name, value);
					}
				}
			}
		}
	}

	// Accessors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	/**
	 *
	 * @return
	 */
	Map<String, File> getFiles() {
		return files;
	}

	/**
	 *
	 * @return
	 */
	Map<String, String> getParameters() {
		return parameters;
	}
}
