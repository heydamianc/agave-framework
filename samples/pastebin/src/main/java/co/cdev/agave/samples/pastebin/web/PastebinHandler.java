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
package co.cdev.agave.samples.pastebin.web;

import co.cdev.agave.samples.pastebin.ServiceException;
import co.cdev.agave.samples.pastebin.overview.Overview;
import co.cdev.agave.samples.pastebin.snippet.Snippet;
import co.cdev.agave.Destination;
import co.cdev.agave.Destinations;
import co.cdev.agave.HandlerContext;
import co.cdev.agave.HandlesRequestsTo;
import javax.servlet.ServletContext;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class PastebinHandler extends AbstractHandler {

    private static final String SNIPPET_KEY = "snippet";
    private static final String OVERVIEW_KEY = "overview";

    @HandlesRequestsTo("/")
    public Destination welcome(HandlerContext handlerContext, PastebinForm form) {
        ServletContext servletContext = handlerContext.getServletContext();
        try {
            Overview overview = getOverviewService(servletContext).getOverview();
            handlerContext.getRequest().setAttribute(OVERVIEW_KEY, overview);
        } catch (ServiceException ex) {
            // TODO redirect to error page
        }
        return Destinations.forward("/WEB-INF/pastebin.jsp");
    }

    @HandlesRequestsTo("/create")
    public Destination create(HandlerContext handlerContext, PastebinForm form) {
        String uniqueId = null;
        ServletContext servletContext = handlerContext.getServletContext();
        try {
            Snippet snippet = new Snippet();
            form.copyValuesTo(snippet);
            uniqueId = getSnippetService(servletContext).saveSnippet(snippet);
        } catch (ServiceException ex) {
            // TODO redirect to error page
        }
        return Destinations.redirect(String.format("/%s", uniqueId));
    }

    @HandlesRequestsTo("/${uniqueId}")
    public Destination snippet(HandlerContext handlerContext, PastebinForm form) {
        ServletContext servletContext = handlerContext.getServletContext();
        try {
            Snippet snippet = getSnippetService(servletContext).getSnippet(form.getUniqueId());
            Overview overview = getOverviewService(servletContext).getOverview();
            handlerContext.getRequest().setAttribute(SNIPPET_KEY, snippet);
            handlerContext.getRequest().setAttribute(OVERVIEW_KEY, overview);
        } catch (ServiceException ex) {
            // TODO redirect to error page
        }
        return Destinations.forward("/WEB-INF/snippet.jsp");
    }
    
}
