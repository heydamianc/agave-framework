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

import agave.ConvertWith;
import agave.conversion.*;
import agave.exception.ConversionException;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="mailto:damianarrillo@gmail.com">Damian Carrillo</a>
 */
public class FormPopulatorImpl implements FormPopulator {

    private SortedMap<String, List<String>> parameters = new TreeMap<String, List<String>>();

    public FormPopulatorImpl(HttpServletRequest request) {
        collectParameters(request);
    }
    
    private void collectParameters(HttpServletRequest request) {
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            List<String> parameterValues = new ArrayList<String>();
            if (request.getParameterValues(parameterName) != null) {
                parameterValues.addAll(Arrays.asList(request.getParameterValues(parameterName)));
            }
            parameters.put(parameterName, parameterValues);
        }
    }
    
    public SortedMap<String, List<String>> getParameters() {
        return parameters;
    }

    public void populate(Object formInstance) 
        throws NoSuchMethodException, 
               SecurityException, 
               IllegalAccessException, 
               IllegalArgumentException, 
               InvocationTargetException,
               InstantiationException,
               ConversionException {
        CallChain callChain = null;
        for (String parameterName : parameters.keySet()) {
            List<String> parameterValues = parameters.get(parameterName);
            boolean unique = true;
            if (parameterValues != null && parameterValues.size() > 1) {
                unique = false;
            }
            callChain = new CallChainImpl(parameterName, unique);
            populateProperty(formInstance, callChain, parameterValues);
        }
    }
    
    private void populateProperty(Object formInstance, CallChain callChain, List<String> parameterValues) 
    throws NoSuchMethodException, 
           SecurityException, 
           IllegalAccessException, 
           IllegalArgumentException, 
           InvocationTargetException,
           InstantiationException,
           ConversionException {
        Object targetInstance = formInstance;
        Class<?> targetClass = targetInstance.getClass();
    
        for (String accessorName : callChain.getAccessorNames()) {
            try {
                Method accessor = targetClass.getMethod(accessorName);
                targetInstance = accessor.invoke(targetInstance);
                targetClass = targetInstance.getClass();
            } catch (NoSuchMethodException ex) {
                throw new NoSuchMethodException("Missing accessor \"" + accessorName + "\" on " + targetClass.getName() 
                    + " invoked through request parameter \"" + callChain.getParameterName() + "\"");
            }
        }
    
        for (Method mutator : targetClass.getMethods()) {
            if (mutator.getName().equals(callChain.getMutatorName())) {
                String parameterValue = null;
                switch (callChain.getMutatorType()) {
                    case SETTING:
                        if (parameterValues != null && !parameterValues.isEmpty()) {
                            parameterValue = parameterValues.get(0);
                        }
                        setOrAppendProperty(mutator, targetInstance, parameterValue);
                        break;
                    case APPENDING:
                        for (String param: parameterValues) {
                            setOrAppendProperty(mutator, targetInstance, param);
                        }
                        break;
                    case INSERTING:
                        if (parameterValues != null && !parameterValues.isEmpty()) {
                            parameterValue = parameterValues.get(0);
                        }
                        insertProperty(mutator, targetInstance, callChain.getIndex(), parameterValue);
                        break;
                    case PUTTING:
                        if (parameterValues != null && !parameterValues.isEmpty()) {
                            parameterValue = parameterValues.get(0);
                        }
                        putProperty(mutator, targetInstance, callChain.getKey(), parameterValue);
                        break;
                }
            }
        }
    }
    
    private void setOrAppendProperty(Method mutator, Object targetInstance, String parameterValue) 
        throws IllegalAccessException, 
               IllegalArgumentException, 
               InvocationTargetException, 
               InstantiationException,
               ConversionException {
        mutator.invoke(targetInstance, convertIfNecessary(mutator, parameterValue));
    }

    private void insertProperty(Method mutator, Object targetInstance, int index, String parameterValue)
        throws IllegalAccessException, 
               IllegalArgumentException, 
               InvocationTargetException, 
               InstantiationException,
               ConversionException {
        mutator.invoke(targetInstance, index, convertIfNecessary(mutator, parameterValue));
    }
    
    private void putProperty(Method mutator, Object targetInstance, String key, String parameterValue)
    throws IllegalAccessException, 
           IllegalArgumentException, 
           InvocationTargetException, 
           InstantiationException,
           ConversionException {
        mutator.invoke(targetInstance, key, convertIfNecessary(mutator, parameterValue));
    }
    
    private Object convertIfNecessary(Method mutator, String parameterValue) 
    throws ConversionException, 
           InstantiationException,
           IllegalAccessException {
        Class<?>[] parameterTypes = mutator.getParameterTypes();
        
        if (parameterTypes != null) {
            int parameterOffset = (parameterTypes.length == 1) ? 0 : 1;
            Class<?> parameterType = parameterTypes[parameterOffset];
            ConvertWith converterAnnotation = null;
            StringConverter<?> converter = null;
            
            // first look for a ConvertWith annotation
            for (Annotation annotation : mutator.getParameterAnnotations()[parameterOffset]) {
                if (annotation instanceof ConvertWith) {
                    converter = (StringConverter<?>)((ConvertWith)annotation).value().newInstance();
                    break;
                }
            }
            
            // try to look up a converter for common types
            if (converter == null) {
                if (parameterType.isAssignableFrom(Boolean.class) || parameterType.isAssignableFrom(boolean.class)) {
                    converter = new BooleanConverter();
                } else if (parameterType.isAssignableFrom(Byte.class) || parameterType.isAssignableFrom(byte.class)) {
                    converter = new ByteConverter();
                } else if (parameterType.isAssignableFrom(Character.class) || parameterType.isAssignableFrom(char.class)) {
                    converter = new CharacterConverter();
                } else if (parameterType.isAssignableFrom(Double.class) || parameterType.isAssignableFrom(double.class)) {
                    converter = new DoubleConverter();
                } else if (parameterType.isAssignableFrom(Float.class) || parameterType.isAssignableFrom(float.class)) {
                    converter = new FloatConverter();
                } else if (parameterType.isAssignableFrom(Integer.class) || parameterType.isAssignableFrom(int.class)) {
                    converter = new IntegerConverter();
                } else if (parameterType.isAssignableFrom(Long.class) || parameterType.isAssignableFrom(long.class)) {
                    converter = new LongConverter();
                } 
            }
            
            if (converter != null) {
                return converter.convert(parameterValue);
            }
        }
        
        return parameterValue;
    }
    
}
