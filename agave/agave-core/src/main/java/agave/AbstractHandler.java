package agave;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractHandler {
    
    protected ServletContext servletContext;
    protected HttpServletRequest request;
    protected HttpServletResponse response;

    @BindsServletContext
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @BindsRequest
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @BindsResponse
    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
    
}