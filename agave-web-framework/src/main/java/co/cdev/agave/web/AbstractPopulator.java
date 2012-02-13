package co.cdev.agave.web;

import co.cdev.agave.conversion.BooleanConverter;
import co.cdev.agave.conversion.ByteConverter;
import co.cdev.agave.conversion.CharacterConverter;
import co.cdev.agave.conversion.DoubleConverter;
import co.cdev.agave.conversion.FloatConverter;
import co.cdev.agave.conversion.IntegerConverter;
import co.cdev.agave.conversion.LongConverter;
import co.cdev.agave.conversion.ParamConverter;
import java.util.Locale;

public abstract class AbstractPopulator {
    
    protected Locale locale;

    protected AbstractPopulator(Locale locale) {
        this.locale = locale;
    }
    
    protected ParamConverter<?, ?> determineMostAppropriateConverter(Class<?> parameterType) {
        ParamConverter<?, ?> converter = null;
        
        if (parameterType.isAssignableFrom(Boolean.class) || parameterType.isAssignableFrom(boolean.class)) {
            converter = new BooleanConverter();
        } else if (parameterType.isAssignableFrom(Byte.class) || parameterType.isAssignableFrom(byte.class)) {
            converter = new ByteConverter();
        } else if (parameterType.isAssignableFrom(Character.class) || parameterType.isAssignableFrom(char.class)) {
            converter = new CharacterConverter();
        } else if (parameterType.isAssignableFrom(Double.class) || parameterType.isAssignableFrom(double.class)) {
            converter = new DoubleConverter();
        } else if (parameterType.isAssignableFrom(Float.class) || parameterType.isAssignableFrom(float.class)) {
            converter = new FloatConverter();
        } else if (parameterType.isAssignableFrom(Integer.class) || parameterType.isAssignableFrom(int.class)) {
            converter = new IntegerConverter();
        } else if (parameterType.isAssignableFrom(Long.class) || parameterType.isAssignableFrom(long.class)) {
            converter = new LongConverter();
        }
        
        return converter;
    }
    
}
