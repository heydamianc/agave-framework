package co.cdev.agave.sample;

import java.util.Date;

import co.cdev.agave.HttpMethod;
import co.cdev.agave.Param;
import co.cdev.agave.Route;
import co.cdev.agave.configuration.RoutingContext;

public class SampleEndpoint {
    
    @Route(uri = "/birds", method = HttpMethod.GET)
    public void listBirds(RoutingContext routingContext) {
        
    }

    @Route(uri = "/birds", method = HttpMethod.GET)
    public void listBirds(RoutingContext routingContext, 
                          @Param("token") String token) {
        
    }

    @Route(uri = "/birds", method = HttpMethod.GET)
    public void listBirds(RoutingContext routingContext, 
                          @Param("token") String token, 
                          @Param("page") int page) {
        
    }
    
    @Route(uri = "/birds", method = HttpMethod.GET)
    public void listBirds(RoutingContext routingContext, 
                          @Param("token") String token, 
                          @Param("since") Date since) {

    }
    
}
