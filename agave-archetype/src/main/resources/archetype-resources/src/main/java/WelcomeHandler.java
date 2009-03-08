package org.dcarrillo;

import agave.Destination;
import agave.Destinations;
import agave.HandlerContext;
import agave.HandlesRequestsTo;

import javax.servlet.http.HttpServletRequest;

public class WelcomeHandler {

    /**
     * Serves the same purpose as that of a 'welcome-file' entry in the web.xml file.
     * Note, that binding a handler to the root of the application like we are doing here will 
     * always override the 'welcome-file' entry.
     *
     * @param handlerContext the context that this handler method executes under
     * @throws Exception if anything goes wrong
     * @return a destination object that wraps the index.jsp page
     */
    @HandlesRequestsTo("/")
    public Destination welcome(HandlerContext handlerContext) throws Exception {
        HttpServletRequest request = handlerContext.getRequest();

        request.setAttribute("world", System.getProperty("user.name"));
        request.setAttribute("contextPath", request.getContextPath());

        return Destinations.forward("/WEB-INF/jsp/index.jsp");
    }
    
}
