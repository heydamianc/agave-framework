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
package agave.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import agave.MultipartRequest;
import agave.Part;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class MultipartRequestImpl extends HttpServletRequestWrapper implements MultipartRequest {

    private MultipartParser parser;

    private Map<String, String[]> parameterMap;

    public MultipartRequestImpl(HttpServletRequest request) throws IOException {
        super(request);
        try {
            parser = new MultipartParserImpl(request);
            parser.parseInput();
        } finally {
            request.getInputStream().close();
        }
    }

    /**
     * Gets a parameter from the query string or the post data in that order.  In the case of multiple
     * values for a single parameter, the first value is taken, and this initial value will always be 
     * the same.
     * @see #getParameterValues(String) How to access the other values of a multi-valued parameter
     */
    @Override
    public String getParameter(String name) {
        if (getParameters().get(name) != null) {
            for (String value : getParameters().get(name)) {
                return value;
            }
        }
        return super.getParameter(name);
    }

    /**
     * Returns an immutable {@code Map} of parameter names and their values.  There can be multiple
     * values for a single parameter whenever the same parameter name is supplied multiple times.  An 
     * example of this would be a URL with a query string like {@code ?catGoes=meow&catGoes=hiss} or
     * a form with multiple inputs that use the same {@code name}.
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        if (this.parameterMap == null) {
            this.parameterMap = new HashMap<String, String[]>();
            for (Object parameterNameObj : super.getParameterMap().keySet()) {
                String parameterName = parameterNameObj.toString();
                Collection<String> parameterValues = new ArrayList<String>();
                if (super.getParameterMap().get(parameterName) != null) {
                    String[] superParameterValues = (String[])super.getParameterMap().get(parameterName);
                    for (String parameterValue : superParameterValues) {
                        parameterValues.add(parameterValue);
                    }
                }
                if (getParameters().get(parameterName) != null) {
                    for (String parameterValue : getParameters().get(parameterName)) {
                        parameterValues.add(parameterValue);
                    }
                }
                this.parameterMap.put(parameterName, parameterValues.toArray(new String[] {}));
            }
            for (String parameterName : getParameters().keySet()) {
                if (!this.parameterMap.containsKey(parameterName)) {
                    this.parameterMap.put(parameterName, getParameters().get(parameterName).toArray(new String[] {}));
                }
            }
            this.parameterMap = Collections.unmodifiableMap(this.parameterMap);
        }
        return this.parameterMap;
    }
   
    @SuppressWarnings("unchecked")
	@Override
    public Enumeration<String> getParameterNames() {
        Vector<String> parameterNames = new Vector<String>();
        parameterNames.addAll(super.getParameterMap().keySet());
        parameterNames.addAll(getParameters().keySet());
        return parameterNames.elements();
    }

    @Override
    public String[] getParameterValues(String name) {
        return getParameterMap().get(name);
    }

    public Map<String, Collection<String>> getParameters() {
        return parser.getParameters();
    }

    public Map<String, Part> getParts() {
        return parser.getParts();
    }

    public static boolean isMultipart(HttpServletRequest request) {
    	return request.getContentType() != null && request.getContentType().startsWith("multipart/form-data");
    }

    public static boolean isFormURLEncoded(HttpServletRequest request) {
        return "application/x-www-form-urlencoded".equals(request.getContentType());
    }

}
