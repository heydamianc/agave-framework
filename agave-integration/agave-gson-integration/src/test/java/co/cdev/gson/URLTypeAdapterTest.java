package co.cdev.gson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import co.cdev.gson.URLTypeAdapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

public class URLTypeAdapterTest extends AbstractHandlerTest {

    private URL url;
    private URLTypeAdapter urlTypeAdapter;
    
    @Before
    public void setUp() throws Exception {
        url = new URL("https://damiancarrillo@github.com/damiancarrillo/agave-web-framework.git");
        urlTypeAdapter = new URLTypeAdapter();
    }
    
    @Test
    public void testDeserialize() throws Exception {
        JsonElement element = new JsonPrimitive("https://damiancarrillo@github.com/damiancarrillo/agave-web-framework.git");
        assertEquals(url, urlTypeAdapter.deserialize(element, URL.class, deserializationContext));
    }
    
    @Test
    public void testDeserialize_withNullURL() throws Exception {
        assertNull(urlTypeAdapter.deserialize(JsonNull.INSTANCE, URL.class, deserializationContext));
    }
    
    @Test
    public void testSerialize() throws Exception {
        JsonElement element = urlTypeAdapter.serialize(url, URL.class, serializationContext);
        assertEquals(new JsonPrimitive("https://damiancarrillo@github.com/damiancarrillo/agave-web-framework.git"), element);
    }
    
    @Test
    public void testSerialize_withNull() throws Exception {
        assertNullElement(urlTypeAdapter.serialize(null, URL.class, serializationContext));
    }
    
}
