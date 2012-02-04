package co.cdev.agave;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import co.cdev.agave.configuration.Config;
import co.cdev.agave.configuration.ConfigReader;
import co.cdev.agave.configuration.ConfigReaderImpl;

/**
 * @goal read-config
 * @phase process-classes
 */
public class ConfigMojo extends AbstractMojo {
    
    /**
     * @parameter expression="${agave.config.rootDirectory}" default-value="${project.build.directory}"
     */
    private File rootDirectory;
    
    /**
     * @parameter expression="${agave.config.outputDirectory}" default-value="${project.build.directory}"
     */
    private File outputDirectory;
    
    /**
     * @parameter expression="${agave.config.outputFilename}" default-value="agave.conf"
     */
    private String outputFilename;

    public void execute() throws MojoExecutionException {
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
        
        ConfigReader configReader = new ConfigReaderImpl();
        Config config = null;
        
        try {
            config = configReader.readConfig(rootDirectory);
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (AgaveConfigurationException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        
        File configFile = new File(outputDirectory, outputFilename);
        
        OutputStream out = null;
        ObjectOutputStream objectOut = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(configFile));
            objectOut = new ObjectOutputStream(out);
            objectOut.writeObject(config);
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                
                if (objectOut != null) {
                    objectOut.close();
                }
            } catch (Throwable e) {
                // do nothing
            }
        }
    }

    public File getRootDirectory() {
        return rootDirectory;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public String getOutputFilename() {
        return outputFilename;
    }
    
}
