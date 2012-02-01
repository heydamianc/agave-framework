package co.cdev.agave.configuration;

import co.cdev.agave.conversion.StringParamConverter;

public class ParamDescriptorImpl implements ParamDescriptor {
    
    private static final long serialVersionUID = 1L;
    
    private Class<?> type;
    private String name;
    private Class<? extends StringParamConverter<?>> converter;
    
    public ParamDescriptorImpl(Class<?> type, String name) {
        this.type = type;
        this.name = name;
    }

    public Class<? extends StringParamConverter<?>> getConverter() {
        return converter;
    }

    public void setConverter(Class<? extends StringParamConverter<?>> converter) {
        this.converter = converter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder representation = new StringBuilder();
        representation.append("[");
        representation.append("type:").append(type.getName());
        representation.append(",").append("name:").append(name);
        if (converter != null) {
            representation.append(",").append("converter:").append(converter.getName());
        }
        representation.append("]");
        return representation.toString();
    }
    
}
