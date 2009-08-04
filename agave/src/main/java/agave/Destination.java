/*
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
package agave;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

/**
 * Represents the destination of a <a href="package-summary.html#handlerMethod">handler method</a>.  
 * This class serves as a unifying facade for redirecting and forwarding in the Servlet API 
 * and working with request parameters.
 * 
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 * 
 * @see <a href="Destinations.html#forward(java.lang.String)">Destinations.forward()</a>
 * @see <a href="Destinations.html#redirect(java.lang.String)">Destinations.redirect()</a>
 */
public interface Destination {

	/**
	 * Adds a parameter to the {@code Destination}.  Multi-valued parameters are supported
	 * and will be serialized into a query string with the same parameter name and multiple
	 * values, eg: {@code /somePath?dogs=woof&dogs=bark&cats=meow&cats=purr}
	 * @param name the name of the parameter
	 * @param value the value of the parameter
	 */
	public abstract void addParameter(String name, String value);

	public abstract String getPath();

	public abstract void setPath(String path);

	public abstract Map<String, List<String>> getParameters();

	public abstract void setParameters(Map<String, List<String>> parameters);

	public abstract Boolean getRedirect();

	public abstract void setRedirect(Boolean redirect);

	/**
	 * Encodes the {@code Destination} path and available parameters. The parameters have any ampersands
	 * replaced with &{@code &amp;amp;} and parameter names and their associated values are sorted (mainly for
	 * testing purposes).
	 * 
	 * @param context the handler context that this destination is running under
	 * @return the encoded destination path and query string
	 */
	public abstract String encode(ServletContext context);

}
