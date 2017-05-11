package omnikryptec.logger;

import java.io.PrintStream;
import java.time.Instant;
import java.util.ArrayList;

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

    public static void log(Object message, ErrorLevel level) {
        log(message, level, level.isBad());
    }

    public static void log(Object message, ErrorLevel level, boolean error) {
        log(message, level, error, true);
    }

    public static void log(Object message, ErrorLevel level, boolean error, boolean newLine) {
        log(new LogEntry(message, Instant.now(), level).setNewLine(newLine));
    }
    
    public static void log(LogEntry logentry) {
        SystemOutputStream stream = null;
        if(logentry.getLevel().isBad) {
            stream = NEWSYSERR;
        } else {
            stream = NEWSYSOUT;
        }
        LOG.add(logentry);
        stream.log(logentry);
    }

    public static boolean isLoggerRedirectionEnabled() {
        return enabled;
    }

}
