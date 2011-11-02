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

import co.cdev.agave.AgaveFilter;
import co.cdev.agave.HandlerFactory;
import co.cdev.agave.LifecycleHooks;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

/**
 * Integrates Agave's handler and form creation into Guice's dependency injection mechanism.
 * 
 * @author <a href="damiancarrillo@gmail.com>Damian Carrillo</a>
 */
public abstract class AgaveInjectionFilter extends AgaveFilter {

    private final Set<Class<?>> handlerClasses;
    private Injector injector;

    public AgaveInjectionFilter() {
        handlerClasses = new HashSet<Class<?>>();
    }
    
    @Override
    public void init(javax.servlet.FilterConfig config) throws ServletException {
        super.init(config);
        
        Module handlerModule = new AbstractModule() {
            @Override
            protected void configure() {
                for (Class<?> handlerClass : handlerClasses) {
                    bind(handlerClass);
                }
            }
        };
        
        // Automatically bind all handlers
        
        List<Module> modules = new ArrayList<Module>();
        modules.add(handlerModule);
        modules.addAll(provideGuiceModules());
        
        injector = Guice.createInjector(modules);
    }
    
    @Override
    public void destroy() {
        injector = null;        
        super.destroy();
    }

    @Override
    protected LifecycleHooks provideLifecycleHooks(FilterConfig config) throws ClassNotFoundException, 
            InstantiationException, IllegalAccessException {
        LifecycleHooks configuredHooks = super.provideLifecycleHooks(config);
        return new HandlerLifecycleHooks(handlerClasses, configuredHooks);
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
        HandlerFactory defaultHandlerFactory = super.provideHandlerFactory(config);
        
        if (defaultHandlerFactory != null) {
            return defaultHandlerFactory;
        } else {
            return new InjectionHandlerFactory(injector);
        }
    }
    
    /**
     * Provides additional Guice modules for the whole web application.  The handlers are included
     * automatically, so there is no need to rebind them.
     * 
     * @return all Guice modules
     */
    protected abstract Collection<Module> provideGuiceModules();
    
}
