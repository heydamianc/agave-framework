package co.cdev.gson;

import static org.junit.Assert.assertEquals;

import org.jmock.Mockery;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonSerializationContext;

public class AbstractHandlerTest {

    protected Mockery mockery = new Mockery();
    protected JsonSerializationContext serializationContext;
    protected JsonDeserializationContext deserializationContext;
    
    public void setUp() throws Exception {
        serializationContext = mockery.mock(JsonSerializationContext.class);
        deserializationContext = mockery.mock(JsonDeserializationContext.class);
    }
    
    protected void assertNullElement(JsonElement element) {
        assertEquals(JsonNull.INSTANCE, element);
    }
    
}
