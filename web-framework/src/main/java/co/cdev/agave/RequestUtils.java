package co.cdev.agave;

import javax.servlet.http.HttpServletRequest;

public final class RequestUtils {
    
    private RequestUtils() {}

    public static boolean isMultipart(RoutingContext context) {
        return context != null && isMultipart(context.getRequest());
    }
    
    public static boolean isMultipart(HttpServletRequest request) {
        return request.getContentType() != null && request.getContentType().startsWith("multipart/form-data");
    }
    
    public static boolean isFormURLEncoded(RoutingContext context) {
        return context != null && isFormURLEncoded(context.getRequest());
    }

    public static boolean isFormURLEncoded(HttpServletRequest request) {
        return "application/x-www-form-urlencoded".equals(request.getContentType());
    }
    
}
