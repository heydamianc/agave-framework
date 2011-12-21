/*
 * Copyright (c) 2011, Damian Carrillo
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import co.cdev.agave.Part;

/**
 * A file backed multipart parser. All parts are saved to individual temporary files that are 
 * deleted when the JVM exits. 
 * 
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 *
 */
public class FileMultipartParser extends AbstractMultipartParser<File> {

    private static final String DEFAULT_SUFFIX = ".tmp";
    private static final Pattern FILENAME_PATTERN = Pattern.compile("(.*)\\.(.*)");
    
    protected boolean readPart(HttpServletRequest request, Part<File> part) throws IOException {
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
        
        temporaryFile = null;
        
        return end;
    }

}
