package co.cdev.gson;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import co.cdev.agave.web.HTTPResponse;

import com.google.gson.Gson;

public class JSONResponseProcessorTest {

    @Test
    public void testCanProcessResult_withMatch() {
        Gson gson = new Gson();
        JSONResponseProcessor responseProcessor = new JSONResponseProcessor(gson);
        assertTrue(responseProcessor.canProcessResult(new JSONResponse(), null, null));
    }
    
    @Test
    public void testCanProcessResult_withoutMatch() {
        Gson gson = new Gson();
        JSONResponseProcessor responseProcessor = new JSONResponseProcessor(gson);
        assertFalse(responseProcessor.canProcessResult(new HTTPResponse(), null, null));
    }
    
}
