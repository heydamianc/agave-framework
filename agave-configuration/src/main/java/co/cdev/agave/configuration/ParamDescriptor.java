package co.cdev.agave.configuration;

import java.io.Serializable;

import co.cdev.agave.conversion.StringConverter;

public interface ParamDescriptor extends Serializable {
    
    public Class<?> getParameterClass();

    public String getName();

    public Class<? extends StringConverter<?>> getConverterClass();
    
}
