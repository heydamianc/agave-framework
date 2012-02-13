package co.cdev.agave.conversion;

import java.util.Locale;

public class ShortConverter implements StringConverter<Short> {

    @Override
    public Short convert(String input, Locale locale) throws AgaveConversionException {
        Short value = null;
        if (input != null && !"".equals(input)) {
            try {
                value = Short.parseShort(input);
            } catch (NumberFormatException ex) {
                throw new AgaveConversionException("Could not convert " + input + " to a Short object", ex.getCause());
            }
        }
        return value;
    }

}
