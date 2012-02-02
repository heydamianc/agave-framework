package co.cdev.agave.configuration;

import java.io.Serializable;
import java.util.Set;

import co.cdev.agave.URIPattern;

public interface Config extends Iterable<HandlerDescriptor>, Serializable {

    public void addHandlerDescriptor(HandlerDescriptor handlerDescriptor) throws DuplicateDescriptorException;

    public void removeHandlerDescriptor(HandlerDescriptor handlerDescriptor);

    public Set<HandlerDescriptor> getCandidatesFor(String uriPatternString);
    
    public Set<HandlerDescriptor> getCandidatesFor(URIPattern uriPattern);

    public int size();
    
    public boolean isEmpty();
    
}