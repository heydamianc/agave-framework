/*
 * Copyright (c) 2012, Damian Carrillo
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

import static co.cdev.agave.URIPattern.REPLACEMENT_PATTERN;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import javax.servlet.http.HttpServletRequest;

public class URIParamExtractorImpl implements URIParamExtractor {

    private final URIPattern uriPattern;
    
    public URIParamExtractorImpl(final URIPattern uriPattern) {
        this.uriPattern = uriPattern;
    }
    
    @Override
    public Map<String, String> extractParams(HttpServletRequest request) {
        String uri = request.getServletPath();
        Map<String, String> parameterMap = new HashMap<String, String>();
        if (uriPattern.getParts() != null && uri != null && uri.length() > 1) {
            String[] requestedParts = uri.substring(1).split("/");
            if (requestedParts.length >= uriPattern.getParts().length) {
                for (int i = 0; i < uriPattern.getParts().length; i++) {
                    Matcher matcher = REPLACEMENT_PATTERN.matcher(uriPattern.getParts()[i]);
                    if (matcher.matches() && matcher.groupCount() > 0) {
                        String paramName = matcher.group(1);
                        String paramValue = requestedParts[i]; 
                        parameterMap.put(paramName, paramValue);
                    }
                }
            }
        }
        return parameterMap;           
    }
    
}
