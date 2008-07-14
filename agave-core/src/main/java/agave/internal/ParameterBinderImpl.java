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

import agave.conversion.Converter;
import agave.conversion.ConversionException;
import agave.conversion.StringConverter;
import agave.conversion.ListConverter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

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

    public void bindRequestParameters(HttpServletRequest request) throws ConversionException {
        Map<String, String[]> parameterMap = (Map<String, String[]>)request.getParameterMap();
        try {
            for (String parameterName : parameterMap.keySet()) {
                String[] values = parameterMap.get(parameterName);
                if (values != null && values.length > 0) {
                    Method parameterSetter = descriptor.getParameterSetters().get(parameterName);
                    Class<? extends Converter<?,?>> converterClass = 
                        descriptor.getParameterConverters().get(parameterName);
                    if (converterClass == null) {
                        if (parameterSetter.getParameterTypes().length > 0) {
                            Class parameterType = parameterSetter.getParameterTypes()[0];
                            if (parameterType.isAssignableFrom(String.class)) {
                                parameterSetter.invoke(form, values[0]);
                            } else if (parameterType.isAssignableFrom(List.class)) {
                                parameterSetter.invoke(form, Arrays.asList(values));
                            } else if (parameterType.isAssignableFrom(String[].class)) {
                                // wrap the array in an object array - the jvm will unwrap 
                                // it and use the string array instead of using each string
                                // element as a separate argument in the var args
                                parameterSetter.invoke(form, new Object[] {values});
                            }
                        }
                    } else {
                        Converter<?,?> converter = converterClass.newInstance();
                        if (converter instanceof StringConverter<?>) {
                            parameterSetter.invoke(form, ((StringConverter<?>)converter).convert(values[0]));
                        } else if (converter instanceof ListConverter<?>) {
                            parameterSetter.invoke(form, ((ListConverter<?>)converter).convert(values));
                        }
                    }
                }
            }
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        }
    }

    public void bindURIParameters(HttpServletRequest request) throws ConversionException {
        Map<String, String> parameterMap = descriptor.getPattern().getParameterMap(request.getRequestURI());
        try {
            for (String parameterName : parameterMap.keySet()) {
                String parameterValue = parameterMap.get(parameterName);
                Method parameterSetter = descriptor.getParameterSetters().get(parameterName);       
                Class<? extends Converter<?,?>> converterClass = 
                    descriptor.getParameterConverters().get(parameterName);
                if (converterClass == null) {
                    parameterSetter.invoke(form, parameterValue);
                } else {
                    Converter<?,?> converter = converterClass.newInstance();
                    if (converter instanceof StringConverter<?>) {
                        parameterSetter.invoke(form, ((StringConverter<?>)converter).convert(parameterValue));
                    }
                }
            }
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        }
    }

}
