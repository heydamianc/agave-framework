package co.cdev.agave.conversion;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import co.cdev.agave.ISO8601DateFormat;

public class DateConverter implements StringConverter<Date> {
    
    @Override
    public Date convert(String input, Locale locale) throws AgaveConversionException {
        try {
            return new ISO8601DateFormat(locale).parse(input);
        } catch (ParseException e) {
            throw new AgaveConversionException(e);
        }
    }

}
