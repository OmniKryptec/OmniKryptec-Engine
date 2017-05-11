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

    public static final String STANDARD_DATETIMEFORMAT = "dd.MM.yyyy HH:mm:ss.SSS";

    public static final PrintStream OLDSYSOUT = System.out;
    public static final PrintStream OLDSYSERR = System.err;
    public static final SystemOutputStream NEWSYSOUT = new SystemOutputStream(OLDSYSOUT, false);
    public static final SystemOutputStream NEWSYSERR = new SystemOutputStream(OLDSYSERR, true);
    
    public static final ArrayList<LogEntry> LOG = new ArrayList<>();
    private static final ExecutorService THREADPOOL = Executors.newFixedThreadPool(1);

    private static boolean enabled = false;

    public static enum ErrorLevel {
        FINEST(false),
        FINER(false),
        FINE(false),
        INFO(false),
        INPUT(false),
        COMMAND(false),
        WARNING(true),
        ERROR(true);

        private final boolean isBad;

        private ErrorLevel(boolean bad) {
            isBad = bad;
        }

        public boolean isBad() {
            return isBad;
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
        log(message, ErrorLevel.INFO);
    }
    
    public static void log(Object message, ErrorLevel level) {
        log(message, level, level.isBad());
    }

    public static void log(Object message, ErrorLevel level, boolean error) {
        log(message, level, error, true);
    }

    public static void log(Object message, ErrorLevel level, boolean error, boolean newLine) {
        Instant instant = Instant.now();
        LogEntry logentry = null;
        if(error) {
            logentry = NEWSYSERR.getLogEntry(message, instant);
        } else {
            logentry = NEWSYSOUT.getLogEntry(message, instant);
        }
        logentry.setLevel(level);
        logentry.setNewLine(newLine);
        log(logentry);
    }
    
    public static void log(LogEntry logentry) {
        addLogEntry(logentry);
    }
    
    private static void addLogEntry(LogEntry logentry) {
        THREADPOOL.submit(() -> {
            try {
                SystemOutputStream stream = null;
                if(logentry.getLevel().isBad) {
                    stream = NEWSYSERR;
                } else {
                    stream = NEWSYSOUT;
                }
                LOG.add(logentry);
                stream.log(logentry);
            } catch (Exception ex) {
            }
        });
    }

    public static boolean isLoggerRedirectionEnabled() {
        return enabled;
    }

}
