package co.cdev.agave.conversion;

import java.util.Date;

public final class Converters {

    public static Class<? extends  StringConverter<?>> getMostAppropriateConverterClassFor(Class<?> inputClass) {
        Class<? extends  StringConverter<?>> converterClass = null; 
        
        if (inputClass.isAssignableFrom(Boolean.class) || inputClass.isAssignableFrom(boolean.class)) {
            converterClass = BooleanConverter.class;
        } else if (inputClass.isAssignableFrom(Byte.class) || inputClass.isAssignableFrom(byte.class)) {
            converterClass = ByteConverter.class;
        } else if (inputClass.isAssignableFrom(Character.class) || inputClass.isAssignableFrom(char.class)) {
            converterClass = CharacterConverter.class;
        } else if (inputClass.isAssignableFrom(Double.class) || inputClass.isAssignableFrom(double.class)) {
            converterClass = DoubleConverter.class;
        } else if (inputClass.isAssignableFrom(Float.class) || inputClass.isAssignableFrom(float.class)) {
            converterClass = FloatConverter.class;
        } else if (inputClass.isAssignableFrom(Integer.class) || inputClass.isAssignableFrom(int.class)) {
            converterClass = IntegerConverter.class;
        } else if (inputClass.isAssignableFrom(Long.class) || inputClass.isAssignableFrom(long.class)) {
            converterClass = LongConverter.class;
        } else if (inputClass.isAssignableFrom(Date.class)) {
            converterClass = DateConverter.class;
        }
        
        return converterClass;
    }
    
    public static ParamConverter<?, ?> getMostAppropriateFor(Class<?> inputClass) {
        ParamConverter<?, ?> converter = null;
        
        if (inputClass.isAssignableFrom(Boolean.class) || inputClass.isAssignableFrom(boolean.class)) {
            converter = new BooleanConverter();
        } else if (inputClass.isAssignableFrom(Byte.class) || inputClass.isAssignableFrom(byte.class)) {
            converter = new ByteConverter();
        } else if (inputClass.isAssignableFrom(Character.class) || inputClass.isAssignableFrom(char.class)) {
            converter = new CharacterConverter();
        } else if (inputClass.isAssignableFrom(Double.class) || inputClass.isAssignableFrom(double.class)) {
            converter = new DoubleConverter();
        } else if (inputClass.isAssignableFrom(Float.class) || inputClass.isAssignableFrom(float.class)) {
            converter = new FloatConverter();
        } else if (inputClass.isAssignableFrom(Integer.class) || inputClass.isAssignableFrom(int.class)) {
            converter = new IntegerConverter();
        } else if (inputClass.isAssignableFrom(Long.class) || inputClass.isAssignableFrom(long.class)) {
            converter = new LongConverter();
        } else if (inputClass.isAssignableFrom(Date.class)) {
            converter = new DateConverter();
        }
        
        return converter;
    }
    
}
