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
package agave.samples.pastebin.web;

import org.apache.commons.lang.StringEscapeUtils;

import agave.samples.pastebin.overview.Overview;
import agave.samples.pastebin.overview.RecentEntry;
import agave.samples.pastebin.snippet.Snippet;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class PastebinViewModel {

    private String contextPath;
    private Snippet snippet;
    private Overview overview;

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public Snippet getSnippet() {
        return snippet;
    }

    public void setSnippet(Snippet snippet) throws CloneNotSupportedException {
        Snippet clone = (Snippet) snippet.clone();
        clone.setContents(StringEscapeUtils.escapeHtml(clone.getContents()));
        clone.setOwner(StringEscapeUtils.escapeHtml(clone.getOwner()));
        this.snippet = clone;
    }

    public Overview getOverview() {
        return overview;
    }
    
    public void setOverview(Overview overview) throws CloneNotSupportedException {
        Overview clone = (Overview)overview.clone();
        for (RecentEntry recentEntry : clone.getRecentEntries()) {
            recentEntry.setUniqueId(StringEscapeUtils.escapeHtml(recentEntry.getUniqueId()));
            recentEntry.setOwner(StringEscapeUtils.escapeHtml(recentEntry.getOwner()));
        }
        this.overview = clone;
    }
    
}
