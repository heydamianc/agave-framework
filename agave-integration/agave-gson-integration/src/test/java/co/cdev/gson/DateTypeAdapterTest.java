package co.cdev.gson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

public class DateTypeAdapterTest extends AbstractHandlerTest {

    private DateFormat dateFormat;
    private ISO8601DateTypeAdapter dateTypeAdapter;
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        dateFormat = new SimpleDateFormat(ISO8601DateTypeAdapter.ISO_8601_DATE_FORMAT);
        dateTypeAdapter = new ISO8601DateTypeAdapter();
    }
    
    @Test
    public void testSerializeDate_ensureUTC() throws Exception {
        Date date = dateFormat.parse("2001-07-04T12:08:56-0700");
        assertEquals(new JsonPrimitive("2001-07-04T19:08:56+0000"), 
                dateTypeAdapter.serialize(date, Date.class, serializationContext));
    }
    
    @Test
    public void testSerializeDate_withNull() throws Exception {
        assertNullElement(dateTypeAdapter.serialize(null, Date.class, serializationContext));
    }
    
    @Test
    public void testSerializeDate_withWrongTargetType() throws Exception {
        Date date = dateFormat.parse("2001-07-04T12:08:56-0700");
        assertNullElement(dateTypeAdapter.serialize(date, String.class, serializationContext));
    }
    
    @Test
    public void testDeserializeDate_ensureUTC() throws Exception {
        Date date = dateTypeAdapter.deserialize(new JsonPrimitive("2001-07-04T19:08:56+0000"), Date.class, deserializationContext);
        assertEquals(dateFormat.parse("2001-07-04T12:08:56-0700"), date);
    }
    
    @Test
    public void testDeserializeDate_withNull() throws Exception {
        assertNull(dateTypeAdapter.deserialize(JsonNull.INSTANCE, Date.class, deserializationContext));
    }
    
    @Test
    public void testDeserializeDate_withWrongTargetType() throws Exception {
        assertNull(dateTypeAdapter.deserialize(new JsonPrimitive("2001-07-04T19:08:56+0000"), String.class, deserializationContext));
    }
    
}
