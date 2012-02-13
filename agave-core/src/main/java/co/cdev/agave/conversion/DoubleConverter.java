package co.cdev.agave.conversion;

import java.util.Locale;

public class DoubleConverter implements StringConverter<Double> {

    @Override
    public Double convert(String input, Locale locale) throws AgaveConversionException {
        Double value = null;
        if (input != null && !"".equals(input)) {
            try {
                value = Double.parseDouble(input);
            } catch (NumberFormatException ex) {
                throw new AgaveConversionException("Could not convert " + input + " to a Double object", ex.getCause());
            }
        }
        return value;
    }

}
