/**
 * Copyright (c) 2010, Damian Carrillo
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

import agave.samples.pastebin.ServiceException;
import agave.samples.pastebin.repository.RetrievalException;
import agave.samples.pastebin.repository.StorageException;
import agave.samples.util.Signal;
import java.util.Date;

/**
 *
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class DefaultSnippetService implements SnippetService {

    private final SnippetRepository snippetRepository;
    private final Signal snippetAdded;
    private final Signal snippetRemoved;

    public DefaultSnippetService(final SnippetRepository snippetRepository) {
        this.snippetRepository = snippetRepository;
        this.snippetAdded = new Signal();
        this.snippetRemoved = new Signal();
    }

    public void removeRetiredSnippets(final Date referenceDate) throws ServiceException {
        try {
            for (Snippet snippet : snippetRepository.retrieveAllSnippets()) {
                if (snippet.isExpired(referenceDate)) {
                    snippetRepository.discardSnippet(snippet);
                    snippetRemoved.raiseWith(snippet);
                }
            }
        } catch (RetrievalException ex) {
            throw new ServiceException(ex);
        }
    }

    public Snippet getSnippet(final String uniqueId) throws ServiceException {
        try {
            return snippetRepository.retrieveSnippet(uniqueId);
        } catch (RetrievalException ex) {
            throw new ServiceException(ex);
        }
    }

    public String saveSnippet(final Snippet snippet) throws ServiceException {
        boolean added = false;
        if (snippet.getUniqueId() == null) {
            snippet.setUniqueId(snippetRepository.determineUniqueId(snippet.getExpiration()));
            added = true;
        }

        try {
            if (snippet.getOwner() == null) {
                snippet.setOwner("Anonymous");
            }

            snippetRepository.storeSnippet(snippet);

            if (added) {
                snippetAdded.raiseWith(snippet);
            }
        } catch (StorageException ex) {
            throw new ServiceException("Unable to store snippet", ex);
        }
        
        return snippet.getUniqueId();
    }

    public Signal onSnippetAdded() {
        return snippetAdded;
    }

    public Signal onSnippetRemoved() {
        return snippetRemoved;
    }

}
