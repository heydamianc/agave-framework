package co.cdev.agave.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import co.cdev.agave.HttpMethod;
import co.cdev.agave.URIPatternImpl;
import co.cdev.agave.sample.LoginForm;
import co.cdev.agave.sample.SampleHandler;

public class ConfigTest {
    
    private Class<?> handlerClass;
    private Method handlerMethod;
    private Config config;

    @Before
    public void setUp() throws SecurityException, NoSuchMethodException {
        handlerClass = SampleHandler.class;
        handlerMethod = handlerClass.getMethod("login", RoutingContext.class, LoginForm.class);
        config = new ConfigImpl();
    }
    
    @Test(expected = DuplicateDescriptorException.class)
    public void testAddDescriptor() throws Exception {
        config.addHandlerDescriptor(new HandlerDescriptorImpl(
                handlerClass, 
                handlerMethod, 
                new URIPatternImpl("/login"), 
                HttpMethod.POST, 
                false, 
                false, 
                (String) null, 
                (Class<?>) null, 
                new ArrayList<ParamDescriptor>()));

        config.addHandlerDescriptor(new HandlerDescriptorImpl(
                handlerClass, 
                handlerMethod, 
                new URIPatternImpl("/login"), 
                HttpMethod.POST, 
                false,
                false, 
                (String) null, 
                (Class<?>) null, 
                new ArrayList<ParamDescriptor>()));
    }

    public void testAddDescriptor_withUniqueDescriptors() throws Exception {
        config.addHandlerDescriptor(new HandlerDescriptorImpl(
                handlerClass, 
                handlerMethod, 
                new URIPatternImpl("/login"), 
                HttpMethod.GET, 
                false, 
                false, 
                (String) null, 
                (Class<?>) null, 
                new ArrayList<ParamDescriptor>()));

        config.addHandlerDescriptor(new HandlerDescriptorImpl(
                handlerClass, 
                handlerMethod, 
                new URIPatternImpl("/login"), 
                HttpMethod.POST, 
                false,
                false, 
                (String) null, 
                (Class<?>) null, 
                new ArrayList<ParamDescriptor>()));
        
        assertEquals(2, config.size());
    }
    
    @Test
    public void testWriteToOutputStream() throws Exception {
        config.addHandlerDescriptor(new HandlerDescriptorImpl(
                handlerClass, 
                handlerMethod, 
                new URIPatternImpl("/login"), 
                HttpMethod.GET, 
                false, 
                false, 
                (String) null, 
                (Class<?>) null, 
                new ArrayList<ParamDescriptor>()));

        config.addHandlerDescriptor(new HandlerDescriptorImpl(
                handlerClass, 
                handlerMethod, 
                new URIPatternImpl("/login"), 
                HttpMethod.POST, 
                false,
                false, 
                (String) null, 
                (Class<?>) null, 
                new ArrayList<ParamDescriptor>()));
        
        File rootDir = new File(getClass().getResource(".").toURI());
        
        assertNotNull(rootDir);
        assertTrue(rootDir.isDirectory());
        
        File configFile = new File(rootDir, "testWriteToOutputStream.conf");
        FileOutputStream out = new FileOutputStream(configFile);
        
        long initialLength = configFile.length();
        
        config.writeToOutputStream(out);
        
        assertTrue(initialLength < configFile.length());
        
        configFile.delete();
    }
    
    @Test
    public void testWriteToFile() throws Exception {
        config.addHandlerDescriptor(new HandlerDescriptorImpl(
                handlerClass, 
                handlerMethod, 
                new URIPatternImpl("/login"), 
                HttpMethod.GET, 
                false, 
                false, 
                (String) null, 
                (Class<?>) null, 
                new ArrayList<ParamDescriptor>()));

        config.addHandlerDescriptor(new HandlerDescriptorImpl(
                handlerClass, 
                handlerMethod, 
                new URIPatternImpl("/login"), 
                HttpMethod.POST, 
                false,
                false, 
                (String) null, 
                (Class<?>) null, 
                new ArrayList<ParamDescriptor>()));
        
        File rootDir = new File(getClass().getResource(".").toURI());
        
        assertNotNull(rootDir);
        assertTrue(rootDir.isDirectory());
        
        File configFile = new File(rootDir, "testWriteToFile.conf");
        
        long initialLength = configFile.length();
        
        config.writeToFile(configFile);
        
        assertTrue(initialLength < configFile.length());
        
        configFile.delete();
    }
    
    @Test
    public void testReadFromInputStream() throws Exception {
        config.addHandlerDescriptor(new HandlerDescriptorImpl(
                handlerClass, 
                handlerMethod, 
                new URIPatternImpl("/login"), 
                HttpMethod.GET, 
                false, 
                false, 
                (String) null, 
                (Class<?>) null, 
                new ArrayList<ParamDescriptor>()));

        config.addHandlerDescriptor(new HandlerDescriptorImpl(
                handlerClass, 
                handlerMethod, 
                new URIPatternImpl("/login"), 
                HttpMethod.POST, 
                false,
                false, 
                (String) null, 
                (Class<?>) null, 
                new ArrayList<ParamDescriptor>()));
        
        File rootDir = new File(getClass().getResource(".").toURI());
        File configFile = new File(rootDir, "testWriteToOutputStream.conf");
        config.writeToFile(configFile);
        
        Config anotherConfig = new ConfigImpl();
        
        assertEquals(0, anotherConfig.getCandidatesFor("/login").size());
        
        anotherConfig.readFromInputStream(new FileInputStream(configFile));
        
        assertEquals(2, anotherConfig.getCandidatesFor("/login").size());
        
        configFile.delete();
    }
    
    @Test
    public void testReadFromFile() throws Exception {
        config.addHandlerDescriptor(new HandlerDescriptorImpl(
                handlerClass, 
                handlerMethod, 
                new URIPatternImpl("/login"), 
                HttpMethod.GET, 
                false, 
                false, 
                (String) null, 
                (Class<?>) null, 
                new ArrayList<ParamDescriptor>()));

        config.addHandlerDescriptor(new HandlerDescriptorImpl(
                handlerClass, 
                handlerMethod, 
                new URIPatternImpl("/login"), 
                HttpMethod.POST, 
                false,
                false, 
                (String) null, 
                (Class<?>) null, 
                new ArrayList<ParamDescriptor>()));
        
        File rootDir = new File(getClass().getResource(".").toURI());
        File configFile = new File(rootDir, "testReadFromFile.conf");
        config.writeToFile(configFile);
        
        Config anotherConfig = new ConfigImpl();
        
        assertEquals(0, anotherConfig.getCandidatesFor("/login").size());
        
        anotherConfig.readFromFile(configFile);
        
        assertEquals(2, anotherConfig.getCandidatesFor("/login").size());
        
        configFile.delete();
    }
    
}
