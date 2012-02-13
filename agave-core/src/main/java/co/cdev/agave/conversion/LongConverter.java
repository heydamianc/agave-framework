package co.cdev.agave.conversion;

import java.util.Locale;

public class LongConverter implements StringConverter<Long> {

    @Override
    public Long convert(String input, Locale locale) throws AgaveConversionException {
        Long value = null;
        if (input != null && !"".equals(input)) {
            try {
                value = Long.parseLong(input);
            } catch (NumberFormatException ex) {
                throw new AgaveConversionException("Could not convert " + input + " to a Long object", ex.getCause());
            }
        }
        return value;
    }

}