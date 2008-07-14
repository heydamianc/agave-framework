/*
 * Copyright (c) 2006 - 2007 Damian Carrillo.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *	* Redistributions of source code must retain the above copyright notice,
 *	  this list of conditions and the following disclaimer.
 *	* Redistributions in binary form must reproduce the above copyright notice,
 *	  this list of conditions and the following disclaimer in the documentation
 *	  and/or other materials provided with the distribution.
 *	* Neither the name of the <ORGANIZATION> nor the names of its contributors
 *	  may be used to endorse or promote products derived from this software
 *	  without specific prior written permission.
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
public class XHTMLTemplateGroup extends SuffixedStringTemplateGroup {

	private static final String XHTML_EXT = ".xhtml";
	
	public XHTMLTemplateGroup(String name, String rootDir) {
		super(name, rootDir, XHTML_EXT);
	}
	
	public XHTMLTemplateGroup(String name, String rootDir, Class lexer) {
		super(name, rootDir, lexer, XHTML_EXT);
	}
	
	
	public XHTMLTemplateGroup(String name) {
		super(name, XHTML_EXT);
	}
	
	public XHTMLTemplateGroup(String name, Class lexer) {
		super(name, lexer, XHTML_EXT);
	}
	
	public XHTMLTemplateGroup(Reader r) {
		super(r, XHTML_EXT);
	}
	
	public XHTMLTemplateGroup(Reader r, StringTemplateErrorListener errors) {
		super(r, errors, XHTML_EXT);
	}
	
	public XHTMLTemplateGroup(Reader r, Class lexer) {
		super(r, lexer, XHTML_EXT);
	}
	
	public XHTMLTemplateGroup(Reader r, Class lexer, StringTemplateErrorListener errors) {
		super(r, lexer, errors, XHTML_EXT);
	}
	
	public XHTMLTemplateGroup(
			Reader r,
			Class lexer,
			StringTemplateErrorListener errors,
			StringTemplateGroup superGroup,
			String suffix) {
		super(r, lexer, errors, superGroup, XHTML_EXT);
	}
	
}
