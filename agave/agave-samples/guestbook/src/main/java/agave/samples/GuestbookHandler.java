package agave.samples;

import agave.AbstractHandler;
import agave.Destination;
import agave.HandlesRequestsTo;

import javax.servlet.http.HttpServletRequest;

public class GuestbookHandler extends AbstractHandler {

    @HandlesRequestsTo("/guestbook")
    public Destination guestbook(GuestbookForm form) throws Exception {
        request.setAttribute("form", form);
        request.setAttribute("contextPath", request.getContextPath());
        return new Destination("/WEB-INF/jsp/guestbook.jsp");
    }
    
}
