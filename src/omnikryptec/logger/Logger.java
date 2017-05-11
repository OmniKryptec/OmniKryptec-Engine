package omnikryptec.logger;

import java.io.PrintStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 * @author Panzer1119 & pcfreak9000
 */
public class Logger {

    public static final PrintStream OLDSYSOUT = System.out;
    public static final PrintStream OLDSYSERR = System.err;
    public static final SystemOutputStream NEWSYSOUT = new SystemOutputStream(OLDSYSOUT, false);
    public static final SystemOutputStream NEWSYSERR = new SystemOutputStream(OLDSYSERR, true);
    
    public static final ArrayList<LogEntry> LOG = new ArrayList<>();
    private static final ExecutorService THREADPOOL = Executors.newFixedThreadPool(1);

    private static boolean enabled = false;
    public static LogLevel minimumLogLevel = LogLevel.INFO;

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

    public static boolean enableLoggerRedirection(boolean enable) {
        if(enable && !enabled) {
            System.setOut(NEWSYSOUT);
            System.setErr(NEWSYSERR);
            enabled = true;
            return true;
        } else if(!enable && enabled) {
            System.setOut(OLDSYSOUT);
            System.setErr(OLDSYSERR);
            enabled = false;
            return true;
        } else {
            return false;
        }
    }
    
    public static void logErr(Object message, Exception ex) {
        log(NEWSYSERR.getLogEntry(message, Instant.now()).setException(ex));
    }

    public static void log(Object message) {
        log(message, LogLevel.INFO);
    }
    
    public static void log(Object message, LogLevel level) {
        log(message, level, level.isBad());
    }

    public static void log(Object message, LogLevel level, boolean error) {
        log(message, level, error, true);
    }

    public static void log(Object message, LogLevel level, boolean error, boolean newLine) {
        Instant instant = Instant.now();
        LogEntry logEntry = null;
        if(error) {
            logEntry = NEWSYSERR.getLogEntry(message, instant);
        } else {
            logEntry = NEWSYSOUT.getLogEntry(message, instant);
        }
        logEntry.setLevel(level);
        logEntry.setNewLine(newLine);
        log(logEntry);
    }
    
    public static void log(LogEntry logEntry) {
        addLogEntry(logEntry);
    }
    
    private static void addLogEntry(LogEntry logEntry) {
        THREADPOOL.submit(() -> {
            try {
                SystemOutputStream stream = null;
                if(logEntry.getLevel().isBad) {
                    stream = NEWSYSERR;
                } else {
                    stream = NEWSYSOUT;
                }
                LOG.add(logEntry);
                stream.log(logEntry);
            } catch (Exception ex) {
            }
        });
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
        NEWSYSERR.setDateTimeFormat(dateTimeFormat);
        NEWSYSOUT.setDateTimeFormat(dateTimeFormat);
        for(LogEntry logEntry : LOG) {
            logEntry.setDateTimeFormat(dateTimeFormat);
        }
    }
    
    public static void setLogEntryFormat(String logEntryFormat) {
        NEWSYSERR.setLogEntryFormat(logEntryFormat);
        NEWSYSOUT.setLogEntryFormat(logEntryFormat);
        for(LogEntry logEntry : LOG) {
            logEntry.setLogEntryFormat(logEntryFormat);
        }
    }

}
