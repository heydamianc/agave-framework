package co.cdev.agave.web;

public class HTTPResponse {
    
    private StatusCode statusCode;
    private String contentType;
    private Object messageBody;
    
    public HTTPResponse() {
        this(null, null, null);
    }
    
    public HTTPResponse(StatusCode statusCode, Object messageBody) {
        this(statusCode, "text/plain", messageBody);
    }
    
    public HTTPResponse(StatusCode statusCode, String contentType, Object messageBody) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.messageBody = messageBody;
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
    
    public Object getMessageBody() {
        return messageBody;
    }
    
    public void setMessageBody(Object messageBody) {
        this.messageBody = messageBody;
    }
    
}