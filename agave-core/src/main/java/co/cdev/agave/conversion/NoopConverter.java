package co.cdev.agave.conversion;

import java.util.Locale;

public class NoopConverter implements StringConverter<String> {

    @Override
    public String convert(String input, Locale locale) throws AgaveConversionException {
        return input;
    }
    
}
