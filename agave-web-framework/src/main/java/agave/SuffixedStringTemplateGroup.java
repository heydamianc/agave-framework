/*
 * Copyright (c) 2006 - 2007 Damian Carrillo.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  o Neither the name of the <ORGANIZATION> nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package agave;

import java.io.Reader;
import org.antlr.stringtemplate.StringTemplateErrorListener;
import org.antlr.stringtemplate.StringTemplateGroup;

/**
 *
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class SuffixedStringTemplateGroup extends StringTemplateGroup {
    
    protected String suffix;
    
    public SuffixedStringTemplateGroup(String name, String rootDir, String suffix) {
        super(name, rootDir);
        this.suffix = suffix;
    }
    
    public SuffixedStringTemplateGroup(String name, String rootDir, Class lexer, String suffix) {
        super(name, rootDir, lexer);
        this.suffix = suffix;
    }
    
    
    public SuffixedStringTemplateGroup(String name, String suffix) {
        super(name);
        this.suffix = suffix;
    }
    
    public SuffixedStringTemplateGroup(String name, Class lexer, String suffix) {
        super(name, lexer);
        this.suffix = suffix;
    }
    
    public SuffixedStringTemplateGroup(Reader r, String suffix) {
        super(r);
        this.suffix = suffix;
    }
    
    public SuffixedStringTemplateGroup(Reader r, StringTemplateErrorListener errors, String suffix) {
        super(r, errors);
        this.suffix = suffix;
    }
    
    public SuffixedStringTemplateGroup(Reader r, Class lexer, String suffix) {
        super(r, lexer);
        this.suffix = suffix;
    }
    
    public SuffixedStringTemplateGroup(Reader r, Class lexer, StringTemplateErrorListener errors, String suffix) {
        super(r, lexer, errors);
        this.suffix = suffix;
    }
    
    public SuffixedStringTemplateGroup(
            Reader r,
            Class lexer,
            StringTemplateErrorListener errors,
            StringTemplateGroup superGroup,
            String suffix) {
        super(r, lexer, errors, superGroup);
        this.suffix = suffix;
    }
    
    public String getSuffix() {
        return suffix;
    }
    
    /** 
     * (public so that people can override behavior; not a general
     *  purpose method)
     * @param templateName 
     */
    public String getFileNameFromTemplateName(String templateName) {
        return templateName + getSuffix();
    }
    
    /** Convert a filename relativePath/name.st to relativePath/name.
     *  (public so that people can override behavior; not a general
     *  purpose method)
     */
    public String getTemplateNameFromFileName(String fileName) {
        String name = fileName;
        int suffix = name.lastIndexOf(getSuffix());
        if ( suffix>=0 ) {
            name = name.substring(0, suffix);
        }
        return name;
    }
    
}
