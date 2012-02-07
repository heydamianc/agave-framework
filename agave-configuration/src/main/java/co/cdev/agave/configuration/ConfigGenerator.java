package co.cdev.agave.configuration;

import java.io.IOException;

public interface ConfigGenerator {

    public Config generateConfig() throws IOException, ClassNotFoundException;

}