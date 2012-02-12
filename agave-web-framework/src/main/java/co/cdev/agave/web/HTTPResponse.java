package co.cdev.agave.web;

public class HTTPResponse {
    
    private StatusCode statusCode;
    private String contentType;
    private Object content;
    
    public HTTPResponse() {
        this(null, null, null);
    }
    
    public HTTPResponse(StatusCode statusCode) {
        this(statusCode, null);
    }
    
    public HTTPResponse(StatusCode statusCode, Object messageBody) {
        this(statusCode, "text/plain", messageBody);
    }
    
    public HTTPResponse(StatusCode statusCode, String contentType, Object content) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.content = content;
    }
    
    public StatusCode getStatusCode() {
        return statusCode;
    }
    
    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public Object getContent() {
        return content;
    }
    
    public void setContent(Object content) {
        this.content = content;
    }
    
}