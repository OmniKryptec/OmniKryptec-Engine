package omnikryptec.logger;

import java.io.PrintStream;
import java.time.Instant;
import java.util.ArrayList;

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

    public static void enableLoggerRedirection(boolean b) {
        if (b && !enabled) {
            System.setOut(NEWSYSOUT);
            System.setErr(NEWSYSERR);
        } else if (!b && enabled) {
            System.setOut(OLDSYSOUT);
            System.setErr(OLDSYSERR);
        }
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
