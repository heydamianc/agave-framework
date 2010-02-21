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

import agave.Destination;
import agave.Destinations;
import agave.HandlerContext;
import agave.HandlesRequestsTo;
import agave.samples.pastebin.Pastebin;
import agave.samples.pastebin.ServiceException;
import agave.samples.pastebin.overview.Overview;
import agave.samples.pastebin.overview.OverviewService;
import agave.samples.pastebin.snippet.Snippet;
import agave.samples.pastebin.snippet.SnippetService;
import freemarker.template.TemplateException;
import javax.servlet.ServletContext;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class PastebinHandler extends AbstractHandler {

    @HandlesRequestsTo("/")
    public void welcome(HandlerContext handlerContext, PastebinForm form) {
        ServletContext servletContext = handlerContext.getServletContext();

        try {
            PastebinViewModel model = new PastebinViewModel();
            model.setContextPath(handlerContext.getRequest().getContextPath());
            
            OverviewService overviewService =
                    (OverviewService) servletContext.getAttribute(Pastebin.OVERVIEW_SVC_KEY);
            
            model.setOverview(overviewService.getOverview());

            renderTemplate(handlerContext, model, "pastebin.ftl");
        } catch (ServiceException ex) {
            // TODO redirect to error page
        } catch (IOException ex) {
            // TODO redirect to error page
        } catch (TemplateException ex) {
            // TODO redirect to error page
        }
    }

    @HandlesRequestsTo("/create")
    public Destination create(HandlerContext handlerContext, PastebinForm form) {
        String uniqueId = null;
        ServletContext servletContext = handlerContext.getServletContext();

        try {
            SnippetService snippetService =
                    (SnippetService) servletContext.getAttribute(Pastebin.SNIPPET_SVC_KEY);
            
            Snippet snippet = new Snippet();
            form.copyValuesTo(snippet);
            uniqueId = snippetService.saveSnippet(snippet);
        } catch (ServiceException ex) {
            // TODO redirect to error page
        }

        return Destinations.redirect("/" + uniqueId);
    }

    @HandlesRequestsTo("/${uniqueId}")
    public void snippet(HandlerContext handlerContext, PastebinForm form) {
        ServletContext servletContext = handlerContext.getServletContext();

        try {
            SnippetService snippetService =
                    (SnippetService) servletContext.getAttribute(Pastebin.SNIPPET_SVC_KEY);
            
            Snippet snippet = snippetService.getSnippet(form.getUniqueId());

            if (snippet != null) {
                PastebinViewModel model = new PastebinViewModel();
                model.setContextPath(handlerContext.getRequest().getContextPath());
                model.setSnippet(snippet);

                OverviewService overviewService =
                        (OverviewService) servletContext.getAttribute(Pastebin.OVERVIEW_SVC_KEY);

                Overview overview = overviewService.getOverview();
                model.setOverview(overview);

                renderTemplate(handlerContext, model, "snippet.ftl");
            } else {
                // TODO indicate that snippet was not found
            }
        } catch (ServiceException ex) {
            // TODO redirect to error page
        } catch (IOException ex) {
            // TODO redirect to error page
        } catch (TemplateException ex) {
            // TODO redirect to error page
        }
    }
    
}
