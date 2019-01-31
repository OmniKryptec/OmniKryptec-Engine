package de.omnikryptec.util;

import de.omnikryptec.util.Logger.LogType;

public class LoggedException<T extends Exception> {
    
    private T exception;
    private Logger logger;
    
    public LoggedException(Class<?> clazz, T exc) {
        this(Logger.getLogger(clazz), exc);
    }
    
    public LoggedException(Logger logger, T exc) {
        this.exception = exc;
        this.logger = logger;
    }
    
    public void thrrow() throws T {
        logger.log(LogType.Error, "An exception occured: ");
        throw exception;
    }
    
    public void print() {
        logger.log(LogType.Error, "An exception occured: ");
        exception.printStackTrace();
    }
    
}
