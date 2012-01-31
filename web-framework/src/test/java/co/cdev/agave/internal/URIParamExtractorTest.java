package co.cdev.agave.internal;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import co.cdev.agave.URIParamExtractor;
import co.cdev.agave.URIParamExtractorImpl;
import co.cdev.agave.URIPattern;
import co.cdev.agave.URIPatternImpl;

public class URIParamExtractorTest {

    Mockery context = new Mockery();
    HttpServletRequest request;
    
    @Before
    public void setup() throws Exception {
        request = context.mock(HttpServletRequest.class);
    }
    
    @Test
    public void testGetParameterMap() throws Exception {
        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue("/one/two/buckle/my/shoe/"));
        }});
        
        URIPattern uriPattern = new URIPatternImpl("/one/two/${three}/${four}/${five}");
        URIParamExtractor paramExtractor = new URIParamExtractorImpl(uriPattern);
        Map<String, String> params = paramExtractor.extractParams(request);
        Assert.assertNotNull(params);
        Assert.assertEquals(3, params.size());
        Assert.assertEquals("buckle", params.get("three"));
        Assert.assertEquals("my", params.get("four"));
        Assert.assertEquals("shoe", params.get("five"));
    }
    
}
