/**
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
package co.cdev.agave.samples.pastebin.web;

import co.cdev.agave.Converter;
import co.cdev.agave.conversion.BooleanConverter;
import co.cdev.agave.conversion.LongConverter;
import co.cdev.agave.samples.pastebin.snippet.Snippet;
import co.cdev.agave.samples.pastebin.snippet.Timeframe;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class PastebinForm {

    private String uniqueId;
    private Long revision;
    private String contents;
    private Timeframe expiration;
    private String owner;
    private Boolean privateSnippet = Boolean.FALSE;
    private String syntaxLanguage;

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Long getRevision() {
        return revision;
    }

    public void setRevision(@Converter(LongConverter.class) Long revision) {
        this.revision = revision;
    }

    public Timeframe getExpiration() {
        return expiration;
    }
    
    public void setExpiration(@Converter(TimeframeParamConverter.class) Timeframe expiration) {
        this.expiration = expiration;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Boolean getPrivateSnippet() {
        return privateSnippet;
    }

    public void setPrivateSnippet(@Converter(BooleanConverter.class) Boolean privateSnippet) {
        this.privateSnippet = privateSnippet;
    }

    public String getSyntaxLanguage() {
        return syntaxLanguage;
    }

    public void setSyntaxLanguage(String syntaxLanguage) {
        this.syntaxLanguage = syntaxLanguage;
    }

    public void copyValuesTo(Snippet snippet) {
        snippet.setContents(contents);
        snippet.setUniqueId(uniqueId);
        snippet.setOwner(owner);
        snippet.setSyntaxLanguage(syntaxLanguage);
        snippet.setPrivateSnippet(privateSnippet);
        snippet.setExpiration(expiration);
    }

}
