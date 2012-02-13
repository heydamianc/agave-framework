package co.cdev.agave.conversion;

import java.util.Locale;

public class BooleanConverter implements StringConverter<Boolean> {

    @Override
    public Boolean convert(String input, Locale locale) throws AgaveConversionException {
        Boolean value = null;
        if (input != null && !"".equals(input)) {
            String parameter = input.toLowerCase();
            if ("true".equals(parameter)
                || "t".equals(parameter)
                || "1".equals(parameter)
                || "on".equals(parameter)
                || "yes".equals(parameter)
                || "y".equals(parameter)) {
                value = Boolean.TRUE;
            } else if ("false".equals(parameter)
                || "f".equals(parameter)
                || "0".equals(parameter)
                || "off".equals(parameter)
                || "no".equals(parameter)
                || "n".endsWith(parameter)) {
                value = Boolean.FALSE;
            } else {
                throw new AgaveConversionException("Could not convert " + input + " into a boolean object");
            }
        }
        return value;
    }

}
