package co.cdev.agave.conversion;

import java.lang.reflect.Method;

public class AgaveConversionException extends Exception {

    private static final long serialVersionUID = 1L;

    public AgaveConversionException() {
        super();
    }

    public AgaveConversionException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public AgaveConversionException(String message) {
        super(message);
    }

    public AgaveConversionException(Throwable rootCause) {
        super(rootCause);
    }
    
    @SuppressWarnings("rawtypes")
	public AgaveConversionException(Method method, Class<? extends ParamConverter> converterClass) {
        this(converterClass.getName() + " is an unsupported converter for " 
                + method.getDeclaringClass() + "#" + method.getName() + "()");
    }

}
