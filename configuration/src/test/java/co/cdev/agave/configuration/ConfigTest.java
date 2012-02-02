package co.cdev.agave.configuration;

import static org.junit.Assert.assertEquals;

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
}
