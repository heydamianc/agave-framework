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

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;

/**
 * A {@code URIPattern} is the object that indicates which handler should be
 * invoked according to the requested URI. A {@code URIPattern} is similar in
 * nature to the string part of the URI except for having wildcards and
 * replacement variables.
 * 
 * Replacement variables look like {@code ${var}} and are supplied to handler
 * methods as arguments to the method if annotated. From this point of view,
 * though, consider replacement variables as a single wildcard match.
 * 
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class URIPatternImpl implements URIPattern {

    private static final long serialVersionUID = 1L;
    
    private String pattern;
    private String[] parts;
    
    protected URIPatternImpl() {
        
    }

    public URIPatternImpl(String pattern) {
        if (!pattern.startsWith("/")) {
            throw new IllegalArgumentException(
                "The supplied pattern must begin with a forward slash (\"/\") "
                    + "where the root is relative to the context path.");
        }
        if (pattern.contains("**/${")) {
            throw new IllegalArgumentException(
                "The supplied pattern is nondeterministic.  There is no way of "
                    + "knowing when to stop matching with this type of pattern: /**/${var}/");
        }
        
        this.pattern = normalizePattern(pattern);
        
        if (pattern.length() > 1) {
            this.parts = pattern.substring(1).split("/");
        }
    }
    
    @Override
    public String[] getParts() {
        return parts;
    }

    protected String normalizePattern(String pattern) {
        URI uri;
        try {
            pattern = pattern.replace("${", "~~agave~~start~~delim~~");
            pattern = pattern.replace("}", "~~agave~~end~~delim~~");
            uri = new URI(pattern);
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Malformed URI pattern: " + pattern, ex);
        }
        String normalizedUri = stripTrailingSlash(uri.normalize().toString());
        normalizedUri = normalizedUri.replace("~~agave~~start~~delim~~", "${");
        normalizedUri = normalizedUri.replace("~~agave~~end~~delim~~", "}");
        normalizedUri = condenseWildcards(normalizedUri);
        return normalizedUri;
    }

    /**
     * Condenses multiple successive wildcards into the most generic wildcard
     */
    private String condenseWildcards(String pattern) {
        while (pattern.contains("**/**") || pattern.contains("**/*")
            || pattern.contains("*/**")) {
            pattern = pattern.replace("**/**", "**");
            pattern = pattern.replace("**/*", "**");
            pattern = pattern.replace("*/**", "**");
        }
        return pattern;
    }

    /**
     * Normalizes the URI string so that .. and . are properly handled and
     * condensed.
     * 
     * @param uriStr
     *            the URI string to normalize
     * @return the normalized URI string
     */
    @Override
    public String normalizeURI(String uriStr) {
        URI uri;
        try {
            uri = new URI(uriStr);
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Malformed URI: " + uriStr, ex);
        }
        return stripTrailingSlash(uri.normalize().toString());
    }

    private String stripTrailingSlash(String input) {
        if (!input.equals(FORWARD_SLASH) && input.endsWith(FORWARD_SLASH)) {
            return input.substring(0, input.length() - 1);
        }
        return input;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof URIPattern))
            return false;
        
        URIPattern that = (URIPattern) obj;
        return pattern.equalsIgnoreCase(that.toString());
    }

    @Override
    public int hashCode() {
        return pattern.hashCode();
    }

    /**
     * Compares two {@code URIPattern}s for greater specificity. A more
     * specific {@code URIPattern} should always be sorted before a more generic
     * one.
     * 
     * @param that
     *            the {@code URIPattern} to compare against
     * @return -1 if this {@code URIPattern} is more specific, 0 if they are
     *         equal in specificity and 1 if that {@code URIPattern} is more
     *         specific
     */
    @Override
    public int compareTo(URIPattern that) {
        if (this.equals(that)) {
            return 0;
        }

        Integer value = null;

        String[] thisTokens = pattern.split(FORWARD_SLASH);
        String[] thatTokens = that.toString().split(FORWARD_SLASH);

        for (int i = 0; i < thisTokens.length && i < thatTokens.length; i++) {
            if ("**".equals(thisTokens[i]) && !"**".equals(thatTokens[i])) {
                value = 1;
                break;
            } else if (!"**".equals(thisTokens[i]) && "**".equals(thatTokens[i])) {
                value = -1;
                break;
            } else if ("*".equals(thisTokens[i]) && !"*".equals(thatTokens[i])) {
                value = 1;
                break;
            } else if (!"*".equals(thisTokens[i]) && "*".equals(thatTokens[i])) {
                value = -1;
                break;
            }
        }
        
        if (value == null) {
            if (thisTokens.length > thatTokens.length) {
                value = -1;
            } else if (thisTokens.length < thatTokens.length) {
                value = 1;
            } else {
                int length = (thisTokens.length > thatTokens.length) ? thisTokens.length : thatTokens.length;
                for (int i = 0; i < length; i++) {
                    Matcher thisTokenMatcher = REPLACEMENT_PATTERN.matcher(thisTokens[i]);
                    Matcher thatTokenMatcher = REPLACEMENT_PATTERN.matcher(thatTokens[i]);
                    
                    if (thisTokenMatcher.matches() && thatTokenMatcher.matches()) {
                        value = 0;
                        continue; // just ignore this case - treat all replacement params equal
                    } else if (!thisTokenMatcher.matches() && thatTokenMatcher.matches()) {
                        value = -1;
                        break;
                    } else if (thisTokenMatcher.matches() && !thatTokenMatcher.matches()) {
                        value = 1;
                        break;
                    }
                    
                    value = thisTokens[i].compareToIgnoreCase(thatTokens[i]);
                    if (value != 0) {
                        break;
                    }
                }
            }
        }

        return value;
    }

    @Override
    public String toString() {
    	return pattern;
    }
    
    // Serialization
    
    private Object writeReplace() {
        return new SerializationProxy(this);
    }
    
    private void readObject(ObjectInputStream in) throws InvalidObjectException {
        throw new InvalidObjectException("Expected SerializationProxy");
    }
    
    private static class SerializationProxy implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        private final String pattern;
        
        SerializationProxy(URIPatternImpl uriPattern) {
            this.pattern = uriPattern.pattern;
        }
        
        private Object readResolve() {
            return new URIPatternImpl(pattern);
        }
    }
    
}
