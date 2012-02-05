package co.cdev.agave;

import java.io.IOException;

import co.cdev.agave.Route;
import co.cdev.agave.configuration.RoutingContext;

public class SampleHandler {
    
    @Route("/login")
    public void login(RoutingContext context) throws IOException {
        
        // do nothing
        
    }
}
