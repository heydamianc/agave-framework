/*
 * Copyright (c) 2006 - 2007 Damian Carrillo.  All rights reserved.
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
package agave.converters;

import agave.HandlerContext;

import java.io.File;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts the input file into a file accessible by the webapp.  Initially,
 * the uploaded file ends up in the system-wide temp directory, which is 
 * typically not accessible from a webapp.
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 * @since 1.0
 */
public class UploadedFileConverter implements Converter<File, File> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UploadedFileConverter.class);

	public File convert(HandlerContext ctx, File uploadedFile) 
	throws ConversionException {
		ServletContext sctx = ctx.getServletContext();
		
		File uploadDir = new File(sctx.getRealPath("/"));
		
		String uploadPath = ctx.getConfiguration().get("upload-directory");
		String[] uploadPathDirs = uploadPath.split(File.pathSeparator);
		
		for (String traversedDir : uploadPath.split("/")) {
			uploadDir = new File(uploadDir, traversedDir);
			if (!uploadDir.exists()) {
				uploadDir.mkdir();
			}
		}
		
		File moved = new File(uploadDir, uploadedFile.getName());
		uploadedFile.renameTo(moved);
		
		LOGGER.debug("Moved uploaded file to " + moved.getAbsolutePath());
		return moved;
	}
	
}
