package co.cdev.agave.sample;

import co.cdev.agave.Route;
import co.cdev.agave.configuration.RoutingContext;

import com.google.inject.Inject;

public class SampleHandler {

    private final SampleDependency sampleDependency;
    
    @Inject
    public SampleHandler(SampleDependency sampleDependency) {
        this.sampleDependency = sampleDependency;
    }
    
    @Route("/message")
    public void getMessage(RoutingContext routingContext) {
        routingContext.getRequest().setAttribute("message", sampleDependency.getMessage());
    }
    
}
