package co.cdev.agave.samples;

import co.cdev.agave.conversion.AgaveConversionException;
import co.cdev.agave.conversion.StringParamConverter;
import java.util.Locale;

public class IndentationParamConverter implements StringParamConverter<String> {

    @Override
    public String convert(String input, Locale locale) throws AgaveConversionException {
        String indentation = null;
        
        if ("1s".equals(input)) {
            indentation = " ";
        } else if ("2s".equals(input)) {
            indentation = "  ";
        } else if ("4s".equals(input)) {
            indentation = "    ";
        } else if ("8s".equals(input)) {
            indentation = "        ";
        } else if ("1t".equals(input)) {
            indentation = "\t";
        }
        
        return indentation;
    }

}
