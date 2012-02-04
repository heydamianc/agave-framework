package co.cdev.agave.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.junit.Before;
import org.junit.Test;

import co.cdev.agave.HttpMethod;
import co.cdev.agave.sample.SampleHandler;

public class ConfigTest {
    
    private Config config;
    
    @Before
    public void setUp() {
        config = new ConfigImpl();
    }
    
    @Test(expected = DuplicateDescriptorException.class)
    public void testAddDescriptor() throws Exception {
        config.addHandlerDescriptor(new HandlerDescriptorImpl(new ScanResultImpl("/pattern", SampleHandler.class.getName(), "login")));
        config.addHandlerDescriptor(new HandlerDescriptorImpl(new ScanResultImpl("/pattern", SampleHandler.class.getName(), "login")));
    }

    @Test
    public void testAddDescriptor_withUniqueDescriptors() throws Exception {
        config.addHandlerDescriptor(new HandlerDescriptorImpl(new ScanResultImpl("/pattern", HttpMethod.GET, SampleHandler.class.getName(), "login")));
        config.addHandlerDescriptor(new HandlerDescriptorImpl(new ScanResultImpl("/pattern", HttpMethod.POST, SampleHandler.class.getName(), "login")));

        assertEquals(2, config.size());
    }
    
    @Test
    public void testWriteToOutputStream() throws Exception {
        config.addHandlerDescriptor(new HandlerDescriptorImpl(new ScanResultImpl("/pattern", HttpMethod.GET, SampleHandler.class.getName(), "login")));
        config.addHandlerDescriptor(new HandlerDescriptorImpl(new ScanResultImpl("/pattern", HttpMethod.POST, SampleHandler.class.getName(), "login")));
        
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
        config.addHandlerDescriptor(new HandlerDescriptorImpl(new ScanResultImpl("/pattern", HttpMethod.GET, SampleHandler.class.getName(), "login")));
        config.addHandlerDescriptor(new HandlerDescriptorImpl(new ScanResultImpl("/pattern", HttpMethod.POST, SampleHandler.class.getName(), "login")));
        
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
        config.addHandlerDescriptor(new HandlerDescriptorImpl(new ScanResultImpl("/pattern", HttpMethod.GET, SampleHandler.class.getName(), "login")));
        config.addHandlerDescriptor(new HandlerDescriptorImpl(new ScanResultImpl("/pattern", HttpMethod.POST, SampleHandler.class.getName(), "login")));
        
        File rootDir = new File(getClass().getResource(".").toURI());
        File configFile = new File(rootDir, "testWriteToOutputStream.conf");
        config.writeToFile(configFile);
        
        Config anotherConfig = new ConfigImpl();
        
        assertTrue(anotherConfig.getCandidatesFor("/pattern").isEmpty());
        
        anotherConfig.readFromInputStream(new FileInputStream(configFile));
        
        assertFalse(anotherConfig.getCandidatesFor("/pattern").isEmpty());
        
        configFile.delete();
    }
    
    @Test
    public void testReadFromFile() throws Exception {
        config.addHandlerDescriptor(new HandlerDescriptorImpl(new ScanResultImpl("/pattern", HttpMethod.GET, SampleHandler.class.getName(), "login")));
        config.addHandlerDescriptor(new HandlerDescriptorImpl(new ScanResultImpl("/pattern", HttpMethod.POST, SampleHandler.class.getName(), "login")));
        
        File rootDir = new File(getClass().getResource(".").toURI());
        File configFile = new File(rootDir, "testReadFromFile.conf");
        config.writeToFile(configFile);
        
        Config anotherConfig = new ConfigImpl();
        
        assertTrue(anotherConfig.getCandidatesFor("/pattern").isEmpty());
        
        anotherConfig.readFromFile(configFile);
        
        assertFalse(anotherConfig.getCandidatesFor("/pattern").isEmpty());
        
        configFile.delete();
    }
    
}
