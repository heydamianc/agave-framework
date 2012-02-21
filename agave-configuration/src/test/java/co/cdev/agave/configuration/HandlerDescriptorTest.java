package co.cdev.agave.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import co.cdev.agave.HttpMethod;
import co.cdev.agave.URIPatternImpl;
import co.cdev.agave.sample.LoginForm;
import co.cdev.agave.sample.SampleHandler;

@SuppressWarnings("serial")
public class HandlerDescriptorTest {
    
    private Class<?> handlerClass;
    private Method handlerMethod;

    @Before
    public void setUp() throws SecurityException, NoSuchMethodException {
        handlerClass = SampleHandler.class;
        handlerMethod = handlerClass.getMethod("login", RoutingContext.class, LoginForm.class);
    }
    
    @Test
    public void testEquals() throws Exception {
        HandlerDescriptor a = new HandlerDescriptorImpl(
                handlerClass, 
                handlerMethod, 
                new URIPatternImpl("/login"), 
                HttpMethod.GET, 
                false, 
                false, 
                (String) null, 
                (Class<?>) null, 
                new ArrayList<ParamDescriptor>());

        HandlerDescriptor b = new HandlerDescriptorImpl(
                handlerClass, 
                handlerMethod, 
                new URIPatternImpl("/login"), 
                HttpMethod.GET, 
                false,
                false, 
                (String) null, 
                (Class<?>) null, 
                new ArrayList<ParamDescriptor>());
        
        Assert.assertEquals(0, a.compareTo(b));
    }
    
    @Test
    public void testCompareTo_withDistinctPath() throws Exception {
        HandlerDescriptor a = new HandlerDescriptorImpl(
                handlerClass, 
                handlerMethod, 
                new URIPatternImpl("/a"), 
                HttpMethod.GET, 
                false, 
                false, 
                (String) null, 
                (Class<?>) null, 
                new ArrayList<ParamDescriptor>());

        HandlerDescriptor b = new HandlerDescriptorImpl(
                handlerClass, 
                handlerMethod, 
                new URIPatternImpl("/b"), 
                HttpMethod.GET, 
                false,
                false, 
                (String) null, 
                (Class<?>) null, 
                new ArrayList<ParamDescriptor>());
        
        Assert.assertTrue(a.compareTo(b) < 0 && 0 < b.compareTo(a));
    }
    
    @Test
    public void testCompareTo_withDuplicatePathAndDistinctMethod() throws Exception {
        HandlerDescriptor a = new HandlerDescriptorImpl(
                handlerClass, 
                handlerMethod, 
                new URIPatternImpl("/login"), 
                HttpMethod.GET, 
                false, 
                false, 
                (String) null, 
                (Class<?>) null, 
                new ArrayList<ParamDescriptor>());

        HandlerDescriptor b = new HandlerDescriptorImpl(
                handlerClass, 
                handlerMethod, 
                new URIPatternImpl("/login"), 
                HttpMethod.POST, 
                false,
                false, 
                (String) null, 
                (Class<?>) null, 
                new ArrayList<ParamDescriptor>());
        
        Assert.assertTrue(a.compareTo(b) < 0 && 0 < b.compareTo(a));
    }
    
    @Test
    public void testCompareTo_withDuplicatePathAndMethodAndDifferentNumberOfParameters() throws Exception {
        HandlerDescriptor a = new HandlerDescriptorImpl(
                handlerClass, 
                handlerMethod, 
                new URIPatternImpl("/login"), 
                HttpMethod.GET, 
                false, 
                false, 
                (String) null, 
                (Class<?>) null, 
                new ArrayList<ParamDescriptor>() {{
                    add(new ParamDescriptorImpl(String.class, "a", null));
                    add(new ParamDescriptorImpl(String.class, "b", null));
                    add(new ParamDescriptorImpl(String.class, "c", null));
                }});

        HandlerDescriptor b = new HandlerDescriptorImpl(
                handlerClass, 
                handlerMethod, 
                new URIPatternImpl("/login"), 
                HttpMethod.GET, 
                false,
                false, 
                (String) null, 
                (Class<?>) null, 
                new ArrayList<ParamDescriptor>() {{
                    add(new ParamDescriptorImpl(String.class, "a", null));
                    add(new ParamDescriptorImpl(String.class, "b", null));
                }});
        
        Assert.assertTrue(a.compareTo(b) < 0 && 0 < b.compareTo(a));
    }
    
    @Test
    public void testCompareTo_withDuplicatePathAndMethodAndSameNumberOfParameters() throws Exception {
        HandlerDescriptor a = new HandlerDescriptorImpl(
                handlerClass, 
                handlerMethod, 
                new URIPatternImpl("/login"), 
                HttpMethod.GET, 
                false, 
                false, 
                (String) null, 
                (Class<?>) null, 
                new ArrayList<ParamDescriptor>() {{
                    add(new ParamDescriptorImpl(String.class, "a", null));
                    add(new ParamDescriptorImpl(Integer.class, "b", null));
                }});

        HandlerDescriptor b = new HandlerDescriptorImpl(
                handlerClass, 
                handlerMethod, 
                new URIPatternImpl("/login"), 
                HttpMethod.GET, 
                false,
                false, 
                (String) null, 
                (Class<?>) null, 
                new ArrayList<ParamDescriptor>() {{
                    add(new ParamDescriptorImpl(String.class, "a", null));
                    add(new ParamDescriptorImpl(String.class, "b", null));
                }});
        
        Assert.assertTrue(a.compareTo(b) != 0 && b.compareTo(a) != 0);
    }
    
    @Test
    public void testSerialize() throws Exception {
        HandlerDescriptor a = new HandlerDescriptorImpl(
                handlerClass, 
                handlerMethod, 
                new URIPatternImpl("/login"), 
                HttpMethod.GET, 
                false, 
                false, 
                (String) null, 
                (Class<?>) null, 
                new ArrayList<ParamDescriptor>());
        
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(bout));
        out.writeObject(a);
    }
    
    @Test
    public void testDeserialize() throws Exception {
        HandlerDescriptor a = new HandlerDescriptorImpl(
                handlerClass, 
                handlerMethod, 
                new URIPatternImpl("/login"), 
                HttpMethod.GET, 
                false, 
                false, 
                (String) null, 
                (Class<?>) null, 
                new ArrayList<ParamDescriptor>());
        
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(bout));
        out.writeObject(a);
        out.close();
        
        byte[] bytes = bout.toByteArray();
        
        assertTrue(bytes.length > 0);
        
        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(bin));
        HandlerDescriptor b = (HandlerDescriptor) in.readObject();
        in.close();
        
        assertEquals(a, b);
    }
    
}

