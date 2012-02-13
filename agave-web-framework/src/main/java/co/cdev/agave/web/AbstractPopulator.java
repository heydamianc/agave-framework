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
    
    protected ParamConverter<?, ?> determineMostAppropriateConverter(Class<?> paramClass) {
        ParamConverter<?, ?> converter = null;
        
        if (paramClass.isAssignableFrom(Boolean.class) || paramClass.isAssignableFrom(boolean.class)) {
            converter = new BooleanConverter();
        } else if (paramClass.isAssignableFrom(Byte.class) || paramClass.isAssignableFrom(byte.class)) {
            converter = new ByteConverter();
        } else if (paramClass.isAssignableFrom(Character.class) || paramClass.isAssignableFrom(char.class)) {
            converter = new CharacterConverter();
        } else if (paramClass.isAssignableFrom(Double.class) || paramClass.isAssignableFrom(double.class)) {
            converter = new DoubleConverter();
        } else if (paramClass.isAssignableFrom(Float.class) || paramClass.isAssignableFrom(float.class)) {
            converter = new FloatConverter();
        } else if (paramClass.isAssignableFrom(Integer.class) || paramClass.isAssignableFrom(int.class)) {
            converter = new IntegerConverter();
        } else if (paramClass.isAssignableFrom(Long.class) || paramClass.isAssignableFrom(long.class)) {
            converter = new LongConverter();
        }
        
        return converter;
    }
    
}
