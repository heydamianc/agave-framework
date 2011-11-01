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
package co.cdev.agave.samples.pastebin.overview;

import co.cdev.agave.samples.pastebin.ServiceException;
import co.cdev.agave.samples.pastebin.repository.RetrievalException;
import co.cdev.agave.samples.pastebin.repository.StorageException;
import co.cdev.agave.samples.pastebin.snippet.Snippet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class DefaultOverviewService implements OverviewService {

    private static final Logger logger = Logger.getLogger(DefaultOverviewService.class.getSimpleName());

    private final OverviewRepository overviewRepository;

    private Overview cachedOverview;

    public DefaultOverviewService(final OverviewRepository overviewRepository) {
        this.overviewRepository = overviewRepository;
    }

    public synchronized Overview getOverview() throws ServiceException {
        if (cachedOverview != null) {
            return cachedOverview;
        }
        
        try {
            return overviewRepository.retrieveOverview();
        } catch (RetrievalException ex) {
            throw new ServiceException(ex);
        }
    }
    
    public synchronized void respondToSnippetAdded(final Snippet snippet) {
        if (!snippet.isPrivateSnippet()) {
            try {
                if (cachedOverview == null) {
                    cachedOverview = getOverview();
                }
                cachedOverview.addRelatedEntry(snippet);
                overviewRepository.storeOverview(cachedOverview);
            } catch (StorageException ex) {
                logger.log(Level.SEVERE, "Unable to store overview", ex);
            } catch (ServiceException ex) {
                logger.log(Level.SEVERE, "Unable to get overview", ex);
            }
        }
    }

    public synchronized void respondToSnippetRemoved(final Snippet snippet) {
        try {
            if (cachedOverview == null) {
                cachedOverview = getOverview();
            }
            cachedOverview.removeRelatedEntry(snippet);
            overviewRepository.storeOverview(cachedOverview);
        } catch (StorageException ex) {
            logger.log(Level.SEVERE, "Unable to store overview", ex);
        } catch (ServiceException ex) {
            logger.log(Level.SEVERE, "Unable to store overview", ex);
        }
    }

}
