/*
 * Copyright (c) 2005 - 2007 Damian Carrillo.  All rights reserved.
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

import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class HandlerContext {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HandlerContext.class);
	
	private Map<String, String> configuration;
	private ServletContext servletContext;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private String requestedPath;
	private String matchedPath;
	
	private FormHandler formHandler;
	
	private Map<String, Class<? extends ResourceHandler>> resourceHandlers;
	private Map<String, Class<? extends FormHandler>> formHandlers;
	
	public HandlerContext(
			Map<String, String> configuration,
			ServletContext servletContext,
			HttpServletRequest request,
			HttpServletResponse response,
			String requestedPath,
			String matchedPath,
			Map<String, Class<? extends ResourceHandler>> resourceHandlers,
			Map<String, Class<? extends FormHandler>> formHandlers) {
		setConfiguration(configuration);
		setServletContext(servletContext);
		setRequest(request);
		setResponse(response);
		setRequestedPath(requestedPath);
		setMatchedPath(matchedPath);
		setResourceHandlers(resourceHandlers);
		setFormHandlers(formHandlers);
	}
	
	public void setConfiguration(Map<String, String> configuration) {
	    this.configuration = configuration;
	}

	public Map<String, String> getConfiguration() {
	    return configuration;
	}
	
	
	public ServletContext getServletContext() {
		return servletContext;
	}
	
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	public HttpServletRequest getRequest() {
		return request;
	}
	
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public HttpServletResponse getResponse() {
		return response;
	}
	
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	public String getRequestedPath() {
		return requestedPath;
	}
	
	public void setRequestedPath(String requestedPath) {
		this.requestedPath = requestedPath;
	}
	
	public Map<String, Class<? extends ResourceHandler>> getResourceHandlers() {
		return resourceHandlers;
	}
	
	public void setResourceHandlers(Map<String, Class<? extends ResourceHandler>> resourceHandlers) {
		this.resourceHandlers = resourceHandlers;
	}
	
	public Map<String, Class<? extends FormHandler>> getFormHandlers() {
		return formHandlers;
	}
	
	public void setFormHandlers(Map<String, Class<? extends FormHandler>> formHandlers) {
		this.formHandlers = formHandlers;
	}

	public String getMatchedPath() {
		return matchedPath;
	}

	public void setMatchedPath(String matchedPath) {
		this.matchedPath = matchedPath;
	}

	public FormHandler getFormHandler() {
		return formHandler;
	}

	public void setFormHandler(FormHandler formHandler) {
		this.formHandler = formHandler;
	}
	
}
