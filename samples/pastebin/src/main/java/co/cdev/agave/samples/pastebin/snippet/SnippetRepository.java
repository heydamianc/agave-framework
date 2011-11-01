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
package co.cdev.agave.samples.pastebin.snippet;

import co.cdev.agave.samples.pastebin.repository.RetrievalException;
import co.cdev.agave.samples.pastebin.repository.StorageException;
import java.util.Set;

/**
 * Provides repository support for snippets.
 *
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public interface SnippetRepository {

    /**
     * Find a unique ID.  The ID does not have to be serial, just unique.
     *
     * @param expiration provides an optional namespace to segment across
     * @return a unique string ID
     */
    public String determineUniqueId(Timeframe expiration);

    /**
     * Stores a snippet in the repository.
     * 
     * @param snippet the snippet to store
     * @throws StorageException if storage was unsuccessful 
     */
    public void storeSnippet(Snippet snippet) throws StorageException;

    /**
     * Retrieves all of the snippets.
     *
     * @return all of the snippets
     * @throws RetrievalException if retrieval was unsuccessful
     */
    public Set<Snippet> retrieveAllSnippets() throws RetrievalException;

    /**
     * Retrieves a specific snippet.
     *
     * @param snippetId the id of the snippet to retrieve
     * @return the retrieved snippet
     * @throws RetrievalException if retrieval was unssuccessful
     */
    public Snippet retrieveSnippet(String snippetId) throws RetrievalException;

    /**
     * Retrieve a specific version of a specific snippet.
     *
     * @param snippetId the id of the snippet to retrieve
     * @param revision the revision of the snippet to retrieve
     * @return the retrieved snippet
     * @throws RetrievalException if retrieval was unsuccessful
     */
    public Snippet retrieveSnippet(String snippetId, long revision) throws RetrievalException;

    public void discardSnippet(Snippet snippet);
    
}
