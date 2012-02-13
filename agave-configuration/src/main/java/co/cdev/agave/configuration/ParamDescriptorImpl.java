package co.cdev.agave.configuration;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import co.cdev.agave.conversion.StringConverter;

public class ParamDescriptorImpl implements ParamDescriptor {
    
    private static final long serialVersionUID = 1L;
    
    private final Class<?> paramClass;
    private final String name;
    private final Class<? extends StringConverter<?>> converterClass;
    
    public ParamDescriptorImpl(Class<?> parameterClass, String name, Class<? extends StringConverter<?>> converterClass) {
        this.paramClass = parameterClass;
        this.name = name;
        this.converterClass = converterClass;
    }

    public Class<? extends StringConverter<?>> getConverterClass() {
        return converterClass;
    }

    public String getName() {
        return name;
    }

    public Class<?> getParamClass() {
        return paramClass;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((converterClass == null) ? 0 : converterClass.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((paramClass == null) ? 0 : paramClass.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        ParamDescriptorImpl other = (ParamDescriptorImpl) obj;
        if (converterClass == null) {
            if (other.converterClass != null)
                return false;
        } else if (!converterClass.equals(other.converterClass))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (paramClass == null) {
            if (other.paramClass != null)
                return false;
        } else if (!paramClass.equals(other.paramClass))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder representation = new StringBuilder();
        representation.append("[");
        representation.append("paramClass:").append(paramClass.getName());
        representation.append(",").append("name:").append(name);
        if (converterClass != null) {
            representation.append(",").append("converter:").append(converterClass.getName());
        }
        representation.append("]");
        return representation.toString();
    }
    
    // Serialization
    
    private Object writeReplace() {
        return new SerializationProxy(this);
    }
    
    private void readObject(ObjectInputStream in) throws InvalidObjectException {
        throw new InvalidObjectException("Expected SerializationProxy");
    }
    
    private static class SerializationProxy implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        private final Class<?> paramClass;
        private final String name;
        private final Class<? extends StringConverter<?>> converter;
        
        SerializationProxy(ParamDescriptorImpl paramDescriptor) {
            paramClass = paramDescriptor.getParamClass();
            name = paramDescriptor.getName();
            converter = paramDescriptor.getConverterClass();
        }
        
        private Object readResolve() {
            return new ParamDescriptorImpl(paramClass, name, converter);
        }
    }
    
}
