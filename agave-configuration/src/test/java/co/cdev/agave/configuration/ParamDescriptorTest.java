package co.cdev.agave.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

public class ParamDescriptorTest {

    @Test
    public void testEquals() throws Exception {
        ParamDescriptor a = new ParamDescriptorImpl(String.class, "param", null);
        ParamDescriptor b = new ParamDescriptorImpl(String.class, "param", null);
        
        assertEquals(a, b);
    }
    
    @Test
    public void testSerialize() throws Exception {
        ParamDescriptor a = new ParamDescriptorImpl(String.class, "param", null);
        
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(bout));
        out.writeObject(a);
    }
    
    @Test
    public void testDeserialize() throws Exception {
        ParamDescriptor a = new ParamDescriptorImpl(String.class, "param", null);
        
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(bout));
        out.writeObject(a);
        out.close();
        
        byte[] bytes = bout.toByteArray();
        
        assertTrue(bytes.length > 0);
        
        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(bin));
        ParamDescriptor b = (ParamDescriptor) in.readObject();
        in.close();
        
        assertEquals(a, b);
    }
    
}
