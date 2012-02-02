package co.cdev.agave.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.objectweb.asm.ClassReader;

import co.cdev.agave.AgaveConfigurationException;

public class ConfigReaderImpl implements ConfigReader {

    @Override
    public Config scanForHandlers(File rootDirectory) 
            throws FileNotFoundException, IOException, ClassNotFoundException, AgaveConfigurationException {
        Config config = new ConfigImpl();
        scanForHandlers(rootDirectory, config);
        return config;
    }
    
    private void scanForHandlers(File rootDirectory, Config config)
            throws FileNotFoundException, IOException, ClassNotFoundException, AgaveConfigurationException {
        if (rootDirectory != null && rootDirectory.isDirectory() && rootDirectory.canRead()) {
            for (File node : rootDirectory.listFiles()) {
                if (node.isDirectory()) {
                    scanForHandlers(node, config);
                } else if (node.isFile() && node.getName().endsWith(".class")) {
                    FileInputStream nodeIn = new FileInputStream(node);
                    
                    try {
                        ClassReader classReader = new ClassReader(nodeIn);
                        Collection<ScanResult> scanResults = new ArrayList<ScanResult>();
                        classReader.accept(new HandlerScanner(scanResults), ClassReader.SKIP_CODE);

                        for (ScanResult scanResult : scanResults) {
                            HandlerDescriptor descriptor = new HandlerDescriptorImpl(scanResult);
                            descriptor.locateAnnotatedHandlerMethods(scanResult);
                            config.addHandlerDescriptor(descriptor);
                        }
                    } finally {
                        nodeIn.close();
                    }
                }
            }
        }
    }
    
}
