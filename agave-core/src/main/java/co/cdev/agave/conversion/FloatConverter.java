package co.cdev.agave.conversion;

import java.util.Locale;

public class FloatConverter implements StringConverter<Float> {

    @Override
    public Float convert(String input, Locale locale) throws AgaveConversionException {
        Float value = null;
        if (input != null && !"".equals(input)) {
            try {
                value = Float.parseFloat(input);
            } catch (NumberFormatException ex) {
                throw new AgaveConversionException("Could not convert " + input + " to a Float object", ex.getCause());
            }
        }
        return value;
    }

}
