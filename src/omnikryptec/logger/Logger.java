package omnikryptec.logger;

import java.awt.Color;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import omnikryptec.logger.LogEntry.LogLevel;

/**
 * 
 * @author Panzer1119 & pcfreak9000
 */
public class Logger {

    protected static final PrintStream OLDSYSOUT = System.out;
    protected static final PrintStream OLDSYSERR = System.err;
    protected static final InputStream OLDSYSIN = System.in;
    public static final SystemOutputStream NEWSYSOUT = new SystemOutputStream(OLDSYSOUT, false);
    public static final SystemOutputStream NEWSYSERR = new SystemOutputStream(OLDSYSERR, true);
    public static final SystemInputStream NEWSYSIN = new SystemInputStream(OLDSYSIN);
    
    public static final ArrayList<LogEntry> LOG = new ArrayList<>();
    private static ExecutorService THREADPOOL = null;

    public static String DATETIMEFORMAT = LogEntry.STANDARD_DATETIMEFORMAT;
    public static String LOGENTRYFORMAT = LogEntry.STANDARD_LOGENTRYFORMAT;
    private static boolean debugMode = false;
    private static boolean enabled = false;
    public static LogLevel minimumLogLevel = LogLevel.INFO;
    
    static {
        initializeThreadPool();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                THREADPOOL.shutdown();
                THREADPOOL.awaitTermination(1, TimeUnit.MINUTES);
            } catch (Exception ex) {
            }
        }));
        Commands.initialize();
    }
    
    public static Class setDebugMode(boolean debugMode) {
    	Logger.debugMode = debugMode;
        return Logger.class;
    }
    
    public static boolean isDebugMode() {
    	return debugMode;
    }
    
    public static boolean enableLoggerRedirection(boolean enable) {
        if(enable && !enabled) {
            System.setOut(NEWSYSOUT);
            System.setErr(NEWSYSERR);
            System.setIn(NEWSYSIN.getNewInputStream());
            NEWSYSIN.setActive(true);
            enabled = true;
            return true;
        } else if(!enable && enabled) {
            System.setOut(OLDSYSOUT);
            System.setErr(OLDSYSERR);
            NEWSYSIN.setActive(false);
            System.setIn(OLDSYSIN);
            enabled = false;
            return true;
        } else {
            return false;
        }
    }
    
    public static LogEntry logErr(Object message, Exception ex) {
        LogEntry logEntry = NEWSYSERR.getLogEntry(message, Instant.now()).setException(ex);
        log(logEntry);
        return logEntry;
    }

    public static LogEntry log(Object message) {
        return log(message, LogLevel.INFO);
    }
    
    public static LogEntry log(Object message, LogLevel logLevel) {
        return log(message, logLevel, logLevel.isBad());
    }

    public static LogEntry log(Object message, LogLevel logLevel, boolean error) {
        return log(message, logLevel, error, true);
    }

    public static LogEntry log(Object message, LogLevel logLevel, boolean error, boolean newLine) {
        Instant instant = Instant.now();
        LogEntry logEntry = null;
        if(error) {
            logEntry = NEWSYSERR.getLogEntry(message, instant);
        } else {
            logEntry = NEWSYSOUT.getLogEntry(message, instant);
        }
        logEntry.setLogLevel(logLevel);
        logEntry.setNewLine(newLine);
        log(logEntry);
        return logEntry;
    }
    
    public static void log(LogEntry logEntry) {
        addLogEntry(logEntry);
    }
    
    private static void addLogEntry(LogEntry logEntry) {
        if(THREADPOOL.isShutdown() || THREADPOOL.isTerminated()) {
            initializeThreadPool();
        }
        THREADPOOL.submit(() -> {
            try {
                SystemOutputStream stream = null;
                if(logEntry.getLogLevel().isBad()) {
                    stream = NEWSYSERR;
                } else {
                    stream = NEWSYSOUT;
                }
                LOG.add(logEntry);
                if(logEntry.getLogLevel() == LogLevel.COMMAND && logEntry.getLogEntry() != null) {
                    boolean found = Command.runCommand(logEntry.getLogEntry().toString().substring(1));
                    if(!found) {
                        LogEntry logEntryError = NEWSYSERR.getLogEntry("Command not found!", Instant.now());
                        logEntryError.setLogEntryFormat(LogEntryFormatter.toggleFormat(logEntryError.getLogEntryFormat(), true, false, true, true, true, true));
                        Logger.log(logEntryError);
                    }
                } else {
                    stream.log(logEntry);
                }
            } catch (Exception ex) {
            	ex.printStackTrace(OLDSYSERR);
            }
        });
    }
    
    private static void initializeThreadPool() {
        THREADPOOL = Executors.newFixedThreadPool(1);
    }

    public static boolean isLoggerRedirectionEnabled() {
        return enabled;
    }

    public static LogLevel getMinimumLogLevel() {
        return minimumLogLevel;
    }

    public static void setMinimumLogLevel(LogLevel minimumLogLevel) {
        Logger.minimumLogLevel = minimumLogLevel;
    }
    
    public static boolean isMinimumLogLevel(LogLevel logLevel) {
        return logLevel.getLevel() <= minimumLogLevel.getLevel();
    }
    
    public static void setDateTimeFormat(String dateTimeFormat) {
        DATETIMEFORMAT = dateTimeFormat;
        /*
        for(LogEntry logEntry : LOG) {
            logEntry.setDateTimeFormat(dateTimeFormat);
        }
        */
    }
    
    public static void setLogEntryFormat(String logEntryFormat) {
        LOGENTRYFORMAT = logEntryFormat;
        /*
        for(LogEntry logEntry : LOG) {
            logEntry.setLogEntryFormat(logEntryFormat);
        }
        */
    }

}
