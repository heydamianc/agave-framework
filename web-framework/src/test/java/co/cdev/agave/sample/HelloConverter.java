package co.cdev.agave.sample;

import co.cdev.agave.conversion.StringConverter;
import java.util.Locale;

public class HelloConverter implements StringConverter<String> {

    public String convert(String input, Locale locale) {
        return "Booyaka!";
    }

}
