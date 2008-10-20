/**
 * Copyright (c) 2008, Damian Carrillo
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
package agave.guice;

import java.util.ArrayList;
import java.util.Collection;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class PastebinInstanceFactory extends AbstractGuiceInstanceFactory {

    @Override
    public Iterable<Module> supplyModules() {
        Collection<Module> modules = new ArrayList<Module>();
        
        final Module formModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(PastebinForm.class);
            }
        };
        
        final Module handlerModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(PastebinHandler.class);
            }
        };
        
        // View Tier
        
        modules.add(new AbstractModule() {
            @Override
            protected void configure() {
                install(formModule);
                install(handlerModule);
            }
        });
        
        // Services
        
        modules.add(new AbstractModule() {
            @Override
            protected void configure() {
                bind(SnippetService.class).to(SnippetServiceImpl.class);
            }
        });
        
        // DAOs
        
        modules.add(new AbstractModule() {
            @Override
            protected void configure() {
                bind(SnippetDao.class).to(SnippetDaoImpl.class);
            }
        });
        
        return modules;
    }

}
