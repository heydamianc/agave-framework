package co.cdev.agave;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import co.cdev.agave.configuration.Config;
import co.cdev.agave.configuration.ConfigGenerator;
import co.cdev.agave.configuration.ConfigGeneratorImpl;
import co.cdev.agave.configuration.HandlerDescriptor;
import co.cdev.agave.util.ClassUtils;
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
        formatConfig(config);
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
                    String className = ClassUtils.getClassNameForJavaFile(javaFile, compileSourceRoot);
                    compiledClassLoader.loadClass(className);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        
        return compiledClassLoader;
    }

    private Config createConfigFromCompiledClasses(ClassLoader classLoader) throws MojoExecutionException {
        ConfigGenerator configGenerator = new ConfigGeneratorImpl(classLoader, rootDirectory);
        Config config = null;
        
        try {
            getLog().info("Scanning classes under " + rootDirectory.getAbsolutePath());
            config = configGenerator.generateConfig();
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        
        return config;
    }
    
    private void formatConfig(Config config) {
        getLog().info("Found the following handler methods:");
        
        for (HandlerDescriptor handlerDescriptor : config) {
            StringBuilder methodRepresentation = new StringBuilder();
            methodRepresentation.append(handlerDescriptor.getHandlerClass().getCanonicalName()).append(".");
            methodRepresentation.append(handlerDescriptor.getHandlerMethod().getName()).append("(");
            
            List<Class<?>> parameterClasses = Arrays.asList(handlerDescriptor.getHandlerMethod().getParameterTypes());
            Iterator<Class<?>> parameterClassItr = parameterClasses.iterator();
            
            while (parameterClassItr.hasNext()) {
                methodRepresentation.append(parameterClassItr.next().getSimpleName());
                
                if (parameterClassItr.hasNext()) {
                    methodRepresentation.append(", ");
                }
            }
            
            methodRepresentation.append(")");
            
            getLog().info("  " + methodRepresentation.toString());
            getLog().info("    URI pattern:        " + handlerDescriptor.getURIPattern());
            getLog().info("    HTTP method:        " + handlerDescriptor.getHttpMethod().name().toLowerCase());
            getLog().info("    Form class:         " + (handlerDescriptor.getFormClass() == null ? "none" : handlerDescriptor.getFormClass()));
            getLog().info("    Initiates workflow: " + (handlerDescriptor.initiatesWorkflow() ? "yes" : "no"));
            getLog().info("    Completes workflow: " + (handlerDescriptor.completesWorkflow() ? "yes" : "no"));
            getLog().info("    Workflow name:      " + (handlerDescriptor.getWorkflowName() == null ? "none" : handlerDescriptor.getWorkflowName()));
        }
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
