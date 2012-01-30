package co.cdev.agave.exception;

public class AgaveConfigurationException extends Exception {

    private static final long serialVersionUID = 1L;

    public AgaveConfigurationException() {
        super();
    }

    public AgaveConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AgaveConfigurationException(String message) {
        super(message);
    }

    public AgaveConfigurationException(Throwable cause) {
        super(cause);
    }

}
