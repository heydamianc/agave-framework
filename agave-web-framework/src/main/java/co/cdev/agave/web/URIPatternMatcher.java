package co.cdev.agave.web;

import javax.servlet.http.HttpServletRequest;

public interface URIPatternMatcher {
 
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
    
}
