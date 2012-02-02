package co.cdev.agave.configuration;

import java.io.Serializable;

import co.cdev.agave.URIPattern;

public interface Config extends Iterable<HandlerDescriptor>, Serializable {

    public void addHandlerDescriptor(HandlerDescriptor handlerDescriptor);

    public void removeHandlerDescriptor(HandlerDescriptor handlerDescriptor);

    public HandlerDescriptor get(String uriPatternString);
    
    public HandlerDescriptor get(URIPattern uriPattern);

    public int size();
    
}