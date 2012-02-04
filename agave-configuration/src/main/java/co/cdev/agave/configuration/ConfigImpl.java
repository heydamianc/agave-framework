package co.cdev.agave.configuration;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
    public void writeToFile(File configFile) throws IOException {
        if (configFile == null) {
            throw new NullPointerException("Config file can not be null");
        }
        writeToOutputStream(new FileOutputStream(configFile));
    }

    @Override
    public void writeToOutputStream(OutputStream out) throws IOException {
        if (out == null) {
            throw new NullPointerException("Output stream can not be null");
        }
        
        ObjectOutputStream objectOut = new ObjectOutputStream(new BufferedOutputStream(out));
        objectOut.writeObject(handlerDescriptors);
        objectOut.flush();
        objectOut.close();
    }
    
    public void readFromFile(File configFile) throws IOException, ClassNotFoundException {
        readFromInputStream(new FileInputStream(configFile));
    }
    
    public void readFromInputStream(InputStream in) throws IOException, ClassNotFoundException {
        ObjectInputStream objectIn = new ObjectInputStream(new BufferedInputStream(in));
        
        @SuppressWarnings("unchecked")
        Set<HandlerDescriptor> handlerDescriptors = (Set<HandlerDescriptor>) objectIn.readObject();
        
        this.handlerDescriptors.clear();
        this.handlerDescriptors.addAll(handlerDescriptors);
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
