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

import java.util.ArrayList;
import java.util.List;

import co.cdev.agave.HttpMethod;

/**
 * Used internally when scanning for handlers.  A HandlerIdentifier is pieced together piece by piece
 * until the handler method is identified with a valid URI pattern.  The {@code uri} and 
 * {@code method} properties come from the the {@code @HandlesRequestTo} annotation, whereas the
 * {@code className} and {@code methodName} property come from the handler method that has been
 * identified.
 */
public final class ScanResultImpl implements ScanResult {

    private String uri;
    private HttpMethod method;
    private String className;
    private String methodName;
    private List<String> parameterClassNames;

    public ScanResultImpl() {
    	this(null, null, null);
    }

    public ScanResultImpl(String uri, String className, String methodName) {
        this(uri, HttpMethod.ANY, className, methodName);
    }
    
    public ScanResultImpl(String uri, HttpMethod method, String className, String methodName) {
        this(uri, method, className, methodName, new ArrayList<String>());
    }
    
    public ScanResultImpl(String uri, HttpMethod method, String className, String methodName, List<String> parameterClassNames) {
        this.uri = uri;
        this.method = method;
        this.className = className;
        this.methodName = methodName;
        this.parameterClassNames = parameterClassNames;
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
    public List<String> getParameterClassNames() {
        return parameterClassNames;
    }

    @Override
    public void setParameterClassNames(List<String> parameterClassNames) {
        this.parameterClassNames = parameterClassNames;
    }

}
