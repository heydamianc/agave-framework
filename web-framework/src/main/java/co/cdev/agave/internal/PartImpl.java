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

import java.io.File;
import java.util.Map;
import java.util.HashMap;

import co.cdev.agave.Part;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class PartImpl implements Part {

    private String name;
    private String filename;
    private String contentType; 
    private File contents;
    private String parameterValue;
    private Map<String, String> otherHeaders = new HashMap<String, String>();

    public PartImpl() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public File getContents() {
        return contents;
    }

    @Override
    public void setContents(File contents) {
        this.contents = contents;
    }

    @Override
    public String getParameterValue() {
        return parameterValue;
    }

    @Override
    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }

    @Override
    public void addHeader(String headerName, String headerValue) {
        otherHeaders.put(headerName, headerValue);
    }

    @Override
    public String getHeader(String headerName) {
        return otherHeaders.get(headerName);
    }

    @Override
    public String toString() {
        return new StringBuilder("Part[")
            .append("name:").append(getName()).append(", ")
            .append("filename:").append(getFilename()).append(", ")
            .append("contentType:").append(getContentType())
            .append("]")
            .toString();
    }

}
