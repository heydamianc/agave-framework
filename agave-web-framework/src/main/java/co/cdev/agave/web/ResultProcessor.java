package co.cdev.agave.web;

import javax.servlet.ServletException;

import co.cdev.agave.configuration.HandlerDescriptor;
import co.cdev.agave.configuration.RoutingContext;

public interface ResultProcessor {
    
    public boolean canProcessResult(Object result, 
                                      RoutingContext routingContext, 
                                      HandlerDescriptor handlerDescriptor);
    
    public void process(Object result, 
                          RoutingContext routingContext, 
                          HandlerDescriptor handlerDescriptor)
                                  throws ServletException;
    
}
