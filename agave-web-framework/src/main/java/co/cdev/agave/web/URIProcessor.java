package co.cdev.agave.web;

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;

import co.cdev.agave.configuration.HandlerDescriptor;
import co.cdev.agave.configuration.RoutingContext;

public class URIProcessor implements ResultProcessor {

    @Override
    public boolean canProcessResult(Object result, 
                                    RoutingContext routingContext,
                                    HandlerDescriptor handlerDescriptor) {
        return result != null && URI.class.isAssignableFrom(result.getClass());
    }

    @Override
    public void process(Object result,
                        RoutingContext routingContext,
                        HandlerDescriptor handlerDescriptor)
                                throws ServletException {
        String location = ((URI) result).toASCIIString();
        
        if (location.startsWith("/")) { // absolute URI
            location = routingContext.getRequest().getContextPath() + location;
        }
        
        try {
            routingContext.getResponse().sendRedirect(location);
        } catch (IOException e) {
            throw new AgaveWebException(e);
        }
    }

}
