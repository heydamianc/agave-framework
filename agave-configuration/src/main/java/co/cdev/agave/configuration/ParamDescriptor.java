package co.cdev.agave.configuration;

import java.io.Serializable;

import co.cdev.agave.conversion.StringParamConverter;

public interface ParamDescriptor extends Serializable {
    
    public Class<? extends StringParamConverter<?>> getConverter();

    public void setConverter(Class<? extends StringParamConverter<?>> converter);

    public String getName();

    public void setName(String name);

    public Class<?> getType();

    public void setType(Class<?> type);
}
