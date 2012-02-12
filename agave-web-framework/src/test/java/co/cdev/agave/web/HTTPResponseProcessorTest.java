package co.cdev.agave.web;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.junit.Test;

public class HTTPResponseProcessorTest extends AbstractResponseProcessorTest {
    
    @Test
    public void testProcess() throws Exception {
        HTTPResponseProcessor responseProcessor = new HTTPResponseProcessor();
        
        mockery.checking(new Expectations() {{
            one(response).setStatus(200);
            one(response).setContentType("text/plain");
        }});
        
        responseProcessor.process(new HTTPResponse(StatusCode._200_Ok, "text/plain", "Test"), routingContext, handlerDescriptor);
        
        assertEquals("Test", out.toString());
    }
    
    @Test
    public void testProcess_withoutStatusCode() throws Exception {
        HTTPResponseProcessor responseProcessor = new HTTPResponseProcessor();
        
        mockery.checking(new Expectations() {{
            one(response).setContentType("text/plain");
        }});
        
        responseProcessor.process(new HTTPResponse(null, "text/plain", "Test"), routingContext, handlerDescriptor);
    }
    
    @Test
    public void testProcess_withoutContentType() throws Exception {
        HTTPResponseProcessor responseProcessor = new HTTPResponseProcessor();
        
        mockery.checking(new Expectations() {{
            one(response).setStatus(200);
        }});
        
        responseProcessor.process(new HTTPResponse(StatusCode._200_Ok, null, "Test"), routingContext, handlerDescriptor);
    }
    
}
