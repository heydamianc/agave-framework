package co.cdev.gson;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
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
 * Safely converts a Date string into a Date.  The date should be formatted like:
 * 
 *   2001-07-04T12:08:56.235-0700
 * 
 * @author ddc
 */
public class ISO8601DateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

    public static final String ISO_8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    
    private static final Logger LOGGER = Logger.getLogger(ISO8601DateTypeAdapter.class.getName());
    
    @Override
    public JsonElement serialize(Date date, Type targetType, JsonSerializationContext context) {
        JsonElement serializedDate = JsonNull.INSTANCE;
        
        if (date != null && targetType.equals(Date.class)) {
            DateFormat dateFormat = new SimpleDateFormat(ISO_8601_DATE_FORMAT);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            serializedDate = new JsonPrimitive(dateFormat.format(date));
        }
        
        return serializedDate;
    }
        
    @Override
    public Date deserialize(JsonElement json, Type targetType, JsonDeserializationContext context) throws JsonParseException {
        Date date = null;
        
        if (targetType.equals(Date.class) && !JsonNull.INSTANCE.equals(json)) {
            DateFormat dateFormat = new SimpleDateFormat(ISO_8601_DATE_FORMAT);
            String dateString = json.getAsString();
            
            try {
                date = dateFormat.parse(dateString);
            } catch (ParseException e) {
                LOGGER.log(Level.WARNING, String.format("Unable to deserialize date from: %s", dateString));
            }
        }
        
        return date;
    }
    
}
