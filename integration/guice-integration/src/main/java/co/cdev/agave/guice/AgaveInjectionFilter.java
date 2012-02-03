/*
 * Copyright (c) 2011, Damian Carrillo
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of 
 *     conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of 
 *     conditions and the following disclaimer in the documentation and/or other materials 
 *     provided with the distribution.
 *   * Neither the name of the copyright holder's organization nor the names of its contributors 
 *     may be used to endorse or promote products derived from this software without specific 
 *     prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package co.cdev.agave.guice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import co.cdev.agave.AgaveFilter;
import co.cdev.agave.HandlerFactory;
import co.cdev.agave.configuration.HandlerDescriptor;

import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;

/**
 * Integrates Agave's handler and form creation into Guice's dependency injection mechanism.
 * 
 * @author <a href="damiancarrillo@gmail.com>Damian Carrillo</a>
 */
public class AgaveInjectionFilter extends AgaveFilter {

    public static final String PARENT_INJECTOR = AgaveInjectionFilter.class.getName() + ".PARENT_INJECTOR";
    private static final Logger LOGGER = Logger.getLogger(AgaveInjectionFilter.class.getName());
    
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
        
        if (LOGGER.isLoggable(Level.INFO)) {
            printBindings(injector);
        }
    }
    
    private void printBindings(Injector injector) {
        if (injector.getParent() != null) {
            printBindings(injector.getParent());
        }
        
        Map<Key<?>, Binding<?>> bindings = injector.getAllBindings();
        for (Key<?> key : bindings.keySet()) {
            LOGGER.log(Level.INFO, "Bound {0}", key.getTypeLiteral().getRawType().getName());
        }
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
