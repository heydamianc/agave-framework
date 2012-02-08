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
package co.cdev.agave.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import co.cdev.agave.configuration.HandlerDescriptor;
import co.cdev.agave.configuration.ParamDescriptor;
import co.cdev.agave.conversion.AgaveConversionException;
import co.cdev.agave.conversion.StringParamConverter;

/**
 * 
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class MapPopulatorImpl extends AbstractPopulator implements MapPopulator {

    private final HttpServletRequest request;
    private final Map<String, String> uriParams;
    private final HandlerDescriptor descriptor;
    
    public MapPopulatorImpl(HttpServletRequest request, Map<String, String> uriParams, HandlerDescriptor descriptor) {
        super(request.getLocale());
   
        this.request = request;
        this.uriParams = uriParams;
        this.descriptor = descriptor;
    }
    
    @Override
    public void populate(Map<String, Object> namedArguments) throws AgaveConversionException {
        
		@SuppressWarnings("unchecked")
        Map<String, Object[]> requestParams = request.getParameterMap();
        
        for (ParamDescriptor paramDescriptor : descriptor.getParamDescriptors()) {
            Object value = uriParams.get(paramDescriptor.getName());
            
            if (value == null) {

                // TODO TRY AND MAKE THIS SUPPORT ARRAYS AS WELL

                String[] values = (String[]) requestParams.get(paramDescriptor.getName());

                if (values != null && values.length > 0) {
                    value = requestParams.get(paramDescriptor.getName())[0];
                }
            }
            
            if (value != null) {
                StringParamConverter<?> converter = null;
                Class<? extends StringParamConverter<?>> converterClass = paramDescriptor.getConverterClass();
                
                if (converterClass != null) {
                    try {
                        converter = converterClass.newInstance();
                    } catch (InstantiationException ex) {
                        throw new AgaveConversionException(ex);
                    } catch (IllegalAccessException ex) {
                        throw new AgaveConversionException(ex);
                    }
                } else {
                    converter = (StringParamConverter<?>) determineMostAppropriateConverter(paramDescriptor.getParameterClass());
                }
                
                if (converter != null) {
                    value = converter.convert((String) value, locale);
                    namedArguments.put(paramDescriptor.getName(), value);
                }
            }
            
            if (value == null 
                    && (paramDescriptor.getParameterClass() == int.class
                    || paramDescriptor.getParameterClass() == long.class
                    || paramDescriptor.getParameterClass() == short.class
                    || paramDescriptor.getParameterClass() == float.class
                    || paramDescriptor.getParameterClass() == double.class)) {
                namedArguments.put(paramDescriptor.getName(), 0);
            } else {
                namedArguments.put(paramDescriptor.getName(), value);
            }
        }
    }
    
}
