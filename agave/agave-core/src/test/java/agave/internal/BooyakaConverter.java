package agave.internal;

import agave.conversion.StringConverter;

public class BooyakaConverter implements StringConverter<String> {

    public String convert(String input) {
        return "Booyaka!";
    }

}