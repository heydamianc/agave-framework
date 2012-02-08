package co.cdev.agave.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.servlet.ServletContext;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import co.cdev.agave.web.Destination;
import co.cdev.agave.web.Destinations;

public class DestinationTest {
    
    Mockery context = new Mockery();
    ServletContext servletContext;
    
    @Before
    public void setup() throws Exception {
        servletContext = context.mock(ServletContext.class);
    }

    @Test
    public void testConstructorWithOnlyPathArgument() throws Exception {
        Destination dest = Destinations.create("/some/resource");
        assertNotNull(dest);
        assertNull(dest.getRedirect());
    }
    
    @Test
    public void testConstructorWithPathAndRedirectArguments() throws Exception {
        Destination dest = Destinations.redirect("/some/resource");
        assertNotNull(dest);
        assertNotNull(dest.getRedirect());
        assertTrue(dest.getRedirect());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithInvalidPath() throws Exception {
        Destination dest = Destinations.create("some/resource");
        assertNotNull(dest);
    }
    
    @Test
    public void testAddingParameters() throws Exception {
        Destination dest = Destinations.create("/some/resource");
        dest.addParameter("dog", "woof");
        dest.addParameter("dog", "bark");
        dest.addParameter("cat", "meow");
        dest.addParameter("cat", "purr");
        
        assertNotNull(dest.getParams());
        assertNotNull(dest.getParams().get("dog"));
        assertNotNull(dest.getParams().get("cat"));
        assertTrue(dest.getParams().get("dog").contains("woof"));
        assertTrue(dest.getParams().get("dog").contains("bark"));
        assertTrue(dest.getParams().get("cat").contains("meow"));
        assertTrue(dest.getParams().get("cat").contains("purr"));
    }
    
    @Test
    public void testEncode() throws Exception { 
        Destination dest = Destinations.create("/some/resource");
        dest.addParameter("dog", "woof");
        dest.addParameter("dog", "bark");
        dest.addParameter("cat", "meow");
        dest.addParameter("cat", "purr");
        
        context.checking(new Expectations() {{
            allowing(servletContext).getContextPath(); will(returnValue("/app"));
        }});
        
        assertEquals("/some/resource?dog=woof&dog=bark&cat=meow&cat=purr", dest.encode(servletContext));

        dest.addParameter("bird", "chirp&chirp");
        
        assertEquals("/some/resource?dog=woof&dog=bark&cat=meow&cat=purr&bird=chirp&amp;chirp", dest.encode(servletContext));
    }
    
}
