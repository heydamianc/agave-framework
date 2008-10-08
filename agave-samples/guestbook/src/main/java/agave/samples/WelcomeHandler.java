package agave.samples;

import agave.AbstractHandler;
import agave.Destination;
import agave.HandlesRequestsTo;

import javax.servlet.http.HttpServletRequest;

public class WelcomeHandler extends AbstractHandler {

    @HandlesRequestsTo("/")
    public Destination welcome() throws Exception {
        GuestbookHandler guestbook = new GuestbookHandler();
        guestbook.setRequest(request);
        guestbook.setResponse(response);
        return guestbook.guestbook(new GuestbookForm());
    }
    
}
