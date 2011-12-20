package co.cdev.agave.sample;

import co.cdev.agave.RoutingContext;
import co.cdev.agave.Param;
import co.cdev.agave.Route;

public class FakeHandler {
    
    @Route("/ambiguous")
    public void ambiguous(RoutingContext handlerContext) {
        
    }
    
    @Route("/ambiguous/${param}")
    public void ambiguous(RoutingContext handlerContext, @Param("param") String param) {
        
    }
    
}