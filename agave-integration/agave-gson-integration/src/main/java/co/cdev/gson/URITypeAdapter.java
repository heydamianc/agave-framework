package co.cdev.gson;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Safely converts a URI string into a URI.  There are a few exceptions that arise from
 * trying to create a URI from an invalid string, and this will handle those cases 
 * gracefully.
 * 
 * @author ddc
 */
public class URITypeAdapter implements JsonSerializer<URI>, JsonDeserializer<URI> {

    private static final Logger LOGGER = Logger.getLogger(URITypeAdapter.class.getName());
    
    @Override
    public JsonElement serialize(URI uri, Type targetType, JsonSerializationContext context) {
        JsonElement serializedURI = JsonNull.INSTANCE;
        
        if (targetType.equals(URI.class) && uri != null) {
            serializedURI = new JsonPrimitive(uri.toString());
        }
        
        return serializedURI;
    }
    
    @Override
    public URI deserialize(JsonElement json, Type targetType, JsonDeserializationContext context) throws JsonParseException {
        URI uri = null;
        
        if (targetType.equals(URI.class) && !json.equals(JsonNull.INSTANCE)) {
            String uriString = json.getAsString();
            
            try {
                uri = new URI(uriString);
            } catch (URISyntaxException e) {
                LOGGER.log(Level.WARNING, "Unable to deserialize URI from: %s", uriString);
            }
        }
        
        return uri;
    }

}
