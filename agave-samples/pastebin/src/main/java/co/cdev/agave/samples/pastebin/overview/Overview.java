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
package co.cdev.agave.samples.pastebin.overview;

import co.cdev.agave.samples.pastebin.snippet.Snippet;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class Overview implements Serializable {

    public static final int MAX_ENTRIES = 10;

    public static final long serialVersionUID = 1l;
    
    public final LinkedList<RecentEntry> recentEntries;
    public final LinkedList<RecentEntry> olderEntries;

    public Overview() {
        recentEntries = new LinkedList<RecentEntry>();
        olderEntries = new LinkedList<RecentEntry>();
    }

    public Overview(final Overview overview) {
        this();
        if (overview != null) {
            for (RecentEntry recentEntry : recentEntries) {
                recentEntries.add(new RecentEntry(recentEntry));
            }
            for (RecentEntry olderEntry : olderEntries) {
                olderEntries.add(new RecentEntry(olderEntry));
            }
        }
    }

    public List<RecentEntry> getRecentEntries() {
        return Collections.unmodifiableList(recentEntries);
    }

    public void setRecentEntries(final List<RecentEntry> recentEntries) {
        this.recentEntries.clear();
        if (recentEntries != null) {
            this.recentEntries.addAll(recentEntries);
        }
    }

    public boolean removeRelatedEntry(final Snippet snippet) {
        for (RecentEntry recentEntry : recentEntries) {
            if (recentEntry.isRelatedTo(snippet)) {
                recentEntries.remove(recentEntry);

                // moving an entry from the older entries to fill the spot of the removed one
                if (!olderEntries.isEmpty()) {
                    recentEntries.addLast(olderEntries.getFirst());
                }
                
                return true;
            }
        }
        return false;
    }

    public void addRelatedEntry(final Snippet snippet) {
        recentEntries.addFirst(new RecentEntry(snippet));
        while (recentEntries.size() > MAX_ENTRIES) {
            olderEntries.addFirst(recentEntries.removeLast());
        }
        while (olderEntries.size() > MAX_ENTRIES) {
            olderEntries.removeLast();
        }
    }
    
}
