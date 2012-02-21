package co.cdev.agave.configuration;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import co.cdev.agave.CompletesWorkflow;
import co.cdev.agave.HttpMethod;
import co.cdev.agave.InitiatesWorkflow;
import co.cdev.agave.Param;
import co.cdev.agave.Route;
import co.cdev.agave.URIPattern;
import co.cdev.agave.URIPatternImpl;
import co.cdev.agave.conversion.Converters;
import co.cdev.agave.conversion.NoopConverter;
import co.cdev.agave.conversion.StringConverter;
import co.cdev.agave.util.ClassUtils;

public class ConfigGeneratorImpl implements ConfigGenerator {

    private static Logger LOGGER = Logger.getLogger(ConfigGeneratorImpl.class.getName()); 
    
    private final ClassLoader classLoader;
    private final File rootDir;
    private final FileFilter classFilter;
    
    public ConfigGeneratorImpl(File rootDir) {
        this(ConfigGenerator.class.getClassLoader(), rootDir);
    }
    
    public ConfigGeneratorImpl(ClassLoader classLoader, File rootDir) {
        this.classLoader = classLoader;
        this.rootDir = rootDir;
        this.classFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".class");
            }
        };
    }
    
    public Config generateConfig() throws IOException, ClassNotFoundException {
        Config config = new ConfigImpl();
        
        if (rootDir != null && rootDir.isDirectory() && rootDir.canRead()) {
            populateConfig(rootDir, config);
        }
        
        return config;
    }
    
    private void populateConfig(File node, Config config) throws IOException, ClassNotFoundException {
        if (node.isDirectory() && node.canRead()) {
            for (File child : node.listFiles()) {
                populateConfig(child, config);
            }
        } else if (node.isFile() && node.canRead() && classFilter.accept(node)) {
            String className = ClassUtils.getClassNameForClassFile(node, rootDir);
            Class<?> candidateClass = Class.forName(className, true, classLoader);
            inspectCandidateClass(candidateClass, config);
        }
    }
    
    private void inspectCandidateClass(Class<?> candidateClass, Config config) {
        for (Method candidateMethod : candidateClass.getMethods()) {
            Route routeAnnotation = candidateMethod.getAnnotation(Route.class);

            if (routeAnnotation != null) {
                Class<?> handlerClass = candidateClass;
                Method handlerMethod = candidateMethod;
               
                URIPattern uriPattern = null;
                
                if (!routeAnnotation.uri().equals("")) {
                    uriPattern = new URIPatternImpl(routeAnnotation.uri());
                } else if (!routeAnnotation.value().equals("")) {
                    uriPattern = new URIPatternImpl(routeAnnotation.value());
                }
                
                if (uriPattern == null) {
                    LOGGER.severe("Expected @Route annotation to have a valid URI path in either \"uri\" or \"value\" in " + candidateMethod);
                    continue;
                }
                
                HttpMethod httpMethod = routeAnnotation.method();
                
                boolean initiatesWorkflow = false;
                boolean completesWorkflow = false;
                String workflowName = null;

                CompletesWorkflow completesWorkflowAnnotation = candidateMethod.getAnnotation(CompletesWorkflow.class);
                
                if (completesWorkflowAnnotation != null) {
                    initiatesWorkflow = false;
                    completesWorkflow = true;
                    workflowName = completesWorkflowAnnotation.value();
                }
                
                InitiatesWorkflow initiatesWorkflowAnnotation = candidateMethod.getAnnotation(InitiatesWorkflow.class);
                
                if (initiatesWorkflowAnnotation != null) {
                    initiatesWorkflow = true;
                    completesWorkflow = false;
                    workflowName = initiatesWorkflowAnnotation.value();
                }
                
                Class<?> formClass = null;
                List<ParamDescriptor> paramDescriptors = Collections.emptyList();
                
                if (candidateMethod.getParameterTypes().length < 1) {
                    LOGGER.severe("Expected at least a single RoutingContext parameter to " + candidateMethod);
                    continue;
                } else if (candidateMethod.getParameterTypes().length == 1) {
                    // simple case handled by AgaveFilter
                } else if (candidateMethod.getParameterTypes().length >= 2) {
                    Annotation[][] parameterAnnotations = candidateMethod.getParameterAnnotations();
                    
                    for (int i = 1; i < parameterAnnotations.length; i++) {
                        for (int j = 0; j < parameterAnnotations[i].length; j++) {
                            Class<? extends Annotation> annotationType = parameterAnnotations[i][j].annotationType();
                            if (Param.class.isAssignableFrom(annotationType)) {
                                Param param = (Param) parameterAnnotations[i][j];
                                
                                Class<?> paramClass = candidateMethod.getParameterTypes()[i]; 
                                String paramName = param.value(); 
                                if (paramName == null || paramName.equals("")) {
                                    paramName = param.name();
                                }
                                
                                Class<? extends StringConverter<?>> converterClass = param.converter();
                                
                                if (converterClass == null || converterClass.equals(NoopConverter.class)) {
                                    converterClass = Converters.getMostAppropriateConverterClassFor(paramClass);
                                    
                                    if (converterClass == null) {
                                        converterClass = NoopConverter.class;
                                    }
                                }
                                
                                if (paramDescriptors.isEmpty()) {
                                    paramDescriptors = new ArrayList<ParamDescriptor>();
                                }
                                
                                paramDescriptors.add(new ParamDescriptorImpl(paramClass, paramName, converterClass));
                            }
                        }
                    }
                }
                
                if (paramDescriptors.isEmpty() && candidateMethod.getParameterTypes().length == 2) {
                    formClass = candidateMethod.getParameterTypes()[1];
                }
                
                try {
                    config.addHandlerDescriptor(new HandlerDescriptorImpl(handlerClass,
                                                                          handlerMethod,
                                                                          uriPattern,
                                                                          httpMethod,
                                                                          initiatesWorkflow,
                                                                          completesWorkflow,
                                                                          workflowName,
                                                                          formClass,
                                                                          paramDescriptors));
                } catch (DuplicateDescriptorException e) {
                    LOGGER.severe("Duplicate descriptor method: " + candidateMethod);
                    continue;
                }
                
                // TODO VALIDATE HANDLER DESCRIPTOR
            }
        }
    }
    
}
