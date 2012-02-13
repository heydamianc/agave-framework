package co.cdev.agave.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import co.cdev.agave.conversion.DateConverter;
import co.cdev.agave.conversion.IntegerConverter;
import co.cdev.agave.conversion.LongConverter;
import co.cdev.agave.conversion.NoopConverter;

public class ConfigurationGeneratorTest {

    private File rootDir;
    private ConfigGenerator configGenerator;
    
    @Before
    public void setUp() throws Exception {
        URL rootUrl = getClass().getClassLoader().getResource(".");
        Assert.assertNotNull(rootUrl);
        rootDir = new File(rootUrl.toURI());
        configGenerator = new ConfigGeneratorImpl(rootDir);
    }
    
    @Test
    public void testRootDir() {
        assertNotNull(rootDir);
    }
    
    @Test
    public void testGenerateConfig() throws Exception {
        Config config = configGenerator.generateConfig();
        
        assertFalse(config.getCandidatesFor("/login").isEmpty());
        assertFalse(config.getCandidatesFor("/aliased").isEmpty());
        assertFalse(config.getCandidatesFor("/uri-params/${username}/${password}/").isEmpty());
        assertFalse(config.getCandidatesFor("/throws/nullPointerException").isEmpty());
        assertFalse(config.getCandidatesFor("/throws/ioException").isEmpty());
        assertFalse(config.getCandidatesFor("/lacks/form").isEmpty());
        assertFalse(config.getCandidatesFor("/has/named/params/${something}/${aNumber}").isEmpty());
        assertFalse(config.getCandidatesFor("/overloaded").isEmpty());
        assertFalse(config.getCandidatesFor("/overloaded/${param}").isEmpty());
        
        assertEquals(11, config.size());
    }
    
    @Test
    public void testGenerateConfig_expectNamedParametersWithConvertersToBeNamed() throws Exception {
        Config config = configGenerator.generateConfig();
        
        for (HandlerDescriptor handlerDescriptor : config.getCandidatesFor("/has/named/params/${something}/${aNumber}")) {
            ParamDescriptor paramDescriptor = handlerDescriptor.getParamDescriptors().get(0);
            assertEquals(String.class, paramDescriptor.getParamClass());
            assertEquals("something", paramDescriptor.getName());
            assertEquals(NoopConverter.class, paramDescriptor.getConverterClass());
            
            paramDescriptor = handlerDescriptor.getParamDescriptors().get(1);
            assertEquals(int.class, paramDescriptor.getParamClass());
            assertEquals("aNumber", paramDescriptor.getName());
            assertEquals(IntegerConverter.class, paramDescriptor.getConverterClass());
        }
    }
    
    @Test
    public void testGenerateConfig_expectImplicitConvertersToBeNamed() throws Exception {
        Config config = configGenerator.generateConfig();
        
        for (HandlerDescriptor handlerDescriptor : config.getCandidatesFor("/implicit/conversion/${id}")) {
            ParamDescriptor paramDescriptor = handlerDescriptor.getParamDescriptors().get(0);
            assertEquals(LongConverter.class, paramDescriptor.getConverterClass());
        }
    }
    
    @Test
    public void testGenerateConfig_expectImplicitDateConvertersToBeNamed() throws Exception {
        Config config = configGenerator.generateConfig();
        
        for (HandlerDescriptor handlerDescriptor : config.getCandidatesFor("/implicit/date/conversion/${date}")) {
            ParamDescriptor paramDescriptor = handlerDescriptor.getParamDescriptors().get(0);
            assertEquals(DateConverter.class, paramDescriptor.getConverterClass());
        }
    }
    
}
