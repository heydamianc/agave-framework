package co.cdev.gson;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import co.cdev.agave.web.StatusCode;

public class JSONResponseTest {
    
    @Test
    public void testConstructor() {
        JSONResponse response = new JSONResponse(StatusCode._200_Ok, null);
        assertEquals("application/json", response.getContentType());
    }
    
}
