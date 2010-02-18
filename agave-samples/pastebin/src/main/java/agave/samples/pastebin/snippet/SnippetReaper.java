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

import agave.samples.pastebin.overview.Overview;
import agave.samples.pastebin.overview.OverviewService;
import agave.samples.pastebin.overview.OverviewServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class SnippetReaper implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(SnippetReaper.class.getSimpleName());
    private final ScheduledExecutorService scheduledExecutor;
    private ScheduledFuture reaperTask;

    public SnippetReaper() {
        scheduledExecutor = Executors.newScheduledThreadPool(1, new ThreadFactory() {
            public Thread newThread(Runnable task) {
                return new Thread(task, "Snippet Reaper");
            }
        });
    }

    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        ServletContext context = servletContextEvent.getServletContext();
        try {
            reaperTask = scheduledExecutor.scheduleAtFixedRate(new ReaperTask(context), 0, 30, TimeUnit.MINUTES);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Unable to schedule snippet reaper task", ex);
        }
    }

    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
        if (reaperTask != null)
            reaperTask.cancel(true);
        
        scheduledExecutor.shutdownNow();
    }

    /**
     * Performs the actual task of reaping snippets.
     */
    private static final class ReaperTask implements Runnable {

        private final SnippetRepository repository;
        private final OverviewService overviewService;

        private ReaperTask(final ServletContext context) throws IOException {
            this.repository = SnippetRepositoryFactory.createFilesystemRepository(context);
            this.overviewService = OverviewServiceFactory.createOverviewService(context);
        }

        public void run() {
            Date timestamp = new Date();
            try {
                Overview overview = overviewService.retrieveOverview();

                Set<Snippet> expiredSnippets = new HashSet<Snippet>();
                for (Snippet snippet: repository.retrieveAllSnippets()) {
                    if (snippet.isExpired(timestamp)) {
                        expiredSnippets.add(snippet);
                        overview.removeRelatedEntry(snippet);
                    }
                }
                repository.discardSnippets(expiredSnippets);
                overviewService.storeOverview(overview);
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Unable to retrieve all snippets.  They will not get reaped.", ex);
            }
        }
    }
}
