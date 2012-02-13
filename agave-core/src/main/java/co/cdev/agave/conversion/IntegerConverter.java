package co.cdev.agave.conversion;

import java.util.Locale;

public class IntegerConverter implements StringConverter<Integer> {

    @Override
    public Integer convert(String input, Locale locale) throws AgaveConversionException {
        Integer value = null;
        if (input != null && !"".equals(input)) {
            try {
                value = Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                throw new AgaveConversionException("Could not convert " + input + " to a Integer object", ex.getCause());
            }
        }
        return value;
    }

}
