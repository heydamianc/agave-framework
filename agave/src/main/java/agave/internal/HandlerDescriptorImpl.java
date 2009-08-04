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
package agave.internal;

import java.lang.reflect.Method;

import agave.CompletesWorkflow;
import agave.InitiatesWorkflow;
import agave.ResumesWorkflow;

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
    private boolean initiatesWorkflow;
    private boolean completesWorkflow;
    private String workflowName;

    public HandlerDescriptorImpl(HandlerIdentifier identifier) throws ClassNotFoundException {
        pattern = new URIPatternImpl(identifier.getUri());
        handlerClass = Class.forName(identifier.getClassName());
        locateAnnotatedHandlerMethods(identifier);
    }

    /**
     * Locates annotated methods on a handler class.  This method will find the annotated method
     * that is identified by the supplied {@code HandlerIdentifier} and then proceed to find any 
     * workflow-related annotations ({@code InitiatesWorkflow}, {@code ResumesWorkflow},
     * {@code CompletesWorkflow}.
     * @param identifier the {@code HandlerIdentifier} that was created while scanning for a handler method
     */
    public void locateAnnotatedHandlerMethods(HandlerIdentifier identifier) {
        for (Method method : handlerClass.getMethods()) {
            if (identifier.getMethodName().equals(method.getName())) {
                handlerMethod = method;
                if (handlerMethod.getParameterTypes().length > 1) {
                    formClass = handlerMethod.getParameterTypes()[1];
                }
                
                if (method.getAnnotation(InitiatesWorkflow.class) != null) {
                    initiatesWorkflow = true;
                    completesWorkflow = false;
                    workflowName = method.getAnnotation(InitiatesWorkflow.class).value();
                } else if (method.getAnnotation(CompletesWorkflow.class) != null) {
                    initiatesWorkflow = false;
                    completesWorkflow = true;
                    workflowName = method.getAnnotation(CompletesWorkflow.class).value();
                } else if (method.getAnnotation(ResumesWorkflow.class) != null) {
                    initiatesWorkflow = false;
                    completesWorkflow = false;
                    workflowName = method.getAnnotation(ResumesWorkflow.class).value();
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

    public boolean initiatesWorkflow() {
        return initiatesWorkflow;
    }

    public boolean completesWorkflow() {
        return completesWorkflow;
    }

    public String getWorkflowName() {
        return workflowName;
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
