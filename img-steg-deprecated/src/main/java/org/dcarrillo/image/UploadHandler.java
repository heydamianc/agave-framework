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

import agave.FormHandler;
import agave.HandlerContext;
import agave.HandlerException;
import agave.StringTemplateHandler;
import agave.annotations.Converters;
import agave.annotations.Path;
import agave.annotations.Template;
import agave.converters.UploadedFileConverter;

import javax.servlet.http.HttpSession;

import java.io.File;
import java.io.IOException;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

/**
 * Processes an uploaded image and performs one of two actions on it:
 *   1. Encodes some text into that image by interpolating UTF-16 character 
 *      bits into the least significant digit of the four color components,
 *      in an ARGB color scheme (Alpha, Red, Green, and Blue)
 *   2. Decodes interpolated text within an image and displays it onscreen.
 * Interpolating characters in an image like this is called 'steganography'.
 * Note that this class is dually bound to the "/upload" path for both a 
 * Get and a Post, but multiple instances of this handler are created for 
 * each HTTP method.
 * @see http://wikipedia.org/Steganography for more information on this 
 *      technique and what it's used for.
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
@Path("/upload") 
@Template(path = "/", name="upload")
public class UploadHandler extends StringTemplateHandler implements FormHandler {
	
	private File image;
	private Action action;
	private String message;
	
	/**
	 * Sets the uploaded file image to be in a location that is easily accessible
	 * with a relative reference.  This specific webapp is configured to drop
	 * uploaded images in the ${webappDir}/images/upload.  Typically the servlet
	 * container determins the ${webappDir}, but you can probably override that
	 * with a custom servlet container configuration.  The '/images/upload' is
	 * specified as an init-param to the HandlerManager in the web.xml.
	 */
	@Converters({UploadedFileConverter.class})
	public void setImage(File image) {
	    this.image = image;
	}

	public File getImage() {
	    return image;
	}
	
	@Converters({ActionConverter.class})
	public void setAction(Action action) {
	    this.action = action;
	}

	public Action getAction() {
	    return action;
	}
	
	public void setMessage(String message) {
	    this.message = message;
	}

	public String getMessage() {
	    return message;
	}

	/**
	 * Prepares the the template to be displayed on-screen.  Nothing is done in this handler
	 * because the StringTemplateHandler abstract class automatically transports the Session
	 * attributes, Request attributes, and the named fields of the handler class into the 
	 * StringTemplate instance (in that order).
	 * @param context The context in which this handler runs
	 * @param template The template that will be instantiated and ultimately rendered
	 * @throws agave.HandlerException if something goes wrong
	 */
	public void prepareTemplate(HandlerContext context, StringTemplate template) 
	throws HandlerException {
	}
	
	/**
	 * Processes the upload form and either encodes the textual message or decodes the message
	 * and stores it in the session.  Note that after handling the http POST, control will be
	 * given to the Agave Framework which will redirect to the ResourceHandler that is mapped
	 * to the return value of this method.  This one always redirects to the UploadHandler
	 * prepareTemplate method above, but two instances of this class are necesary to do so. 
	 * Don't expect local variables to persist through the process, because of the fact that
	 * two instances are used.
	 * @param context the context in which this handler runs in
	 * @return the handler path to redirect to (in the Post-Redirect-Get pattern)
	 * @throws agave.HandlerException if anything goes wrong
	 */
	public String process(HandlerContext context) throws HandlerException {
		// Ideally this would come from JNDI or Spring so that the actual implementation
		// would not be hardwired into this handler.
		ImageService imageService = new ImageServiceImpl();

		HttpSession session = context.getRequest().getSession(true);
		session.setAttribute("image", getImage());
		session.setAttribute("action", getAction());
		session.setAttribute("message", getMessage());

		if (getAction().equals(Action.encode)) {
			File encodedImage = null;
			try {
				encodedImage = imageService.encode(getImage(), getMessage());
				session.setAttribute("encodedImage", encodedImage);
			} catch (Exception ex) {
				throw new HandlerException(ex);
			}
		} else {
			String decodedMessage = null;
			try {
				decodedMessage = imageService.decode(getImage());
				session.setAttribute("decodedMessage", decodedMessage);
			} catch (Exception ex) {
				throw new HandlerException(ex);
			}
		}

		return "/upload";
	}
	
}
