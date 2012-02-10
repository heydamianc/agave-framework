package co.cdev.agave.sample;

import java.io.IOException;

import javax.servlet.ServletException;

import co.cdev.agave.configuration.HandlerDescriptor;
import co.cdev.agave.configuration.RoutingContext;
import co.cdev.agave.web.HTTPResponseProcessor;

public class StringResponseProcessor extends HTTPResponseProcessor {

    @Override
    public boolean canProcessResult(Object result, RoutingContext routingContext, HandlerDescriptor handlerDescriptor) {
        return result != null && StringResponse.class.isAssignableFrom(result.getClass());
    }

    @Override
    public void process(Object result, RoutingContext routingContext, HandlerDescriptor handlerDescriptor)
            throws ServletException {
        try {
            routingContext.getResponse().getWriter().write("Text!");
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }

}
