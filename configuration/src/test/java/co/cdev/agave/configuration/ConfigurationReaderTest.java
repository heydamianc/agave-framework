package co.cdev.agave.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationReaderTest {

    private File rootDir;
    private ConfigReader configReader;
    
    @Before
    public void setUp() throws Exception {
        URL rootUrl = getClass().getClassLoader().getResource(".");
        Assert.assertNotNull(rootUrl);
        rootDir = new File(rootUrl.toURI());
        configReader = new ConfigReaderImpl();
    }
    
    @Test
    public void testRootDir() {
        assertNotNull(rootDir);
    }
    
    @Test
    public void testScanForHandlers() throws Exception {
        Config config = configReader.scanForHandlers(rootDir);
        
        assertNotNull(config.get("/login"));
        assertNotNull(config.get("/aliased"));
        assertNotNull(config.get("/uri-params/${username}/${password}/"));
        assertNotNull(config.get("/throws/nullPointerException"));
        assertNotNull(config.get("/throws/ioException"));
        assertNotNull(config.get("/lacks/form"));
        assertNotNull(config.get("/has/named/params/${something}/${aNumber}"));
        assertNotNull(config.get("/overloaded"));
        assertNotNull(config.get("/overloaded/${param}"));
        assertEquals(9, config.size());
    }
    
}
