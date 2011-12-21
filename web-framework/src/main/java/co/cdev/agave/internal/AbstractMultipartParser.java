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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import co.cdev.agave.Part;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public abstract class AbstractMultipartParser<T> implements MultipartParser<T> {

    protected static class CoupledLine {
        public StringBuilder characters = new StringBuilder();
        public List<Byte> bytes = new LinkedList<Byte>();

        public void append(int i) {
            characters.append((char)i);
            bytes.add((byte)i);
        }
    }
    
    private static final String DEFAULT_SUFFIX = ".tmp";
    private static final Pattern BOUNDARY_PATTERN = Pattern.compile("multipart/form-data;\\s*boundary=(.*)");
    private static final String CONTENT_DISPOSIITON = "Content-Disposition:\\s*form-data;\\s*name=\"(.*)\"";
    private static final Pattern PART_PATTERN = Pattern.compile(CONTENT_DISPOSIITON + ";\\s*filename=\"(.+)\"");
    private static final Pattern FILENAME_PATTERN = Pattern.compile("(.*)\\.(.*)");
    private static final Pattern PARAMETER_PATTERN = Pattern.compile(CONTENT_DISPOSIITON);
    private static final Pattern CONTENT_TYPE_PATTERN = Pattern.compile("Content-Type:\\s*(.+)");
    private static final Pattern OTHER_HEADER_PATTERN = Pattern.compile("(\\S+):\\s*(.+?)");
    
    private Map<String, Collection<String>> parameters;
    protected Map<String, Part<T>> parts;
    protected String boundary;
    protected String eos;
    protected InputStream in;
    
    public void prepare(HttpServletRequest request) throws IOException {
        parameters = new HashMap<String, Collection<String>>();
        parts = new HashMap<String, Part<T>>();
        Matcher boundaryMatcher = BOUNDARY_PATTERN.matcher(request.getContentType());
        if (boundaryMatcher.matches() && boundaryMatcher.groupCount() >= 1) {
            boundary = "--" + boundaryMatcher.group(1);
        }
        eos = boundary + "--";
        in = request.getInputStream();
        readLine(in);
    }
    
    @Override
    public Map<String, Collection<String>> getParameters() {
        return parameters;
    }

    @Override
    public Map<String, Part<T>> getParts() {
        return parts;
    }
    
    public String getBoundary() {
        return boundary;
    }

    @Override
    public void parseInput() throws Exception {
        if (in == null) {
            throw new IllegalStateException("prepare(HttpServletRequest) must be called prior to parseInput()");
        }
        
        try {
            while (true) {
                Part<T> part = new DefaultPart<T>();
                readHeaders(part);
                if (part.getFilename() != null) {
                    String prefix = null;
                    String suffix = null;
                    
                    Matcher matcher = FILENAME_PATTERN.matcher(part.getFilename());
                    if (matcher.matches() && matcher.groupCount() >= 2) {
                        prefix = matcher.group(1);
                        suffix = (matcher.group(2).startsWith(".")) ? matcher.group(2) : '.' + matcher.group(2);
                    } else {
                        prefix = part.getName();
                        suffix = DEFAULT_SUFFIX;
                    }
                    
                    if (readPart(prefix, suffix, part)) {
                        break;
                    }
                } else {
                    if (readParameter(part)) {
                        break;
                    }
                }
            }
        } finally {
            in.close();
            in = null;
        }
    }
    
    private void readHeaders(Part<T> part) throws IOException {
        String line = null;
        while ((line = readLine(in)) != null) {
            line = line.trim();
            
            if ("".equals(line)) {
                break;
            }
            
            Matcher matcher = PART_PATTERN.matcher(line);
            if (matcher.matches() && matcher.groupCount() >= 2) {
                part.setName(matcher.group(1));
                part.setFilename(matcher.group(2));
                continue;
            }
            
            matcher = PARAMETER_PATTERN.matcher(line);
            if (matcher.matches() && matcher.groupCount() >= 1) {
                part.setName(matcher.group(1));
                continue;
            }
            
            matcher = CONTENT_TYPE_PATTERN.matcher(line);
            if (matcher.matches() && matcher.groupCount() >= 1) {
                part.setContentType(matcher.group(1));
                continue;
            }
            
            matcher = OTHER_HEADER_PATTERN.matcher(line);
            if (matcher.matches() && matcher.groupCount() >= 2) {
                part.addHeader(matcher.group(1), matcher.group(2));
                continue;
            }
        }
    }
    
    private String readLine(InputStream in) throws IOException {
        StringBuilder text = new StringBuilder();
        
        int i = -1;
        while ((i = in.read()) != -1) {
            text.append((char) i);
            
            if ((char) i == '\n') {
                break;
            }
        }
        
        return text.toString();
    }
    
    protected CoupledLine readCoupledLine(InputStream in) throws IOException {
        CoupledLine line = new CoupledLine();
        
        int i = -1;
        while ((i = in.read()) != -1) { 
            line.append(i);
            
            if ((char) i == '\n') {
                break;
            }
        }
        
        return line;
    }
    
    private boolean readParameter(Part<T> part) throws IOException {
        boolean end = false;

        StringBuilder parameterValue = new StringBuilder();
        String line = null;
        
        while ((line = readLine(in)) != null) {
            line = line.trim();
            
            if (eos.equals(line)) {
                end = true;
                break;
            }
            
            if (boundary.equals(line)) {
                break;
            }
            
            parameterValue.append(line);
        }
        
        if (!parameters.containsKey(part.getName())) {
            parameters.put(part.getName(), new ArrayList<String>());
        }
        parameters.get(part.getName()).add(parameterValue.toString());
        
        return end;
    }
    
    protected abstract boolean readPart(String prefix, String suffix, Part<T> part) throws Exception;
    
    /**
     * Just making sure that this is closed... (see the finally block of the parseInput method as well)
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (in != null) {
            in.close();
        }
    }
}
