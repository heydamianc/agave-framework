/*
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
package co.cdev.agave.internal;

import co.cdev.agave.conversion.StringParamConverter;
import co.cdev.agave.exception.ConversionException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class MapPopulatorImpl extends AbstractPopulator implements MapPopulator {

    private final HttpServletRequest request;
    private final HandlerDescriptor descriptor;
    
    public MapPopulatorImpl(HttpServletRequest request, HandlerDescriptor descriptor) {
        super(request.getLocale());
   
        this.request = request;
        this.descriptor = descriptor;
    }
    
    @Override
    public void populate(Map<String, Object> namedArguments) throws ConversionException {
        Map<String, String> uriParams = descriptor.getPattern().getParameterMap(request);
        Map<String, String> requestParams = request.getParameterMap();
        
        for (String name : namedArguments.keySet()) {
            String value = uriParams.get(name);
            
            if (value == null) {
                value = requestParams.get(name);
            }
            
            if (value != null) {
                Class<? extends StringParamConverter<?>> converterClass = descriptor.getConverters().get(name);
                
                if (converterClass != null) {
                    try {
                        StringParamConverter<?> converter = converterClass.newInstance();
                        namedArguments.put(name, converter.convert(value, request.getLocale()));
                        
                        continue;
                    } catch (InstantiationException ex) {
                        Logger.getLogger(MapPopulatorImpl.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(MapPopulatorImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
            namedArguments.put(name, value);
        }
    }
    
}
