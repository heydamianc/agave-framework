package co.cdev.agave.conversion;

import java.util.Locale;

public class ByteConverter implements StringConverter<Byte> {

    @Override
    public Byte convert(String input, Locale locale) throws AgaveConversionException {
        Byte value = null;
        if (input != null && !"".equals(input)) {
            try {
                value = Byte.parseByte(input);
            } catch (NumberFormatException ex) {
                throw new AgaveConversionException("Could not convert " + input + " into a Byte object", ex.getCause());
            }
        }
        return value;
    }

}
