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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import co.cdev.agave.CompletesWorkflow;
import co.cdev.agave.HttpMethod;
import co.cdev.agave.InitiatesWorkflow;
import co.cdev.agave.Param;
import co.cdev.agave.ResumesWorkflow;
import co.cdev.agave.conversion.PassThroughParamConverter;
import co.cdev.agave.conversion.StringParamConverter;
import co.cdev.agave.exception.InvalidHandlerException;
import co.cdev.agave.exception.InvalidParamException;

/**
 * A descriptor that serves as the configuration for the building of handlers
 * and routing requests into them.
 * 
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public final class HandlerMethodDescriptorImpl implements HandlerMethodDescriptor {

    private URIPattern pattern;
    private HttpMethod method;
    private Class<?> handlerClass;
    private Method handlerMethod;
    private List<ParamDescriptor> paramDescriptors; // only applicable when @Param is used
    private Class<?> formClass;
    private boolean initiatesWorkflow;
    private boolean completesWorkflow;
    private String workflowName;

    public HandlerMethodDescriptorImpl(HandlerIdentifier identifier) throws ClassNotFoundException, InvalidHandlerException {
        pattern = new URIPatternImpl(identifier.getUri());
        method = identifier.getMethod();
        handlerClass = Class.forName(identifier.getClassName());
        paramDescriptors = Collections.emptyList();
    }

    /**
     * Locates annotated methods on a handler class. This method will find the
     * annotated method that is identified by the supplied
     * {@code HandlerIdentifier} and then proceed to find any workflow-related
     * annotations ({@code InitiatesWorkflow}, {@code ResumesWorkflow},
     * {@code CompletesWorkflow}.
     * 
     * @param identifier
     *            the {@code HandlerIdentifier} that was created while scanning
     *            for a handler method
     */
    @Override
    public void locateAnnotatedHandlerMethods(HandlerIdentifier identifier) throws InvalidHandlerException {
        for (Method m : handlerClass.getMethods()) {
            if (identifier.matches(m)) {
                handlerMethod = m;
                
                int paramCount = handlerMethod.getParameterTypes().length;
                
                // Account for the HandlerContext as the 0th argument
                
                paramDescriptors = new ArrayList<ParamDescriptor>(paramCount - 1);
                
                Annotation[][] allAnnotations = handlerMethod.getParameterAnnotations();

                try {
                    if (allAnnotations != null && allAnnotations.length == paramCount) {
                        for (int i = 1; i < paramCount; i++) {
                            Class<?> paramType = m.getParameterTypes()[i];
                            Annotation[] annotations = m.getParameterAnnotations()[i];

                            ParamDescriptor descriptor = ParamDescriptor.createIfApplicable(paramType, annotations);

                            if (descriptor == null) {
                                break;
                            }

                            paramDescriptors.add(descriptor);
                        }
                    }
                } catch (InvalidParamException ex) {
                    String message = String.format("%s is an invalid handler method", m);
                    throw new InvalidHandlerException(message, ex);
                }
                
                // Once again, account for the HandlerContext as the 0th argument
                
                if (paramDescriptors.size() != paramCount - 1) {
                    paramDescriptors = Collections.emptyList();
                }
                
                // Now, if the parameters weren't all marked as being named @Params,
                // treat an additional argument as a form
                
                if (paramDescriptors.isEmpty() && paramCount == 2) {
                    formClass = handlerMethod.getParameterTypes()[1];
                }

                if (m.getAnnotation(InitiatesWorkflow.class) != null) {
                    initiatesWorkflow = true;
                    completesWorkflow = false;
                    workflowName = m.getAnnotation(InitiatesWorkflow.class).value();
                } else if (m.getAnnotation(CompletesWorkflow.class) != null) {
                    initiatesWorkflow = false;
                    completesWorkflow = true;
                    workflowName = m.getAnnotation(CompletesWorkflow.class).value();
                } else if (m.getAnnotation(ResumesWorkflow.class) != null) {
                    initiatesWorkflow = false;
                    completesWorkflow = false;
                    workflowName = m.getAnnotation(ResumesWorkflow.class).value();
                }
            }
        }
        
        if (handlerMethod == null) {
            throw new IllegalStateException(String.format("Unable to find handler method for %s", identifier.getUri()));
        }
    }

    @Override
    public URIPattern getPattern() {
        return pattern;
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public Class<?> getHandlerClass() {
        return handlerClass;
    }

    @Override
    public Class<?> getFormClass() {
        return formClass;
    }

    @Override
    public Method getHandlerMethod() {
        return handlerMethod;
    }

    @Override
    public boolean initiatesWorkflow() {
        return initiatesWorkflow;
    }

    @Override
    public boolean completesWorkflow() {
        return completesWorkflow;
    }

    @Override
    public String getWorkflowName() {
        return workflowName;
    }

    @Override
    public int compareTo(HandlerMethodDescriptor that) {
        int result = pattern.compareTo(that.getPattern());
        if (result == 0) {
            result = method.ordinal() - that.getMethod().ordinal();
        }
        return result;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (!(that instanceof HandlerMethodDescriptor)) {
            return false;
        }
        HandlerMethodDescriptor desc = (HandlerMethodDescriptor) that;
        boolean equal = pattern.equals(desc.getPattern()) && method == desc.getMethod();
        
        return equal;
    }

    @Override
    public int hashCode() {
        return pattern.hashCode() + method.name().hashCode();
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return request != null && method.matches(request)
                && pattern.matches(request);
    }
    
    @Override
    public String toString() {
        return "HandlerMethodDescriptorImpl [pattern=" + pattern + ", method=" + method + "]";
    }

    @Override
    public List<ParamDescriptor> getParamDescriptors() {
        return paramDescriptors;
    }

    public boolean isCompletesWorkflow() {
        return completesWorkflow;
    }

    public boolean isInitiatesWorkflow() {
        return initiatesWorkflow;
    }
    
    public static class ParamDescriptor {
        
        private Class<?> type;
        private String name;
        private Class<? extends StringParamConverter<?>> converter;
        
        public ParamDescriptor(Class<?> type, String name) {
            this.type = type;
            this.name = name;
        }

        public Class<? extends StringParamConverter<?>> getConverter() {
            return converter;
        }

        public void setConverter(Class<? extends StringParamConverter<?>> converter) {
            this.converter = converter;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Class<?> getType() {
            return type;
        }

        public void setType(Class<?> type) {
            this.type = type;
        }
        
        private static ParamDescriptor createIfApplicable(Class<?> type, Annotation[] annotations) 
                throws InvalidParamException {
            
            ParamDescriptor descriptor = null;
            
            for (int i = 0; i < annotations.length; i++) {
                Class<?> annotationType = annotations[i].annotationType();
                
                if (annotationType.isAssignableFrom(Param.class)) {
                    Param param = (Param) annotations[i];
                    String name = param.name();
                    
                    if (name == null || "".equals(name)) {
                        name = param.value();
                    } 
                    
                    if (name != null) {
                        descriptor = new ParamDescriptor(type, name);
                        
                        if (!param.converter().equals(PassThroughParamConverter.class)) {
                            descriptor.setConverter(param.converter());
                        }
                    }
                    
                    if (name == null) {
                        
                    }
                }
            }
            
            return descriptor;
        }
        
    }
    
}
