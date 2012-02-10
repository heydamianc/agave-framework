package co.cdev.gson;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
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
 * Safely converts a URL string into a URL.  There are a few exceptions that arise from
 * trying to create a URL from an invalid string, and this will handle those cases 
 * gracefully.
 * 
 * @author ddc
 */
public class URLTypeAdapter implements JsonSerializer<URL>, JsonDeserializer<URL> {
    
    private static final Logger LOGGER = Logger.getLogger(URLTypeAdapter.class.getName());
    
    @Override
    public JsonElement serialize(URL url, Type targetType, JsonSerializationContext context) {
        JsonElement serializedURL = JsonNull.INSTANCE;
        
        if (targetType.equals(URL.class) && url != null) {
            serializedURL = new JsonPrimitive(url.toString());
        }
        
        return serializedURL;
    }
    
    @Override
    public URL deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        URL url = null;
        
        if (typeOfT.equals(URL.class) && !json.equals(JsonNull.INSTANCE)) {
            String urlString = json.getAsString();
            
            try {
              url = new URL(urlString);
            } catch (MalformedURLException e) {
                LOGGER.log(Level.WARNING, "Unable to deserialize URI from: %s", urlString);
            }
        }
        
        return url;
    }

}
