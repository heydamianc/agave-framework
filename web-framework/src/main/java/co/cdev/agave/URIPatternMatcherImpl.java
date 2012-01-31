package co.cdev.agave;

import static co.cdev.agave.URIPattern.FORWARD_SLASH;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

public class URIPatternMatcherImpl implements URIPatternMatcher {
    
    private final URIPattern uriPattern;
    
    public URIPatternMatcherImpl(URIPattern uriPattern) {
        this.uriPattern = uriPattern;
    }
    
    @Override
    public boolean matches(HttpServletRequest request) {
        return request != null && matches(request.getServletPath());
    }

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
     * @param uri the uri string
     * @return true if the uri matches this pattern
     */
    @Override
    public boolean matches(String uri) {
        if (!uri.startsWith(FORWARD_SLASH)) {
            throw new IllegalArgumentException("URI must begin with a forward slash ('/')");
        }

        // check for root (so as to not match all patterns starting with a '/')
        if ("/".equals(uri)) {
            if ("/".equals(uriPattern.getPattern())) {
                return true;
            }
            return false;
        }
        
        String[] patternTokens = uriPattern.getPattern().split(FORWARD_SLASH);
        String[] uriTokens = uriPattern.normalizeURI(uri).split(FORWARD_SLASH);
        
        int pi = 0, ui = 0;
        for (; pi < patternTokens.length && ui < uriTokens.length; pi++, ui++) {
            if ("**".equals(patternTokens[pi])) {
                ++pi;
                if (pi >= patternTokens.length) {
                    // matches the rest of the uri
                    return true;
                } else {
                    // slurp the uri tokens until they match the next position
                    // in the pattern
                    while (ui < uriTokens.length) {
                        if (uriTokens[ui].equalsIgnoreCase(patternTokens[pi])) {
                            break;
                        }
                        ++ui;
                    }
                    if (ui >= uriTokens.length) {
                        return false;
                    }
                }
            } else if (!uriTokens[ui].equalsIgnoreCase(patternTokens[pi])
                && !"*".equals(patternTokens[pi])
                && !Pattern.compile("\\$\\{.*\\}").matcher(patternTokens[pi]).matches()) {
                return false;
            }
        }

        if ((pi >= patternTokens.length && ui < uriTokens.length)
            || (ui < uriTokens.length && !"**".equals(patternTokens[pi]))
            || (pi < patternTokens.length && !"**".equals(patternTokens[pi]))) {
            return false;
        }

        return true;
    }

}
