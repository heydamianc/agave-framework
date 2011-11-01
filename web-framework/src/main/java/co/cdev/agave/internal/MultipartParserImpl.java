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
import java.io.FileOutputStream;
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
public class MultipartParserImpl implements MultipartParser {

    private static class CoupledLine {
        private StringBuilder characters = new StringBuilder();
        private List<Byte> bytes = new LinkedList<Byte>();

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
    private Map<String, Part> parts;
    private String boundary;
    private String eos;
    private InputStream in;
    
    public MultipartParserImpl(HttpServletRequest request) throws IOException {
        parameters = new HashMap<String, Collection<String>>();
        parts = new HashMap<String, Part>();
        Matcher boundaryMatcher = BOUNDARY_PATTERN.matcher(request.getContentType());
        if (boundaryMatcher.matches() && boundaryMatcher.groupCount() >= 1) {
            boundary = "--" + boundaryMatcher.group(1);
        }
        eos = boundary + "--";
        in = request.getInputStream();
        readLine(in);
    }
    
    public Map<String, Collection<String>> getParameters() {
        return parameters;
    }

    public Map<String, Part> getParts() {
        return parts;
    }
    
    public String getBoundary() {
        return boundary;
    }

    public void parseInput() throws IOException {
        try {
            while (true) {
                Part part = new PartImpl();
                readHeaders(part);
                if (part.getFilename() != null) {
                    if (readPart(part)) {
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
    
    public void readHeaders(Part part) throws IOException {
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
    
    private CoupledLine readCoupledLine(InputStream in) throws IOException {
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
    
    public boolean readParameter(Part part) throws IOException {
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
    
    public boolean readPart(Part part) throws IOException {
        boolean end = false;

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
        
        File temporaryFile = File.createTempFile(prefix, suffix);
        temporaryFile.deleteOnExit();
        
        FileOutputStream out = new FileOutputStream(temporaryFile);
        
        CoupledLine line = null;
        while ((line = readCoupledLine(in)) != null) {
            String text = line.characters.toString().trim();
            
            if (eos.equals(text)) {
                end = true;
                break;
            }
            
            if (boundary.equals(text)) {
                break;
            }
            
            for (Byte b : line.bytes) {
                out.write(b);
            }
        }
        
    	out.flush();
        out.close();
        part.setContents(temporaryFile);
        parts.put(part.getName(), part);
        return end;
    }

    protected void finalize() throws Exception {
        if (in != null) {
            in.close();
        }
    }
}
