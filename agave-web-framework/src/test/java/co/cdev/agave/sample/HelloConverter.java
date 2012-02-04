package co.cdev.agave.sample;

import co.cdev.agave.conversion.StringParamConverter;
import java.util.Locale;

public class HelloConverter implements StringParamConverter<String> {

    public String convert(String input, Locale locale) {
        return "Booyaka!";
    }

}
