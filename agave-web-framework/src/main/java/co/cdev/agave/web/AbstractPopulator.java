package co.cdev.agave.web;

import co.cdev.agave.conversion.BooleanParamConverter;
import co.cdev.agave.conversion.ByteParamConverter;
import co.cdev.agave.conversion.CharacterParamConverter;
import co.cdev.agave.conversion.DoubleParamConverter;
import co.cdev.agave.conversion.FloatParamConverter;
import co.cdev.agave.conversion.IntegerParamConverter;
import co.cdev.agave.conversion.LongParamConverter;
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
            converter = new BooleanParamConverter();
        } else if (parameterType.isAssignableFrom(Byte.class) || parameterType.isAssignableFrom(byte.class)) {
            converter = new ByteParamConverter();
        } else if (parameterType.isAssignableFrom(Character.class) || parameterType.isAssignableFrom(char.class)) {
            converter = new CharacterParamConverter();
        } else if (parameterType.isAssignableFrom(Double.class) || parameterType.isAssignableFrom(double.class)) {
            converter = new DoubleParamConverter();
        } else if (parameterType.isAssignableFrom(Float.class) || parameterType.isAssignableFrom(float.class)) {
            converter = new FloatParamConverter();
        } else if (parameterType.isAssignableFrom(Integer.class) || parameterType.isAssignableFrom(int.class)) {
            converter = new IntegerParamConverter();
        } else if (parameterType.isAssignableFrom(Long.class) || parameterType.isAssignableFrom(long.class)) {
            converter = new LongParamConverter();
        }
        
        return converter;
    }
    
}
