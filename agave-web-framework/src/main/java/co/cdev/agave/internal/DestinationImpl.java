package co.cdev.agave.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import co.cdev.agave.Destination;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class DestinationImpl implements Destination {
    
    public static final String ESCAPED_AMPERSAND = "&amp;";

    private String path;
    private Map<String, List<String>> params = new LinkedHashMap<String, List<String>>();
    private Boolean redirect;
    
    /**
     * Create a new {@code Destination} within the deployed context.  The destination will be 
     * redirected to if the HTTP request method was a POST, otherwise it will be forwarded to.
     * 
     * @param path
     */
    public DestinationImpl(String path) {
        setPath(path);
    }
    
    public DestinationImpl(String path, boolean redirect) {
        setPath(path);
        setRedirect(Boolean.valueOf(redirect));
    }
    
    @Override
    public void addParameter(String name, String value) {
        if (!params.containsKey(name)) {
            params.put(name, new ArrayList<String>());
        }
        params.get(name).add(value);
    }
    
    @Override
    public String getPath() {
        return path;
    }

    @Override
    public final void setPath(String path) {
        if (!path.contains("://") && !path.startsWith("/")) {
            throw new IllegalArgumentException("Relative destination paths should start with a forward slash '/'; "
                    + "got '" + path + "' instead");
        }
        this.path = path;
    }

    @Override
    public Map<String, List<String>> getParams() {
        return params;
    }

    @Override
    public void setParams(Map<String, List<String>> params) {
        this.params = params;
    }

    @Override
    public Boolean getRedirect() {
        return redirect;
    }
    
    @Override
    public final void setRedirect(Boolean redirect) {
        this.redirect = redirect;
    }
    
    @Override
    public String encode(ServletContext context) {
        StringBuilder encodedPath = new StringBuilder();
        
        if (getPath() != null) {
            encodedPath.append(getPath());
        
            if (!getParams().isEmpty()) {
                encodedPath.append("?");
                
                Iterator<String> paramNameItr = params.keySet().iterator();
                while (paramNameItr.hasNext()) {
                    String paramName = paramNameItr.next();
                    
                    Iterator<String> paramValueItr = params.get(paramName).iterator();
                    while (paramValueItr.hasNext()) {
                        
                        // Other values may need to be escaped here as well
                        encodedPath
                            .append(paramName)
                            .append("=")
                            .append(paramValueItr.next()
                                    .replace("&", ESCAPED_AMPERSAND));
                        
                        if (paramValueItr.hasNext()) {
                            encodedPath.append("&");
                        }
                    }
                    
                    if (paramNameItr.hasNext()) {
                        encodedPath.append("&");
                    }
                }
            }
        }
        
        return encodedPath.toString();
    }

    @Override
    public String toString() {
        return String.format("Destination[path:%s, redirect: %b]", this.path, this.redirect);
    }
    
}
