package co.cdev.agave.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import co.cdev.agave.AgaveConfigurationException;

public interface ConfigReader {

    public abstract Config readConfig(File rootDirectory) throws FileNotFoundException, IOException,
            ClassNotFoundException, AgaveConfigurationException;

}