package co.cdev.agave;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class ConfigMojoTest extends AbstractMojoTestCase {

    private ConfigMojo configMojo;
    
    protected void setUp() throws Exception {
        super.setUp(); // required for mojo lookups to work
        
        File testPom = new File(getBasedir(), "src/test/resources/sample-pom.xml");
        configMojo = (ConfigMojo) lookupMojo("read-config", testPom);
    }

    public void testSetup() throws Exception {
        assertNotNull(configMojo);
        assertNotNull(configMojo.getRootDirectory());
        assertNotNull(configMojo.getOutputDirectory());
        assertNotNull(configMojo.getOutputFilename());
    }
    
    public void testReadConfig() throws Exception {
        configMojo.execute();
    }
    
}
