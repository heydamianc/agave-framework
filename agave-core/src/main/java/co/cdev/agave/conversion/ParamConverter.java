package co.cdev.agave.conversion;

import java.util.Locale;

public interface ParamConverter<InputT, OutputT> {
    
    public OutputT convert(InputT input, Locale locale) throws AgaveConversionException;
    
}
