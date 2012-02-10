package co.cdev.agave.sample;

import co.cdev.agave.Route;
import co.cdev.agave.configuration.RoutingContext;
import co.cdev.agave.web.StatusCode;

public class StringResponseHandler {

    @Route("/text")
    public StringResponse str(RoutingContext context) {
        StringResponse response = new StringResponse();
        response.setContentType("text/plain");
        response.setMessageBody("Text!");
        response.setStatusCode(StatusCode._200_Ok);
        return response;
    }
    
}
