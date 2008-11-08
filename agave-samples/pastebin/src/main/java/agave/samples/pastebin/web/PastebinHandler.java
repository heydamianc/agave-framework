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

import java.io.IOException;
import java.io.PrintWriter;

import agave.Destination;
import agave.Destinations;
import agave.HandlerContext;
import agave.HandlesRequestsTo;
import agave.exception.AgaveException;
import agave.samples.pastebin.overview.Overview;
import agave.samples.pastebin.overview.OverviewService;
import agave.samples.pastebin.overview.RecentEntry;
import agave.samples.pastebin.snippet.Snippet;
import agave.samples.pastebin.snippet.SnippetRepository;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class PastebinHandler extends AbstractHandler {
    
    @HandlesRequestsTo("/")
    public void init(HandlerContext handlerContext, PastebinForm form)
    throws AgaveException, IOException, TemplateException, CloneNotSupportedException, ClassNotFoundException {
        PastebinViewModel model = new PastebinViewModel();
        model.setContextPath(handlerContext.getRequest().getContextPath());
        
        OverviewService overviewService = 
        	OverviewServiceFactory.createOverviewService(handlerContext.getServletContext());
        Overview overview = overviewService.retrieveOverview();
        model.setOverview(overview);
        
        PrintWriter out = handlerContext.getResponse().getWriter();
        Configuration config = 
        	(Configuration)handlerContext.getRequest().getAttribute(FreemarkerFilter.FREEMARKER_CONFIG_KEY);
        Template template = config.getTemplate("pastebin.ftl");
        template.process(model, out);
        out.flush();
        out.close();
    }
    
    @HandlesRequestsTo("/create")
    public Destination create(HandlerContext handlerContext, PastebinForm form)
    throws AgaveException, IOException, TemplateException, ClassNotFoundException {
        SnippetRepository repo = 
        	SnippetRepositoryFactory.createFilesystemRepository(handlerContext.getServletContext());
        
        String uniqueId = repo.determineUniqueId(form.getExpiration());
        Snippet snippet = new Snippet();
        snippet.setUniqueId(uniqueId);
        form.extract(snippet);
        repo.storeSnippet(snippet);

        if (!snippet.isPrivateSnippet()) {
            OverviewService overviewService = 
            	OverviewServiceFactory.createOverviewService(handlerContext.getServletContext());
            Overview overview = overviewService.retrieveOverview();
            if (overview.getRecentEntries().size() >= 10) {
                overview.getRecentEntries().removeLast();
            }
            overview.getRecentEntries().addFirst(new RecentEntry(snippet));
            overviewService.storeOverview(overview);
        }
        
        return Destinations.redirect("/" + uniqueId);
    }
    
    @HandlesRequestsTo("/${uniqueId}")
    public void snippet(HandlerContext handlerContext, PastebinForm form)
    throws AgaveException, IOException, TemplateException, CloneNotSupportedException, ClassNotFoundException {
        SnippetRepository repo = 
        	SnippetRepositoryFactory.createFilesystemRepository(handlerContext.getServletContext());
        Snippet snippet = repo.retrieveSnippet(form.getUniqueId());
        
        PastebinViewModel model = new PastebinViewModel();
        model.setContextPath(handlerContext.getRequest().getContextPath());
        model.setSnippet(snippet);
        
        OverviewService overviewService = 
        	OverviewServiceFactory.createOverviewService(handlerContext.getServletContext());
        Overview overview = overviewService.retrieveOverview();
        model.setOverview(overview);
        
        PrintWriter out = handlerContext.getResponse().getWriter();
        Configuration config = 
        	(Configuration)handlerContext.getRequest().getAttribute(FreemarkerFilter.FREEMARKER_CONFIG_KEY);
        Template template = config.getTemplate("snippet.ftl");
        template.process(model, out);
        out.flush();
        out.close();
    }
    
}
