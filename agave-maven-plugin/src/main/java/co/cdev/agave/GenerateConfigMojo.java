package co.cdev.agave;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import co.cdev.agave.configuration.Config;
import co.cdev.agave.configuration.ConfigGenerator;
import co.cdev.agave.configuration.ConfigGeneratorImpl;
import co.cdev.agave.util.FileSystem;

/**
 * @goal generate-config
 * @phase process-classes
 * @requiresDependencyResolution compile
 */
public class GenerateConfigMojo extends AbstractMojo {
    
    /**
     * @parameter expression="${agave.config.rootDirectory}" default-value="${project.build.outputDirectory}"
     */
    private File rootDirectory;
    
    /**
     * @parameter expression="${agave.config.outputDirectory}" default-value="${project.build.outputDirectory}"
     */
    private File outputDirectory;
    
    /**
     * @parameter expression="${agave.config.outputFilename}" default-value="agave.conf"
     */
    private String outputFilename;
    
    /**
     * @parameter expression="${project}"
     * @required
     */
    protected MavenProject project;
    
    public void execute() throws MojoExecutionException {
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
        
        ClassLoader classLoader = loadCompiledClasses();
        Config config = createConfigFromCompiledClasses(classLoader);
        writeConfigToFile(config, new File(outputDirectory, outputFilename));
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

    private ClassLoader loadCompiledClasses() throws MojoExecutionException {
        List<URL> urls = new ArrayList<URL>();
        
        try {
            for (Object compiledClasspathelementPath : project.getCompileClasspathElements()) {
                File compiledClasspathElement = new File((String) compiledClasspathelementPath);
                urls.add(compiledClasspathElement.toURI().toURL());
                getLog().info("Adding " + compiledClasspathElement.getAbsolutePath() + " to classpath");            
            }
        } catch (MalformedURLException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        
        FileFilter javaFileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".java");
            }
        };
        
        ClassLoader compiledClassLoader = new URLClassLoader(urls.toArray(new URL[] {}), getClass().getClassLoader());
        
        try {
            for (Object compileSourceRootPath : project.getCompileSourceRoots()) {
                File compileSourceRoot = new File((String) compileSourceRootPath);
                
                for (File javaFile : FileSystem.filterFiles(compileSourceRoot, javaFileFilter)) {
                    String className = getClassNameForFile(javaFile, compileSourceRoot);
                    getLog().info("Loading " + className);
                    compiledClassLoader.loadClass(className);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        
        return compiledClassLoader;
    }
    
    private String getClassNameForFile(File javaFile, File compileSourceRoot) {
        List<String> parts = new ArrayList<String>();
        parts.add(javaFile.getName().replace(".java", ""));
        
        File dir = javaFile.getParentFile();
        while (!dir.equals(compileSourceRoot)) {
            parts.add(0, dir.getName());
            dir = dir.getParentFile();
        }
        
        StringBuilder className = new StringBuilder();
        
        Iterator<String> itr = parts.iterator();
        while (itr.hasNext()) {
            className.append(itr.next());
            
            if (itr.hasNext()) {
                className.append(".");
            }
        }
        
        return className.toString();
    }
    
    private Config createConfigFromCompiledClasses(ClassLoader classLoader) throws MojoExecutionException {
        ConfigGenerator configGenerator = new ConfigGeneratorImpl(classLoader, rootDirectory);
        Config config = null;
        
        try {
            getLog().info("Scanning for classes under " + rootDirectory.getAbsolutePath());
            config = configGenerator.generateConfig();
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        
        return config;
    }
    
    private void writeConfigToFile(Config config, File configFile) throws MojoExecutionException {
        try {
            config.writeToFile(configFile);
            getLog().info("Wrote Agave configuration file to " + configFile.getAbsolutePath());
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
    
}
