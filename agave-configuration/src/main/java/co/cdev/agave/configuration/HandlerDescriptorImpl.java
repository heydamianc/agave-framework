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
package co.cdev.agave.configuration;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import co.cdev.agave.HttpMethod;
import co.cdev.agave.URIPattern;

/**
 * A descriptor that serves as the configuration for the building of handlers
 * and routing requests into them.
 * 
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class HandlerDescriptorImpl implements HandlerDescriptor {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(HandlerDescriptorImpl.class.getName());
    
    private final Class<?>              handlerClass;
    private final Method                handlerMethod;
    private final URIPattern            uriPattern;
    private final HttpMethod            httpMethod;
    private final boolean               initiatesWorkflow;
    private final boolean               completesWorkflow;
    private final String                workflowName;
    private final Class<?>              formClass;
    private final List<ParamDescriptor> paramDescriptors;
    
    public HandlerDescriptorImpl(Class<?>              handlerClass,
                                 Method                handlerMethod,
                                 URIPattern            uriPattern,
                                 HttpMethod            httpMethod,
                                 boolean               initiatesWorkflow,
                                 boolean               completesWorkflow,
                                 String                workflowName,
                                 Class<?>              formClass,
                                 List<ParamDescriptor> paramDescriptors) {
        this.handlerClass = handlerClass;
        this.handlerMethod = handlerMethod;
        this.uriPattern = uriPattern;
        this.httpMethod = httpMethod;
        this.initiatesWorkflow = initiatesWorkflow;
        this.completesWorkflow = completesWorkflow;
        this.workflowName = workflowName;
        this.formClass = formClass;
        this.paramDescriptors = paramDescriptors;
    }

    public URIPattern getURIPattern() {
        return uriPattern;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public Class<?> getHandlerClass() {
        return handlerClass;
    }

    public Method getHandlerMethod() {
        return handlerMethod;
    }

    public Class<?> getFormClass() {
        return formClass;
    }

    public List<ParamDescriptor> getParamDescriptors() {
        return Collections.unmodifiableList(paramDescriptors);
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

    @Override
    public int compareTo(HandlerDescriptor that) {
        int result = uriPattern.compareTo(that.getURIPattern());
        
        if (result == 0) {
            result = httpMethod.ordinal() - that.getHttpMethod().ordinal();
        }
        
        if (result == 0) {
            result = -(paramDescriptors.size() - that.getParamDescriptors().size());
        }
        
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((handlerClass == null) ? 0 : handlerClass.getCanonicalName().hashCode());
        result = prime * result + ((handlerMethod == null) ? 0 : handlerMethod.hashCode());
        result = prime * result + ((httpMethod == null) ? 0 : httpMethod.hashCode());
        result = prime * result + ((paramDescriptors == null) ? 0 : paramDescriptors.hashCode());
        result = prime * result + ((uriPattern == null) ? 0 : uriPattern.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        HandlerDescriptorImpl other = (HandlerDescriptorImpl) obj;
        if (handlerClass == null) {
            if (other.handlerClass != null)
                return false;
        } else if (!handlerClass.getCanonicalName().equals(other.handlerClass.getCanonicalName()))
            return false;
        if (handlerMethod == null) {
            if (other.handlerMethod != null)
                return false;
        } else if (!handlerMethod.equals(other.handlerMethod))
            return false;
        if (httpMethod != other.httpMethod)
            return false;
        if (paramDescriptors == null) {
            if (other.paramDescriptors != null)
                return false;
        } else if (!paramDescriptors.equals(other.paramDescriptors))
            return false;
        if (uriPattern == null) {
            if (other.uriPattern != null)
                return false;
        } else if (!uriPattern.equals(other.uriPattern))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "HandlerDescriptorImpl [handlerClass=" + handlerClass + ", handlerMethod=" + handlerMethod
                + ", uriPattern=" + uriPattern + ", httpMethod=" + httpMethod + ", initiatesWorkflow="
                + initiatesWorkflow + ", completesWorkflow=" + completesWorkflow + ", workflowName=" + workflowName
                + ", formClass=" + formClass + ", paramDescriptors=" + paramDescriptors + "]";
    }
    
    // Serialization
    
    private Object writeReplace() {
        return new SerializationProxy(this);
    }
    
    private void readObject(ObjectInputStream in) throws InvalidObjectException {
        throw new InvalidObjectException("Expected SerializationProxy");
    }
    
    private static class SerializationProxy implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        private final Class<?>              handlerClass;
        private final String                handlerMethodName;
        private final Class<?>[]            handlerMethodParameterClasses;
        private final URIPattern            uriPattern;
        private final HttpMethod            httpMethod;
        private final boolean               initiatesWorkflow;
        private final boolean               completesWorkflow;
        private final String                workflowName;
        private final Class<?>              formClass;
        private final List<ParamDescriptor> paramDescriptors;
        
        SerializationProxy(HandlerDescriptorImpl handlerDescriptor) {
            handlerClass = handlerDescriptor.getHandlerClass();
            handlerMethodName = handlerDescriptor.getHandlerMethod().getName();
            handlerMethodParameterClasses = handlerDescriptor.getHandlerMethod().getParameterTypes();
            uriPattern = handlerDescriptor.getURIPattern();
            httpMethod = handlerDescriptor.getHttpMethod();
            initiatesWorkflow = handlerDescriptor.initiatesWorkflow();
            completesWorkflow = handlerDescriptor.completesWorkflow();
            workflowName = handlerDescriptor.getWorkflowName();
            formClass = handlerDescriptor.getFormClass();
            paramDescriptors = handlerDescriptor.getParamDescriptors();
        }
        
        private Object readResolve() {
            Method handlerMethod = null;
            
            try {
                handlerMethod = handlerClass.getMethod(handlerMethodName, handlerMethodParameterClasses);
            } catch (Exception e) {
                LOGGER.severe("Unable to find handler method named " + handlerMethodName);
            }
            
            HandlerDescriptor handlerDescriptor = new HandlerDescriptorImpl(handlerClass,
                                                                            handlerMethod,
                                                                            uriPattern,
                                                                            httpMethod,
                                                                            initiatesWorkflow,
                                                                            completesWorkflow,
                                                                            workflowName,
                                                                            formClass,
                                                                            paramDescriptors);
            
            return handlerDescriptor;
        }
    }

}
