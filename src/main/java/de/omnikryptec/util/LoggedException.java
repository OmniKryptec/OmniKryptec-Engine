package de.omnikryptec.util;

import de.omnikryptec.util.Logger.LogType;

public class LoggedException<T extends Exception> {
    
    private T exception;
    private Logger logger;
    
    public LoggedException(final Class<?> clazz, final T exc) {
        this(Logger.getLogger(clazz), exc);
    }
    
    public LoggedException(final Logger logger, final T exc) {
        this.exception = exc;
        this.logger = logger;
    }
    
    public void thrrow() throws T {
        this.logger.log(LogType.Error, "An exception occured: ");
        throw this.exception;
    }
    
    public void print() {
        this.logger.log(LogType.Error, "An exception occured: ");
        this.exception.printStackTrace();
    }
    
}
