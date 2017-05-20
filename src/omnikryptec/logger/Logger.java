package omnikryptec.logger;

import java.io.InputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
    
    public static enum LogLevel {
        FINEST  (false, 6),
        FINER   (false, 5),
        FINE    (false, 4),
        INFO    (false, 3),
        INPUT   (false, 2),
        COMMAND (false, 1),
        WARNING (true, 0),
        ERROR   (true, -1);

        private final boolean isBad;
        /**
         * The higher the level the less important is this LogLevel
         */
        private final int level;

        private LogLevel(boolean isBad, int level) {
            this.isBad = isBad;
            this.level = level;
        }

        public boolean isBad() {
            return isBad;
        }
        
        public int getLevel() {
            return level;
        }
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
    
    public static LogEntry log(Object message, LogLevel level) {
        return log(message, level, level.isBad());
    }

    public static LogEntry log(Object message, LogLevel level, boolean error) {
        return log(message, level, error, true);
    }

    public static LogEntry log(Object message, LogLevel level, boolean error, boolean newLine) {
        Instant instant = Instant.now();
        LogEntry logEntry = null;
        if(error) {
            logEntry = NEWSYSERR.getLogEntry(message, instant);
            if(logEntry.getException() == null) {
                logEntry.setException(new Exception());
            }
        } else {
            logEntry = NEWSYSOUT.getLogEntry(message, instant);
        }
        logEntry.setLevel(level);
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
                if(logEntry.getLevel().isBad) {
                    stream = NEWSYSERR;
                } else {
                    stream = NEWSYSOUT;
                }
                LOG.add(logEntry);
                if(logEntry.getLevel() == LogLevel.COMMAND && logEntry.getLogEntry() != null) {
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
