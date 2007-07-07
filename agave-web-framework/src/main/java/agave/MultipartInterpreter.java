/*
 * Copyright (c) 2005 - 2007 Damian Carrillo.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of the <ORGANIZATION> nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
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
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interprets a multipart form post input stream. This is typically obtained
 * by calling {@code request.getInputStream()}.  A multipart request has 
 * very limited support since the Servlet API appears to do nothing with a 
 * multipart request.  The typical form properties that are not file uploads
 * need to be interpreted as well or else they are lost.  They DO NOT appear
 * in the parameter map that you get from the request object.  So, this 
 * class interprets those properties and also the uploaded files.
 * @author <a href="damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public final class MultipartInterpreter {
	
	private final Map<String, String> properties;
	private final Map<String, File> files;
	
	/**
	 * Constructs an interpreted Multipart input stream.  Basically, send in
	 * a raw input stream and then call the {@code getProperties()} or 
	 * {@code getFiles()} methods to get the interpreted parts
	 *
	 * @param in the input stream with multiple Parts
	 * @param boundary the boundary to use as a separator (taken from the 
	 *        Content-Type HTTP header)
	 * @param deleteTempFiles Whether or not to delete the temporary files
	 *        when the JVM exits
	 *
	 * @throws java.io.IOException
	 * @throws java.lang.IllegalStateException
	 * @throws java.util.NoSuchElementException
	 */
	public MultipartInterpreter(
		final InputStream in, 
		final String boundary,
		boolean deleteTempFiles) 
	throws 
		IOException, 
		IllegalStateException, 
		NoSuchElementException {
			
		properties = new LinkedHashMap<String, String>();
		files = new LinkedHashMap<String, File>();
		
		Scanner scanner = new Scanner(in);
		
		// Consume the initial boundary - it's useless
		scanner.nextLine();
		scanner.useDelimiter(boundary + "(--)?");
		
		// Note: both of the following patterns should be reluctant within
		// the capturing group or else one or the other won't be matched
		
		Pattern filenamePattern = Pattern.compile(".* filename=\"(.*?)\".*");
		Pattern namePattern = Pattern.compile(".* name=\"(.*?)\".*");
		Pattern tmpFilePattern = Pattern.compile("^(.*)\\.(.*)$");
		
		int i = 0;
		
		// iterate over the parts matched with the delimiter above
		while (scanner.hasNext()) {
			
			String line = null;
			String name = null;
			String filename = null;
			
			// read headers until a blank line is encountered
			while (scanner.hasNextLine()) {
				line = scanner.nextLine();
				
				// finished reading headers
				if (line.equals("")) {
					break;
				}
				
				// extract the filename from the headers
				Matcher filenameMatcher = filenamePattern.matcher(line);
				if (filenameMatcher.matches() && filenameMatcher.groupCount() >= 1) {
					filename = filenameMatcher.group(1);
				}
				
				// extract the name from the headers
				Matcher nameMatcher = namePattern.matcher(line);
				if (nameMatcher.matches() && nameMatcher.groupCount() >= 1) {
					name = nameMatcher.group(1);
				}
			}
			
			// read parts
			if (name != null) {
				if (filename != null) {
					Matcher tmpFileMatcher = tmpFilePattern.matcher(filename);
					if (tmpFileMatcher.matches() && tmpFileMatcher.groupCount() >= 2) {
						String tmpName = tmpFileMatcher.group(1);
						String tmpExt = tmpFileMatcher.group(2);
						
						if (tmpName.length() < 3) {
							tmpName += "000";
						}
						
						File tmp = File.createTempFile(tmpName + ".", "." + tmpExt);
						
						if (deleteTempFiles) {
							tmp.deleteOnExit();
						}
						
						FileWriter fw = new FileWriter(tmp);
						
						if (scanner.hasNext()) {
							fw.write(scanner.next());
						}
						
						fw.close();
						getFiles().put(name, tmp);
					}
				} else {
					if (scanner.hasNextLine()) {
						String value = scanner.nextLine();
						getProperties().put(name, value);
					}
				}
			}
		}
	}
	
	/**
	 * @return a map of interpreted properties
	 */
	public final Map<String, String> getProperties() {
		return properties;
	}
	
	/**
	 * @return a map of interpreted files
	 */
	public final Map<String, File> getFiles() {
		return files;
	}
	
}
