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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import co.cdev.agave.HttpMethod;

/**
 * Used internally when scanning for handlers.  A HandlerIdentifier is pieced together piece by piece
 * until the handler method is identified with a valid URI pattern.  The {@code uri} and 
 * {@code method} properties come from the the {@code @HandlesRequestTo} annotation, whereas the
 * {@code className} and {@code methodName} property come from the handler method that has been
 * identified.
 */
public final class HandlerIdentifierImpl implements HandlerIdentifier {

    private String uri;
    private HttpMethod method;
    private String className;
    private String methodName;
    private Collection<Class<?>> argumentTypes;

    public HandlerIdentifierImpl() {
    	this(null, null, null);
    }

    public HandlerIdentifierImpl(String uri, String className, String methodName) {
        this(uri, HttpMethod.ANY, className, methodName);
    }
    
    public HandlerIdentifierImpl(String uri, HttpMethod method, String className, String methodName) {
        this(uri, method, className, methodName, new ArrayList<Class<?>>());
    }
    
    public HandlerIdentifierImpl(String uri, HttpMethod method, String className, String methodName, Collection<Class<?>> argumentTypes) {
    	setUri(uri);
    	setMethod(method);
        setClassName(className);
        setMethodName(methodName);
        setArgumentTypes(argumentTypes);
    }

    @Override
    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public HttpMethod getMethod() {
		return method;
	}

    @Override
	public void setMethod(HttpMethod method) {
		this.method = method;
	}

    @Override
	public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public Collection<Class<?>> getParamTypes() {
        return argumentTypes;
    }

    @Override
    public void setArgumentTypes(Collection<Class<?>> paramTypes) {
        this.argumentTypes = paramTypes;
    }
    
    @Override
    public boolean matches(Method method) {
        boolean matches = method.getName().equals(this.getMethodName());
        
        Map<Class<?>, Boolean> arguments = new HashMap<Class<?>, Boolean>();
        
        if (argumentTypes != null) {
            for (Class<?> argumentType : argumentTypes) {
                arguments.put(argumentType, Boolean.FALSE);
            }
        }
        
        Class<?>[] parameterTypes = method.getParameterTypes();
        
        if (parameterTypes.length > 1) {
            for (int i = 1; i < parameterTypes.length; i++) {
                arguments.put(parameterTypes[i], arguments.containsKey(parameterTypes[i]));
            }
        }
        
        for (Boolean expected : arguments.values()) {
            matches &= expected;
        }
        
        return matches;
    }

}
