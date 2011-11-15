package co.cdev.agave.internal;

import java.lang.reflect.Method;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import co.cdev.agave.HandlerContext;
import co.cdev.agave.HttpMethod;
import co.cdev.agave.sample.FakeHandler;

public class HandlerIdentifierTest {
    
    private String uri;
    private HttpMethod method;
    private String className;
    private String methodName;
    private Collection<Class<?>> argumentTypes;
    private HandlerIdentifier handlerIdentifier; 
    
    @Before
    public void setUp() {
        uri = "/ambiguous";
        method = HttpMethod.GET;
        className = FakeHandler.class.getName();
        methodName = "ambiguous";
        
        handlerIdentifier = new HandlerIdentifierImpl(uri, method, className, methodName, argumentTypes);
    }

    @Test
    public void testMatches_withNoParams() throws Exception {
        Method methodWithNoParams = FakeHandler.class.getMethod("ambiguous", HandlerContext.class);
        Assert.assertTrue(handlerIdentifier.matches(methodWithNoParams));
    }
    
    @Test
    public void testMatches_withParams() throws Exception {
        Method methodWithParams = FakeHandler.class.getMethod("ambiguous", HandlerContext.class, String.class);
        Assert.assertFalse(handlerIdentifier.matches(methodWithParams));
    }
    
}
