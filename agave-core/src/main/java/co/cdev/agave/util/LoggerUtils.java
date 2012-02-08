package co.cdev.agave.util;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class LoggerUtils {

    private LoggerUtils() {
        
    }
    
    public static void silenceLoggers() {
        Logger logger = LogManager.getLogManager().getLogger("");
        
        if (logger != null) {
            logger.setLevel(Level.OFF);
        }
    }
    
}
