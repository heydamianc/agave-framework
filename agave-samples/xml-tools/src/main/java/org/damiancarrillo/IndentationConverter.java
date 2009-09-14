package org.damiancarrillo;

import agave.conversion.StringConverter;
import agave.exception.ConversionException;
import java.util.Locale;

public class IndentationConverter implements StringConverter<String> {

    public String convert(String input, Locale locale) throws ConversionException {
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
