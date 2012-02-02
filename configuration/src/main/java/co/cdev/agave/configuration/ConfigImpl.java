package co.cdev.agave.configuration;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import co.cdev.agave.URIPattern;
import co.cdev.agave.URIPatternImpl;

public class ConfigImpl implements Config {

    private static final long serialVersionUID = 1L;

    private final SortedSet<HandlerDescriptor> handlerDescriptors;
    
    public ConfigImpl() {
        handlerDescriptors = new TreeSet<HandlerDescriptor>();
    }
    
    @Override
    public void addHandlerDescriptor(HandlerDescriptor handlerDescriptor) throws DuplicateDescriptorException {
        for (HandlerDescriptor existingHandlerDescriptor : handlerDescriptors) {
            if (existingHandlerDescriptor.equals(handlerDescriptor)) {
                throw new DuplicateDescriptorException(existingHandlerDescriptor, handlerDescriptor);
            }
        }
        handlerDescriptors.add(handlerDescriptor);
    }
    
    @Override
    public void removeHandlerDescriptor(HandlerDescriptor handlerDescriptor) {
        handlerDescriptors.remove(handlerDescriptor);
    }

    @Override
    public Iterator<HandlerDescriptor> iterator() {
        return handlerDescriptors.iterator();
    }

    @Override
    public Set<HandlerDescriptor> getCandidatesFor(String uriPatternString) {
        return getCandidatesFor(new URIPatternImpl(uriPatternString));
    }
    
    @Override
    public Set<HandlerDescriptor> getCandidatesFor(URIPattern uriPattern) {
        Set<HandlerDescriptor> candidates = new HashSet<HandlerDescriptor>();
        
        if (uriPattern != null) {
            for (HandlerDescriptor handlerDescriptor : handlerDescriptors) {
                if (handlerDescriptor.getURIPattern().equals(uriPattern)) {
                    candidates.add(handlerDescriptor);
                }
            }
        }
        
        return candidates;
    }

    @Override
    public int size() {
        return handlerDescriptors.size();
    }
    
    @Override
    public boolean isEmpty() {
        return handlerDescriptors.isEmpty();
    }
 
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("ConfigImpl [descriptors=");
        
        Iterator<HandlerDescriptor> itr = handlerDescriptors.iterator();
        
         while (itr.hasNext()) {
             s.append(itr.next().toString());
             
             if (itr.hasNext()) {
                 s.append(",");
             }
         }
        
        s.append("]");
        
        return s.toString();
    }
    
}
