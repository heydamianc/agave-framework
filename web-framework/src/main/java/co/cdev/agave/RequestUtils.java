package co.cdev.agave;

import javax.servlet.http.HttpServletRequest;

public final class RequestUtils {
    
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    
    private RequestUtils() {}

    public static boolean isMultipart(RoutingContext context) {
        return context != null && isMultipart(context.getRequest());
    }
    
    public static boolean isMultipart(HttpServletRequest request) {
        return request.getContentType() != null && request.getContentType().startsWith(MULTIPART_FORM_DATA);
    }
    
    public static boolean isFormURLEncoded(RoutingContext context) {
        return context != null && isFormURLEncoded(context.getRequest());
    }

    public static boolean isFormURLEncoded(HttpServletRequest request) {
        return APPLICATION_X_WWW_FORM_URLENCODED.equals(request.getContentType());
    }
    
}
