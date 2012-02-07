package co.cdev.agave.configuration;

import java.io.Serializable;

import co.cdev.agave.conversion.StringParamConverter;

public interface ParamDescriptor extends Serializable {
    
    public Class<?> getParameterClass();

    public String getName();

    public Class<? extends StringParamConverter<?>> getConverterClass();
    
}
