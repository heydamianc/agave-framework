package co.cdev.agave.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import co.cdev.agave.util.ReflectionUtils;

public class ReflectionUtilsTest {
    
    @Test
    public void testGetField() throws Exception {        
        assertNotNull(ReflectionUtils.getField(Happy.class, "color"));
        assertNotNull(ReflectionUtils.getField(Happy.class, "name"));
        assertNull(ReflectionUtils.getField(Happy.class, "missing"));
    }
    
    @Test
    public void testGetField_withNullArguments() throws Exception {
        assertNull(ReflectionUtils.getField(Happy.class, null));
        assertNull(ReflectionUtils.getField(null, "name"));
    }
    
    @Test
    public void testGetAccessors() throws Exception {
        List<Method> accessors = ReflectionUtils.getAccessors(Happy.class);
        
        assertTrue(accessors.contains(Happy.class.getDeclaredMethod("getColor")));
        assertTrue(accessors.contains(Mood.class.getDeclaredMethod("getName")));
    }
    
    @Test
    public void testGetAccessors_withNullArguments() throws Exception {
        assertTrue(ReflectionUtils.getAccessors(null).isEmpty());
    }
    
    @Test
    public void testGetAccessedField() throws Exception {
        Method getColor = Happy.class.getDeclaredMethod("getColor");
        Field expectedField = ReflectionUtils.getField(Happy.class, "color");
        Field actualField = ReflectionUtils.getAccessedField(getColor);
        
        assertEquals(expectedField, actualField);
        
        Method getName = Mood.class.getDeclaredMethod("getName");
        expectedField = ReflectionUtils.getField(Happy.class, "name");
        actualField = ReflectionUtils.getAccessedField(getName);
        
        assertEquals(expectedField, actualField);
    }
    
    @Test
    public void testGetAccessedField_withNullArgument() throws Exception {
        assertNull(ReflectionUtils.getAccessedField(null));
    }
    
    @Test
    public void testGetMutators() throws Exception {
        List<Method> mutators = ReflectionUtils.getMutators(Happy.class);
        
        assertTrue(mutators.contains(Happy.class.getDeclaredMethod("setColor", String.class)));
        assertTrue(mutators.contains(Mood.class.getDeclaredMethod("setName", String.class)));
    }
    
    @Test
    public void testGetMutators_withNullArguments() throws Exception {
        assertTrue(ReflectionUtils.getMutators(null).isEmpty());
    }
    
    @Test
    public void testGetMutatedField() throws Exception {
        Method setColor = Happy.class.getDeclaredMethod("setColor", String.class);
        Field expectedField = ReflectionUtils.getField(Happy.class, "color");
        Field actualField = ReflectionUtils.getMutatedField(setColor);
        
        assertEquals(expectedField, actualField);
        
        Method setName = Mood.class.getDeclaredMethod("setName", String.class);
        expectedField = ReflectionUtils.getField(Happy.class, "name");
        actualField = ReflectionUtils.getMutatedField(setName);
        
        assertEquals(expectedField, actualField);
    }
    
    @Test
    public void testGetMutatedField_withNullArgument() throws Exception {
        assertNull(ReflectionUtils.getMutatedField(null));
    }
    
    @Test
    public void testCanBeSubstituted() throws Exception {
        assertTrue(ReflectionUtils.canBeSubstituted(int.class, Integer.class));
        assertTrue(ReflectionUtils.canBeSubstituted(Integer.class, int.class));
        
        assertTrue(ReflectionUtils.canBeSubstituted(float.class, Float.class));
        assertTrue(ReflectionUtils.canBeSubstituted(Float.class, float.class));
        
        assertFalse(ReflectionUtils.canBeSubstituted(Float.class, int.class));
    }
    
    @Test
    public void testGetMethod() throws Exception {
        assertNotNull(ReflectionUtils.getMethod(Happy.class, "setExuberanceLevel", int.class));
        assertNotNull(ReflectionUtils.getMethod(Happy.class, "setExuberanceLevel", Integer.class));
    }
    
    @Test
    public void testGetMutator() throws Exception {
        assertNotNull(ReflectionUtils.getMutator(Happy.class, "exuberanceLevel", int.class));
        assertNull(ReflectionUtils.getMutator(Happy.class, "nonExistent", int.class));
    }
    
    @Test
    public void testGetAccessor() throws Exception {
        assertNotNull(ReflectionUtils.getAccessor(Happy.class, "exuberanceLevel"));
        assertNull(ReflectionUtils.getAccessor(Happy.class, "nonExistent"));
    }
    
    @SuppressWarnings("unused")    
    private static class Mood {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
        
    }
    
    @SuppressWarnings("unused")
    private static class Happy extends Mood {
        private String color;
        private int exuberanceLevel;

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public int getExuberanceLevel() {
            return exuberanceLevel;
        }

        public void setExuberanceLevel(int exuberanceLevel) {
            this.exuberanceLevel = exuberanceLevel;
        }
    }
    
}
