package co.cdev.agave.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationGeneratorTest {

    private File rootDir;
    private ConfigGenerator configGenerator;
    
    @Before
    public void setUp() throws Exception {
        URL rootUrl = getClass().getClassLoader().getResource(".");
        Assert.assertNotNull(rootUrl);
        rootDir = new File(rootUrl.toURI());
        configGenerator = new ConfigGeneratorImpl();
    }
    
    @Test
    public void testRootDir() {
        assertNotNull(rootDir);
    }
    
    @Test
    public void testScanForHandlers() throws Exception {
        Config config = configGenerator.scanClassesWithinRootDirectory(rootDir);
        
        assertFalse(config.getCandidatesFor("/login").isEmpty());
        assertFalse(config.getCandidatesFor("/aliased").isEmpty());
        assertFalse(config.getCandidatesFor("/uri-params/${username}/${password}/").isEmpty());
        assertFalse(config.getCandidatesFor("/throws/nullPointerException").isEmpty());
        assertFalse(config.getCandidatesFor("/throws/ioException").isEmpty());
        assertFalse(config.getCandidatesFor("/lacks/form").isEmpty());
        assertFalse(config.getCandidatesFor("/has/named/params/${something}/${aNumber}").isEmpty());
        assertFalse(config.getCandidatesFor("/overloaded").isEmpty());
        assertFalse(config.getCandidatesFor("/overloaded/${param}").isEmpty());
        
        assertEquals(9, config.size());
    }
    
}
