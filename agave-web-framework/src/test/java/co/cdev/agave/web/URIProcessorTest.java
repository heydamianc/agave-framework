package co.cdev.agave.web;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

public class URIProcessorTest extends AbstractResponseProcessorTest {
    
    private URIProcessor uriProcessor;
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        uriProcessor = new URIProcessor();
    }
    
    @Test
    public void testCanProcessResults() throws Exception {
        assertTrue(uriProcessor.canProcessResult(new URI("/something"), routingContext, handlerDescriptor));
        assertFalse(uriProcessor.canProcessResult(new Object(), routingContext, handlerDescriptor));
    }
    
    @Test
    public void testProcess_withAbsolutePath() throws Exception {
        mockery.checking(new Expectations() {{
            allowing(request).getContextPath(); will(returnValue("/context"));
            
            one(response).sendRedirect("/context/resource");
        }});
        
        uriProcessor.process(new URI("/resource"), routingContext, handlerDescriptor);
    }

    @Test
    public void testProcess_withRelativePath() throws Exception {
        mockery.checking(new Expectations() {{
            one(response).sendRedirect("resource");
        }});
        
        uriProcessor.process(new URI("resource"), routingContext, handlerDescriptor);
    }
    
}
