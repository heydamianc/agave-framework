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
package agave.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import agave.conversion.Converter;
import agave.conversion.StringConverter;
import agave.exception.ConversionException;
import agave.exception.ParameterBindingException;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class ParameterBinderImpl implements ParameterBinder {

    private Object form;
    private HandlerDescriptor descriptor;

    public ParameterBinderImpl(Object form, HandlerDescriptor descriptor) {
        this.form = form;
        this.descriptor = descriptor;
    }

    @SuppressWarnings("unchecked")
    public void bindRequestParameters(HttpServletRequest request) throws ParameterBindingException, 
        ConversionException {
        
        Map<String, String[]> parameterMap = (Map<String, String[]>) request.getParameterMap();
        
        for (String parameterName : parameterMap.keySet()) {
            String[] values = parameterMap.get(parameterName);
            if (values != null && values.length > 0) {
                Method parameterMutator = descriptor.getMutators().get(parameterName);
                if (parameterMutator != null) {
                    Class<? extends Converter<?, ?>> converterClass = descriptor.getConverters().get(parameterName);
                    if (converterClass == null) {
                        if (parameterMutator.getParameterTypes().length > 0) {
                            Class argumentType = parameterMutator.getParameterTypes()[0];
                            try {
                                if (argumentType.isAssignableFrom(String.class)) {
                                    parameterMutator.invoke(form, values[0]);
                                } else if (argumentType.isAssignableFrom(List.class)) {
                                    parameterMutator.invoke(form, Arrays.asList(values));
                                } else if (argumentType.isAssignableFrom(String[].class)) {
                                    parameterMutator.invoke(form, new Object[] { values });
                                } else {
                                    throw new ParameterBindingException(parameterMutator, argumentType);
                                }
                            } catch (IllegalAccessException ex) {
                                throw new ParameterBindingException(parameterMutator, ex);
                            } catch (IllegalArgumentException ex) {
                                throw new ParameterBindingException(parameterMutator, ex);
                            } catch (InvocationTargetException ex) {
                                throw new ParameterBindingException(parameterMutator, ex.getCause());
                            }
                        }
                    } else {
                        try {
                            Converter<?, ?> converter = converterClass.newInstance();
                            if (converter instanceof StringConverter<?>) {
                                parameterMutator.invoke(form, ((StringConverter<?>) converter).convert(values[0]));
                            } else {
                                throw new ConversionException(parameterMutator, converterClass);
                            }
                        } catch (IllegalAccessException ex) {
                            throw new ParameterBindingException(parameterMutator, ex);
                        } catch (IllegalArgumentException ex) {
                            throw new ParameterBindingException(parameterMutator, ex);
                        } catch (InstantiationException ex) {
                            throw new ParameterBindingException(parameterMutator, ex);
                        } catch (InvocationTargetException ex) {
                            throw new ParameterBindingException(parameterMutator, ex.getCause());
                        }
                    }
                }
            }
        }
    }

    public void bindURIParameters(HttpServletRequest request) throws ParameterBindingException, ConversionException {
        Map<String, String> parameterMap = descriptor.getPattern().getParameterMap(request);
        for (String parameterName : parameterMap.keySet()) {
            String parameterValue = parameterMap.get(parameterName);
            Method parameterMutator = descriptor.getMutators().get(parameterName);
            
            if (parameterMutator == null) { // URI parameters are required
                throw new ParameterBindingException("Could not find a mutator for URI parameter \"" + parameterName +
                        "\" on " + descriptor.getFormClass().getName());
            }
            
            try {
                Class<? extends Converter<?, ?>> converterClass = descriptor.getConverters().get(parameterName);
                if (converterClass == null) {
                    parameterMutator.invoke(form, parameterValue);
                } else {
                    Converter<?, ?> converter = converterClass.newInstance();
                    if (converter instanceof StringConverter<?>) {
                        parameterMutator.invoke(form, ((StringConverter<?>) converter).convert(parameterValue));
                    }
                }
            } catch (IllegalAccessException ex) {
                throw new ParameterBindingException(parameterMutator, ex);
            } catch (IllegalArgumentException ex) {
                throw new ParameterBindingException(parameterMutator, ex);
            } catch (InvocationTargetException ex) {
                throw new ParameterBindingException(parameterMutator, ex);
            } catch (InstantiationException ex) {
                throw new ParameterBindingException(parameterMutator, ex.getCause());
            }
        }
    }

}
