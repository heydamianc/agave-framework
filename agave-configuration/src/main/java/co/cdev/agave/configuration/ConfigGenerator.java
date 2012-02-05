package co.cdev.agave.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import co.cdev.agave.AgaveConfigurationException;

public interface ConfigGenerator {

    public abstract Config scanClassesWithinRootDirectory(File rootDirectory) throws FileNotFoundException, IOException,
            ClassNotFoundException, AgaveConfigurationException;

}