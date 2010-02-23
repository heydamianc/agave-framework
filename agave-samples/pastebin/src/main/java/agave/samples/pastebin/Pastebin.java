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
package agave.samples.pastebin;

import agave.samples.pastebin.overview.DefaultOverviewService;
import agave.samples.pastebin.overview.FilesystemOverviewRepository;
import agave.samples.pastebin.overview.OverviewRepository;
import agave.samples.pastebin.overview.OverviewService;
import agave.samples.pastebin.repository.RepositoryException;
import agave.samples.pastebin.snippet.DefaultSnippetService;
import agave.samples.pastebin.snippet.FilesystemSnippetRepository;
import agave.samples.pastebin.snippet.Snippet;
import agave.samples.pastebin.snippet.SnippetRepository;
import agave.samples.pastebin.snippet.SnippetService;
import agave.samples.util.SingleLineLogFormatter;
import java.io.File;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class Pastebin implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(Pastebin.class.getSimpleName());

    public static final String REAPER_INTERVAL_MINS = "REAPER_INTERVAL_MINS";
    public static final String REPOSITORY_PATH = "REPOSITORY_PATH";

    public static final String OVERVIEW_SVC_KEY = String.format("key[%s]", OverviewService.class.getName());
    public static final String SNIPPET_SVC_KEY = String.format("key[%s]", SnippetService.class.getName());

    private final ScheduledExecutorService scheduledExecutor;

    private File repositoryDir;

    private SnippetRepository snippetRepository;
    private OverviewRepository overviewRepository;

    private SnippetService snippetService;
    private OverviewService overviewService;

    private ScheduledFuture<?> reaper;

    public Pastebin() {
        SingleLineLogFormatter.apply();
        this.scheduledExecutor = Executors.newScheduledThreadPool(1, new ThreadFactory() {
            public Thread newThread(Runnable task) {
                Thread thread = new Thread(task, "Snippet Reaper");
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    public void contextInitialized(final ServletContextEvent event) {
        createRepositoryDir(event.getServletContext());
        createRepositories();
        createServices();
        connectServices();
        addServicesToServletContext(event.getServletContext());
        scheduleReaper(event.getServletContext());
    }

    public void contextDestroyed(final ServletContextEvent event) {
        stopReaper();
        disconnectServices();
    }

    private void createRepositoryDir(final ServletContext servletContext) {
        String repositoryAttr = (String) servletContext.getAttribute(REPOSITORY_PATH);

        if (repositoryAttr == null || repositoryAttr.equals("")) {
            repositoryDir = new File(new File(servletContext.getRealPath("/WEB-INF")), "repository");
        } else {
            repositoryDir = new File(repositoryAttr);
        }

        repositoryDir.mkdirs();
        logger.info(String.format("Using %s as repository directory", repositoryDir.getAbsolutePath()));

        if (!repositoryDir.isDirectory()) {
            throw new IllegalArgumentException(String.format("%s is not an actual directory",
                    repositoryDir.getAbsolutePath()));
        } else if (!repositoryDir.canRead()) {
            throw new IllegalArgumentException(String.format("Unable to access %s",
                    repositoryDir.getAbsolutePath()));
        }
    }

    private void createRepositories() {
        try {
            overviewRepository = new FilesystemOverviewRepository(repositoryDir);
        } catch (RepositoryException ex) {
            logger.log(Level.SEVERE, "Unable to initialize overview repository", ex);
        }

        snippetRepository =  new FilesystemSnippetRepository(repositoryDir);
    }

    private void createServices() {
        snippetService = new DefaultSnippetService(snippetRepository);
        overviewService = new DefaultOverviewService(overviewRepository);
    }

    private void connectServices() {
        snippetService.onSnippetAdded().addObserver(new Observer() {
            public void update(Observable source, Object arg) {
                overviewService.respondToSnippetAdded((Snippet) arg);
            }
        });
        snippetService.onSnippetRemoved().addObserver(new Observer() {
            public void update(Observable source, Object arg) {
                overviewService.respondToSnippetRemoved((Snippet) arg);
            }
        });
    }

    private void addServicesToServletContext(final ServletContext servletContext) {
        servletContext.setAttribute(SNIPPET_SVC_KEY, snippetService);
        servletContext.setAttribute(OVERVIEW_SVC_KEY, overviewService);
    }
    
    private void scheduleReaper(final ServletContext servletContext) {
        int reaperIntervalMins = 30;

        try {
            reaperIntervalMins = Integer.parseInt((String) servletContext.getAttribute(REAPER_INTERVAL_MINS));
        } catch (Throwable ex) {
            // swallow exception - interval will be set to 30 mins
        }
        
        reaper = scheduledExecutor.scheduleAtFixedRate(new Reaper(), 0, reaperIntervalMins, TimeUnit.MINUTES);
    }

    private void stopReaper() {
        reaper.cancel(true);
        scheduledExecutor.shutdownNow();
    }

    private void disconnectServices() {
        snippetService.onSnippetAdded().deleteObservers();
        snippetService.onSnippetRemoved().deleteObservers();
    }

    private final class Reaper implements Runnable {
        public void run() {
            try {
                snippetService.removeRetiredSnippets(new Date());
            } catch (ServiceException ex) {
                Logger.getLogger(Pastebin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
