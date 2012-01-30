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
package co.cdev.agave;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * The {@code URIPattern} is an object constructed around the the argument 
 * to the {@code @HandlesRequestsTo} annotation, and it indicates which 
 * URL the handler method will field, based on the URL constructed by 
 * combining the protocol, port, context, and URI.
 *
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public interface URIPattern extends Comparable<URIPattern> {

    public String getPattern();

    /**
     * Determines whether the supplied requests servlet path matches the pattern 
     * that this {@code URIPattern} describes. The URI string supplied as 
     * an argument must start with a forward slash ('/'). The URI string is 
     * normalized with URI.normalize() from the Java API, then compared against 
     * the stored pattern where wildcards and replacement variables help determine 
     * the match. Replacement variables look like <code>${someVar}</code> and are
     * taken as an automatic match. A single asterisk represents a wildcard
     * match where the supplied token matches automatically as well. A double
     * asterisk matches multiple tokens until the next token in the pattern is
     * matched against the URI.
     *
     * @param request the servlet request
     * @return true if the URI matches this pattern
     */
    public boolean matches(HttpServletRequest request);

    /**
     * Determines whether the supplied URI string matches the pattern that this
     * {@code URIPattern} encapsulates. The URI string supplied as an argument
     * must start with a forward slash ('/'). The URI string is normalized with
     * URI.normalize() from the Java API, then compared against the stored
     * pattern where wildcards and replacement variables help determine the
     * match. Replacement variables look like <code>${someVar}</code> and are
     * taken as an automatic match. A single asterisk represents a wildcard
     * match where the supplied token matches automatically as well. A double
     * asterisk matches multiple tokens until the next token in the pattern is
     * matched against the URI.
     *
     * @param uri the URI string
     * @return true if the URI matches this pattern
     */
    public boolean matches(String uri);

    /**
     * Normalizes the URI string so that .. and . are properly handled and condensed.
     *
     * @param uriStr the URI string to normalize
     * @return the normalized URI string
     */
    public String normalizeURI(String uriStr);

    /**
     * Indicates whether this {@code URIPattern}'s normalized internal
     * pattern matches that of the supplied object's.
     */
    @Override
    public boolean equals(Object obj);

    @Override
    public int hashCode();

    /**
     * Compares two {@code URIPattern}s for greater specificity. A more
     * specific {@code URIPattern} should always be sorted before a more generic
     * one.
     * @param that the {@code URIPattern} to compare against
     * @return -1 if this {@code URIPattern} is more specific, 0 if they are
     *         equal in specificity and 1 if that {@code URIPattern} is more
     *         specific
     */
    @Override
    public int compareTo(URIPattern that);

    public Map<String, String> getParameterMap(HttpServletRequest request);
}
