package co.cdev.agave.web;

import java.net.URI;

import co.cdev.agave.configuration.HandlerDescriptor;

public class DestinationException extends AgaveWebException {

    private static final long serialVersionUID = 1L;

    public DestinationException() {
        this("", null);
    }
    
    public DestinationException(String message) {
        this(message, null);
    }

    public DestinationException(Throwable rootCause) {
        this("", rootCause);
    }

    public DestinationException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public DestinationException(URI destination, HandlerDescriptor handlerDescriptor, Throwable rootCause) {
        this(String.format("Invalid destination (%s) returned from \"%s\"",
                destination.toString(), handlerDescriptor.getHandlerMethod().toString()), rootCause);
    }

    public DestinationException(URI destination, HandlerDescriptor handlerDescriptor) {
        this(String.format("Invalid destination (%s) returned from \"%s\"",
                destination.toString(), handlerDescriptor.getHandlerMethod().toString()));
    }

    public DestinationException(Destination destination, HandlerDescriptor handlerDescriptor, Throwable rootCause) {
        this(String.format("Invalid destination (%s) returned from \"%s\"",
                destination.toString(), handlerDescriptor.getHandlerMethod().toString()), rootCause);
    }

    public DestinationException(Destination destination, HandlerDescriptor handlerDescriptor) {
        this(String.format("Invalid destination (%s) returned from \"%s\"",
                destination.toString(), handlerDescriptor.getHandlerMethod().toString()));
    }
    
}
