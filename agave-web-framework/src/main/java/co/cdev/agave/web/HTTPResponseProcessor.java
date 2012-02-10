package co.cdev.agave.web;

import java.io.IOException;

import javax.servlet.ServletException;

import co.cdev.agave.configuration.HandlerDescriptor;
import co.cdev.agave.configuration.RoutingContext;

public class HTTPResponseProcessor implements ResultProcessor {

    @Override
    public boolean canProcessResult(Object result, RoutingContext routingContext, HandlerDescriptor handlerDescriptor) {
        return result != null && HTTPResponse.class.isAssignableFrom(result.getClass());
    }

    @Override
    public void process(Object result, RoutingContext routingContext, HandlerDescriptor handlerDescriptor)
            throws ServletException {
        HTTPResponse response = (HTTPResponse) result;
        
        processStatusCode(response, routingContext, handlerDescriptor);
        processContentType(response, routingContext, handlerDescriptor);
        processMessageBody(response, routingContext, handlerDescriptor);
    }
    
    protected void processContentType(HTTPResponse response, RoutingContext routingContext, HandlerDescriptor handlerDescriptor) {
        routingContext.getResponse().setContentType(response.getContentType());
    }

    protected void processStatusCode(HTTPResponse response, RoutingContext routingContext, HandlerDescriptor handlerDescriptor) {
        routingContext.getResponse().setStatus(response.getStatusCode().getNumericCode());
    }
    
    protected void processMessageBody(HTTPResponse response, RoutingContext routingContext, HandlerDescriptor handlerDescriptor) 
            throws ServletException {
        try {
            routingContext.getResponse().getWriter().write(response.getMessageBody().toString());
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }

}
