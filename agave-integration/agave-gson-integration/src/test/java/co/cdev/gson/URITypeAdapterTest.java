package co.cdev.gson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import co.cdev.gson.URITypeAdapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;


public class URITypeAdapterTest extends AbstractHandlerTest {

    private URI uri;
    private URITypeAdapter uriTypeAdapter;
    
    @Before
    public void setUp() throws Exception {
        uri = new URI("git://github.com/damiancarrillo/agave-web-framework.git");
        uriTypeAdapter = new URITypeAdapter();
    }
    
    @Test
    public void testDeserialize() throws Exception {
        JsonElement element = new JsonPrimitive("git://github.com/damiancarrillo/agave-web-framework.git");
        assertEquals(uri, uriTypeAdapter.deserialize(element, URI.class, deserializationContext));
    }
    
    @Test
    public void testDeserialize_withNullURI() throws Exception {
        assertNull(uriTypeAdapter.deserialize(JsonNull.INSTANCE, URI.class, deserializationContext));
    }
    
    @Test
    public void testSerialize() throws Exception {
        JsonElement element = uriTypeAdapter.serialize(uri, URI.class, serializationContext);
        assertEquals(new JsonPrimitive("git://github.com/damiancarrillo/agave-web-framework.git"), element);
    }
    
    @Test
    public void testSerialize_withNull() throws Exception {
        assertNullElement(uriTypeAdapter.serialize(null, URI.class, serializationContext));
    }
    
}
