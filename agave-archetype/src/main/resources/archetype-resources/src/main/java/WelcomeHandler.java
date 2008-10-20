package $package;

import agave.AbstractHandler;
import agave.Destination;
import agave.HandlesRequestsTo;

public class WelcomeHandler extends AbstractHandler {

    @HandlesRequestsTo("/")
    public Destination welcome() throws Exception {
        request.setAttribute("contextPath", request.getContextPath());
        request.setAttribute("context", request.getContextPath().substring(1));
        return new Destination("/WEB-INF/jsp/index.jsp");
    }
    
}
