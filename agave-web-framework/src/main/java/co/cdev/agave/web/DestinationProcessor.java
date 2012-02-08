package co.cdev.agave.web;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import co.cdev.agave.HttpMethod;
import co.cdev.agave.configuration.HandlerDescriptor;
import co.cdev.agave.configuration.RoutingContext;

public class DestinationProcessor implements ResultProcessor {

    public boolean canProcessResult(Object result, 
                                      RoutingContext routingContext, 
                                      HandlerDescriptor handlerDescriptor) {
        return result != null && result.getClass().isAssignableFrom(Destination.class);
    }
    
    @Override
    public void process(Object result, 
                          RoutingContext routingContext, 
                          HandlerDescriptor handlerDescriptor)
                                  throws ServletException {
        URI uri = null;
        boolean redirect = false;
        Destination destination = (Destination) result;

        try {
            uri = new URI(null, destination.encode(routingContext.getServletContext()), null);
        } catch (URISyntaxException ex) {
            throw new DestinationException(destination, handlerDescriptor, ex);
        }
        
        if (destination.getRedirect() == null) {
            if (HttpMethod.POST.name().equalsIgnoreCase(routingContext.getRequest().getMethod())) {
                redirect = true;
            }
        } else {
            redirect = destination.getRedirect();
        }
        
        if (redirect) {
            String location = uri.toASCIIString();
            
            if (location.startsWith("/")) { // absolute URI
                location = routingContext.getRequest().getContextPath() + location;
            }
            
            try {
                routingContext.getResponse().sendRedirect(location);
            } catch (IOException e) {
                throw new DestinationException(destination, handlerDescriptor);
            }
        } else {
            try {
                RequestDispatcher dispatcher = routingContext.getRequest().getRequestDispatcher(uri.toASCIIString());
                dispatcher.forward(routingContext.getRequest(), routingContext.getResponse());
            } catch (IOException e) {
                throw new AgaveWebException(e);
            }
        }
    }

}
