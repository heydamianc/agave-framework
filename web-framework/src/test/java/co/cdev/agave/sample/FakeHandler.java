package co.cdev.agave.sample;

import co.cdev.agave.HandlerContext;
import co.cdev.agave.Param;
import co.cdev.agave.Route;

public class FakeHandler {
    
    @Route("/ambiguous")
    public void ambiguous(HandlerContext handlerContext) {
        
    }
    
    @Route("/ambiguous/${param}")
    public void ambiguous(HandlerContext handlerContext, @Param("param") String param) {
        
    }
    
}