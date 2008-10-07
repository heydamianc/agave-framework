package agave;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractHandler {

    protected HttpServletRequest request;
    protected HttpServletResponse response;

    @BindsRequest
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @BindsResponse
    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
    
}