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
package agave.samples.pastebin.snippet;

import java.io.Serializable;
import java.util.Date;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class Snippet implements Serializable, Cloneable {

    private static final long serialVersionUID = 4L;
    
    private String uniqueId;
    private long revision;
    private Timeframe expiration;
    private String owner;
    private Date created;
    private String syntaxLanguage;
    private String contents;
    private boolean privateSnippet;
    
    public Snippet() {
        setRevision(0l);
        setCreated(new Date());
        setSyntaxLanguage("");
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
    
    public long getRevision() {
        return revision;
    }

    public void setRevision(long revision) {
        this.revision = revision;
    }

    public Timeframe getExpiration() {
        return expiration;
    }

    public void setExpiration(Timeframe expiration) {
        this.expiration = expiration;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getSyntaxLanguage() {
        return syntaxLanguage;
    }

    public void setSyntaxLanguage(String syntaxLanguage) {
        this.syntaxLanguage = syntaxLanguage;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public boolean isPrivateSnippet() {
        return privateSnippet;
    }

    public void setPrivateSnippet(boolean privateSnippet) {
        this.privateSnippet = privateSnippet;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Snippet clone = new Snippet();
        clone.setUniqueId(new String(getUniqueId()));
        clone.setRevision(getRevision());
        clone.setExpiration(getExpiration());
        clone.setOwner(new String(getOwner()));
        clone.setCreated((Date)getCreated().clone());
        clone.setSyntaxLanguage(new String(getSyntaxLanguage()));
        clone.setContents(new String(getContents()));
        clone.setPrivateSnippet(new Boolean(isPrivateSnippet()));
        return clone;
    }

}
