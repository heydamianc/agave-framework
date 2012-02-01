package co.cdev.agave.configuration;

import java.io.Serializable;
import java.util.Collection;

public class Configuration implements Serializable {

    private static final long serialVersionUID = 1L;

    private Collection<HandlerDescriptor> handlerDescriptors;

    public Collection<HandlerDescriptor> getHandlerDescriptors() {
        return handlerDescriptors;
    }

    public void setHandlerDescriptors(Collection<HandlerDescriptor> handlerDescriptors) {
        this.handlerDescriptors = handlerDescriptors;
    }
    
}
