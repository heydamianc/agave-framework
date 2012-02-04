package co.cdev.agave.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    
    public void writeToFile(File configFile) throws IOException;
    
    /**
     * Wraps the supplied output stream with a buffered output stream and then writes the config 
     * to it.
     * 
     * @param out
     * @throws IOException
     */
    public void writeToOutputStream(OutputStream out) throws IOException;
    
    public void readFromFile(File configFile) throws IOException, ClassNotFoundException;
    
    /**
     * Wraps the supplied input stream with a buffered output stream and then reads the config 
     * to it.
     * 
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void readFromInputStream(InputStream in) throws IOException, ClassNotFoundException;
    
}