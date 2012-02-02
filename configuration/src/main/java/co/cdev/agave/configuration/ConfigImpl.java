package co.cdev.agave.configuration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import co.cdev.agave.URIPattern;
import co.cdev.agave.URIPatternImpl;

public class ConfigImpl implements Config {

    private static final long serialVersionUID = 1L;

    private final Map<URIPattern, HandlerDescriptor> handlerDescriptors;
    
    public ConfigImpl() {
        handlerDescriptors = new HashMap<URIPattern, HandlerDescriptor>();
    }
    
    @Override
    public void addHandlerDescriptor(HandlerDescriptor handlerDescriptor) {
        handlerDescriptors.put(handlerDescriptor.getURIPattern(), handlerDescriptor);
    }
    
    @Override
    public void removeHandlerDescriptor(HandlerDescriptor handlerDescriptor) {
        handlerDescriptors.remove(handlerDescriptor);
    }

    @Override
    public Iterator<HandlerDescriptor> iterator() {
        return handlerDescriptors.values().iterator();
    }

    @Override
    public HandlerDescriptor get(String uriPatternString) {
        return handlerDescriptors.get(new URIPatternImpl(uriPatternString));
    }
    
    @Override
    public HandlerDescriptor get(URIPattern uriPattern) {
        return handlerDescriptors.get(uriPattern);
    }

    @Override
    public int size() {
        return handlerDescriptors.size();
    }
    
}
