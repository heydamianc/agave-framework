package co.cdev.agave.web;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

public class DestinationProcessorTest extends AbstractProcessorTest {
    
    private DestinationProcessor destinationProcessor;
    
    @Before
    public void setUp() {
        super.setUp();
        destinationProcessor = new DestinationProcessor();
    }
    
    @Test
    public void testCanProcessResults() throws Exception {
        assertTrue(destinationProcessor.canProcessResult(new DestinationImpl("/something"), routingContext, handlerDescriptor));
        assertFalse(destinationProcessor.canProcessResult(new Object(), routingContext, handlerDescriptor));
    }
    
    @Test
    public void testProcess_withRedirect() throws Exception {
        mockery.checking(new Expectations() {{
            allowing(request).getContextPath(); will(returnValue("/context"));
            
            one(response).sendRedirect("/context/resource");
        }});
        
        destinationProcessor.process(Destinations.redirect("/resource"), routingContext, handlerDescriptor);
    }
    
    @Test
    public void testProcess_withForward() throws Exception {
        mockery.checking(new Expectations() {{
            allowing(request).getContextPath(); will(returnValue("/context"));
            allowing(request).getRequestDispatcher("/resource");
            
            one(requestDispatcher).forward(request, response);
        }});
        
        destinationProcessor.process(Destinations.forward("/resource"), routingContext, handlerDescriptor);
    }
    
}
