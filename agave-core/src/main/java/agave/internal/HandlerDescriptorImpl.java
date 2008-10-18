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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import agave.BindsInput;
import agave.BindsRequest;
import agave.BindsResponse;
import agave.BindsServletContext;
import agave.ConvertWith;
import agave.conversion.Converter;

/**
 * A descriptor that serves as the configuration for the building of handlers and routing requests 
 * into them.
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class HandlerDescriptorImpl implements HandlerDescriptor {
    
    private URIPattern pattern;
    private Class<?> handlerClass;
    private Class<?> formClass;
    private Method handlerMethod;
    private Method requestSetter;
    private Method responseSetter;
    private Method servletContextSetter;
    private Map<String, Method> mutators = new HashMap<String, Method>();
    private Map<String, Class<? extends Converter<?,?>>> converters = 
        new HashMap<String, Class<? extends Converter<?,?>>>();

    public HandlerDescriptorImpl(HandlerIdentifier identifier) throws ClassNotFoundException {
        this.pattern = new URIPatternImpl(identifier.getUri());
        this.handlerClass = Class.forName(identifier.getClassName());

        locateAnnotatedHandlerMethods(identifier);

        if (this.formClass != null) {
            locateAnnotatedFormMethods(identifier);
        }
    }

    /**
     * Locates annotated methods on a handler class.  This method will find the annotated method
     * that is identified by the supplied {@code HandlerIdentifier} and then proceed to find the 
     * request and response binders. The specific annotations this method targets are {@code BindsRequest} 
     * and {@code BindsResponse}.
     * @param identifier the {@code HandlerIdentifier} that was created while scanning for a handler method
     */
    public void locateAnnotatedHandlerMethods(HandlerIdentifier identifier) {
        for (Method method : this.handlerClass.getMethods()) {
            if (identifier.getMethodName().equals(method.getName())) {
                this.handlerMethod = method;
                for (Class<?> parameterType : method.getParameterTypes()) {
                    this.formClass = parameterType;
                }
            } else if (method.getAnnotation(BindsRequest.class) != null) {
                this.requestSetter = method;    
            } else if (method.getAnnotation(BindsResponse.class) != null) {
                this.responseSetter = method;
            } else if (method.getAnnotation(BindsServletContext.class) != null) {
                this.servletContextSetter = method;
            }
        }
    }

    /**
     * Locates annotated methods on a form class.  This method will find the annotated methods on a form 
     * class to be used when binding parameters.  Once an annotated method is identified, this method
     * will check if the parameter needs a converter.  The annotations this method targets are 
     * {@code BindsParameter} and {@code ConvertWith}.
     * 
     * @param identifer the {@code HandlerIdentifier} that was created while scanning for a handler method
     */
    public void locateAnnotatedFormMethods(HandlerIdentifier identifier) {
        for (Method method : this.formClass.getMethods()) {
            BindsInput annotation = method.getAnnotation(BindsInput.class);
            if (annotation != null) {
                String paramName = null;
                if (annotation.name() != null && "".equals(annotation.name().trim())) {
                    paramName = method.getName();
                    if (paramName.startsWith("set")) {
                        paramName = paramName.substring(3, 4).toLowerCase() + paramName.substring(4);
                    }
                } else {
                    paramName = annotation.name();
                }
                mutators.put(paramName, method);
                for (Annotation paramAnnotation : method.getParameterAnnotations()[0]) {
                    if (paramAnnotation instanceof ConvertWith) {
                        converters.put(paramName, ((ConvertWith)paramAnnotation).value());        
                    }
                }
            }
        }
    }
    
    public URIPattern getPattern() {
        return pattern;
    }
    
    public Class<?> getHandlerClass() {
        return handlerClass;
    }

    public Class<?> getFormClass() {
        return formClass;
    }
    
    public Method getHandlerMethod() {
        return handlerMethod;
    }

    public Method getRequestSetter() {
        return requestSetter;
    }

    public Method getResponseSetter() {
        return responseSetter;
    }
    
    public Method getServletContextSetter() {
        return servletContextSetter;
    }

    public Map<String, Method> getMutators() {
        return mutators;
    }

    public Map<String, Class<? extends Converter<?,?>>> getConverters() {
        return converters;
    }

    public int compareTo(HandlerDescriptor that) {
        return getPattern().compareTo(that.getPattern());
    }
    
    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (!(that instanceof HandlerDescriptor)) {
            return false;
        }
        HandlerDescriptor desc = (HandlerDescriptor)that;
        return this.getPattern().equals(desc.getPattern());
    }

    @Override
    public int hashCode() {
        return pattern.hashCode() + handlerClass.hashCode() + handlerMethod.hashCode();
    }

    public boolean matches(String uri) {
        return pattern.matches(uri);
    }

}
