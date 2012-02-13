package co.cdev.agave.conversion;

import java.util.Locale;

public class CharacterConverter implements StringConverter<Character> {

    @Override
    public Character convert(String input, Locale locale) throws AgaveConversionException {
        Character value = null;
        if (input != null && !"".equals(input)) {
            try {
                value = (char)Character.codePointAt(input, 0);
            } catch (Exception ex) {
                throw new AgaveConversionException("Could not convert " + input + " to a Character object", ex.getCause());
            }
        }
        return value;
    }

}
