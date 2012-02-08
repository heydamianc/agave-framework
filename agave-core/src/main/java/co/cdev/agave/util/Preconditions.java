package co.cdev.agave.util;

public final class Preconditions {

    private Preconditions() {}
    
    public static <T> void nonNull(T argument, String name) {
        if (argument == null) {
            String message = String.format("Failed precondition: expected non-null %s argument", name);
            throw new NullPointerException(message);
        }
    }
    
}
