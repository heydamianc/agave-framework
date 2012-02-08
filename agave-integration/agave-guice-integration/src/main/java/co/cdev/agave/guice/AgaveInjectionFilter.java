package co.cdev.agave.guice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import co.cdev.agave.AgaveFilter;
import co.cdev.agave.HandlerFactory;
import co.cdev.agave.configuration.HandlerDescriptor;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Integrates Agave's handler and form creation into Guice's dependency injection mechanism.
 * 
 * @author <a href="damiancarrillo@gmail.com>Damian Carrillo</a>
 */
public class AgaveInjectionFilter extends AgaveFilter {

    public static final String PARENT_INJECTOR = AgaveInjectionFilter.class.getName() + ".PARENT_INJECTOR";
    
    private InjectionHandlerFactory injectionHandlerFactory;
    protected Injector injector;
    
    @Override
    public void init(javax.servlet.FilterConfig config) throws ServletException {
        injectionHandlerFactory = new InjectionHandlerFactory();
        
        super.init(config);
        
        Set<Class<?>> handlerClasses = new HashSet<Class<?>>();
        for (HandlerDescriptor descriptor : super.getConfig()) {
            handlerClasses.add(descriptor.getHandlerClass());
        }
        
        Module handlerModule = new HandlersModule(handlerClasses);
        
        // Automatically bind all handlers
        
        List<Module> modules = new ArrayList<Module>();
        modules.add(handlerModule);
        modules.addAll(provideGuiceModules());
        
        injector = (Injector) config.getServletContext().getAttribute(PARENT_INJECTOR);
        injector = injector == null ? Guice.createInjector(modules) : injector.createChildInjector(modules);
        
        injectionHandlerFactory.setInjector(injector);
    }
    
    @Override
    public void destroy() {
        super.destroy();
    }

    /**
     * Provides a default implementation of a handlerFactory if it has not been overridden in the
     * web.xml.
     * 
     * @param config The filter configuration 
     * @return the handler factory
     * 
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException 
     */
    @Override
    protected HandlerFactory provideHandlerFactory(FilterConfig config) throws ClassNotFoundException, 
            InstantiationException, IllegalAccessException {
        return injectionHandlerFactory;
    }
    
    /**
     * Provides additional Guice modules for the whole web application.  The handlers are included
     * automatically, so there is no need to rebind them.  The default implementation returns an
     * empty list, however you can override this and supply more modules.
     * 
     * @return all Guice modules
     */
    protected Collection<Module> provideGuiceModules() {
        return Collections.emptyList();
    }
    
    public Injector getInjector() {
        return injector;
    }
    
    private static class HandlersModule extends AbstractModule {
        
        private final Set<Class<?>> handlerClasses;

        public HandlersModule(Set<Class<?>> handlerClasses) {
            this.handlerClasses = handlerClasses;
        }
        
        @Override
        protected void configure() {
            for (Class<?> handlerClass : handlerClasses) {
                bind(handlerClass);
            }
        }
    }
    
}
