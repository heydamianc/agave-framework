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
package agave.samples;

import java.io.File;

import org.apache.commons.lang.RandomStringUtils;

import agave.AbstractHandler;
import agave.Destination;
import agave.HandlesRequestsTo;
import agave.Part;

public class StegoHandler extends AbstractHandler {

	public static final String ENCODED_FILENAME_PREFIX = "enc.";
	public static final String USER_SUBMITTED_IMAGE_DIR = "/img/submitted/";
	
    @HandlesRequestsTo("/")
    public Destination welcome() throws Exception {
        return new Destination("/WEB-INF/jsp/index.jsp");
    }
    
    @HandlesRequestsTo("/obscure")
    public Destination obscure(StegoForm form) throws Exception {
    	Part carrierPart = form.getCarrier();
    	
    	String filename = generateRandomizedFilename(carrierPart);
        File carrier = new File(servletContext.getRealPath(USER_SUBMITTED_IMAGE_DIR + filename));
        carrierPart.getContents().renameTo(carrier);
        
	    request.getSession(true).setAttribute("filename", 
	    		request.getContextPath() + USER_SUBMITTED_IMAGE_DIR + filename);
	    
	    ImageStegoService stegoService = ImageStegoServiceFactory.createImageStegoService();
	    File encodedCarrier = stegoService.encode(carrier, form.getPayload(), ENCODED_FILENAME_PREFIX);
	    encodedCarrier.renameTo(new File(USER_SUBMITTED_IMAGE_DIR + encodedCarrier.getName()));
	    
	    request.getSession().setAttribute("encodedFilename", 
	    		request.getContextPath() + USER_SUBMITTED_IMAGE_DIR + encodedCarrier.getName());
        
	    request.getSession().removeAttribute("extractedPayload");
	    
        return new Destination("/", true);
    }
    
    private String generateRandomizedFilename(Part carrierPart) {
        String[] filenameParts = carrierPart.getFilename().split("\\.");
        String filename = carrierPart.getName() + "-" + RandomStringUtils.randomAlphanumeric(4) +
        	"." + filenameParts[filenameParts.length - 1];
        return filename;
    }
    
    @HandlesRequestsTo("/extract")
    public Destination extract(StegoForm form) throws Exception {
    	ImageStegoService stegoService = ImageStegoServiceFactory.createImageStegoService();
    	String extractedPayload = stegoService.decode(form.getCarrier().getContents());
    	request.getSession(true).setAttribute("extractedPayload", extractedPayload);
    	
    	request.getSession().removeAttribute("filename");
    	request.getSession().removeAttribute("encodedFilename");
    	
        return new Destination("/", true);
    }
    
}