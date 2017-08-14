package omnikryptec.util.logger;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Panzer1119
 */
public class LogEntry implements Serializable {

    private static final long serialVersionUID = 1045037976976108198L;
    public static String STANDARD_DATETIMEFORMAT = "dd.MM.yyyy HH:mm:ss.SSS";
    public static String STANDARD_LOGENTRYFORMAT = "dtclthll: meex";
    public static String NEWLINESTRING = "\n";
    public static String ESCAPESTRING = "/";

    private Object logEntry = null;
    private Instant timestamp = null;
    private Thread thread = null;
    private StackTraceElement stackTraceElement = null;
    private Exception exception = null;
    private LogLevel logLevel = LogLevel.INFO;
    private String dateTimeFormat = STANDARD_DATETIMEFORMAT;
    private String logEntryFormat = STANDARD_LOGENTRYFORMAT;
    private boolean debug = false;
    private boolean newLine = true;

    public LogEntry() {
        this(null);
    }

    public LogEntry(Object logEntry) {
        this(logEntry, Instant.now());
    }

    public LogEntry(Object logEntry, Instant timestamp) {
        this(logEntry, timestamp, LogLevel.INFO);
    }

    public LogEntry(Object logEntry, Instant timestamp, LogLevel logLevel) {
        this(logEntry, timestamp, logLevel, null, null);
    }

    public LogEntry(Object logEntry, Instant timestamp, LogLevel logLevel, Thread thread,
            StackTraceElement stackTraceElement) {
        this(logEntry, timestamp, logLevel, Logger.DATETIMEFORMAT, Logger.LOGENTRYFORMAT, thread, stackTraceElement);
    }

    public LogEntry(Object logEntry, Instant timestamp, LogLevel logLevel, String dateTimeFormat, String logEntryFormat,
            Thread thread, StackTraceElement stackTraceElement) {
        this.logEntry = logEntry;
        this.timestamp = timestamp;
        this.logLevel = logLevel;
        this.dateTimeFormat = dateTimeFormat;
        this.logEntryFormat = logEntryFormat;
        this.thread = thread;
        this.stackTraceElement = stackTraceElement;
    }

    public Object getLogEntry() {
        return logEntry;
    }

    public LogEntry setLogEntry(Object logEntry) {
        this.logEntry = logEntry;
        return this;
    }

    public Instant getTimeStamp() {
        return timestamp;
    }

    public LogEntry setTimeStamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public StackTraceElement getStackTraceElement() {
        return stackTraceElement;
    }

    public LogEntry setStackTraceElement(StackTraceElement stacktraceelement) {
        this.stackTraceElement = stacktraceelement;
        return this;
    }

    public Exception getException() {
        return exception;
    }

    public LogEntry setException(Exception exception) {
        this.exception = exception;
        return this;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public LogEntry setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public LogEntry setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
        return this;
    }

    public boolean isDebug() {
        return debug;
    }

    public LogEntry setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public Thread getThread() {
        return thread;
    }

    public LogEntry setThread(Thread thread) {
        this.thread = thread;
        return this;
    }

    public boolean isNewLine() {
        return newLine;
    }

    public LogEntry setNewLine(boolean newLine) {
        this.newLine = newLine;
        return this;
    }

    public String getLogEntryFormat() {
        return logEntryFormat;
    }

    /**
     * Symbol Meaning Examples ------ ------- -------- dt Date and Time
     * [11.05.2017 17:57:10.258] (Format can vary) cl Class and Line
     * [omnikryptec.test.Test.main(Test.java:22)] th Thread [main] ll Log Level
     * [ERROR] me Text Test ex Exception StackTrace
     * omnikryptec.test.Test.lambda$main$0(Test.java:43)
     * java.lang.Thread.run(Thread.java:745)
     *
     * @param logEntryFormat String New LogEntry Format
     */
    public LogEntry setLogEntryFormat(String logEntryFormat) {
        this.logEntryFormat = logEntryFormat;
        return this;
    }

    public LogEntry update() {
        setDateTimeFormat(Logger.DATETIMEFORMAT);
        setLogEntryFormat(Logger.LOGENTRYFORMAT);
        return this;
    }

    @Override
    public String toString() {
        String logEntryFormatTemp = logEntryFormat;
        String output = "";
        String dt = null;
        String cl = null;
        String th = null;
        String ll = null;
        String me = "" + logEntry;
        String ex = null;
        try {
            dt = String.format("[%s]", LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern(dateTimeFormat)));
        } catch (Exception ex2) {
            dt = null;
        }
        if (stackTraceElement != null) {
            try {
                cl = String.format("[%s.%s(%s:%s)]", stackTraceElement.getClassName(),
                        stackTraceElement.getMethodName(), stackTraceElement.getFileName(),
                        stackTraceElement.getLineNumber());
            } catch (Exception ex2) {
                cl = null;
            }
        } else {
            cl = null;
        }
        try {
            th = String.format("[%s]", (thread != null ? thread.getName()
                    : (stackTraceElement != null ? stackTraceElement.getClassName() : "-")));
        } catch (Exception ex2) {
            th = null;
        }
        try {
            ll = String.format("[%s]", logLevel.toString());
        } catch (Exception ex2) {
            ll = null;
        }
        if (logLevel == LogLevel.ERROR && exception != null) {
            try {
                ex = "";
                for (StackTraceElement e : exception.getStackTrace()) {
                    ex += NEWLINESTRING + e;
                }
            } catch (Exception ex2) {
                ex = null;
            }
        }
        try {
            while (!logEntryFormatTemp.isEmpty()) {
                int remove = 2;
                final String symbol = logEntryFormatTemp.substring(0, 2);
                if (symbol.startsWith(ESCAPESTRING)) {
                    output += symbol.charAt(1);
                } else {
                    switch (symbol) {
                        case "dt":
                            output += (dt != null ? dt : "");
                            break;
                        case "cl":
                            output += (cl != null ? cl : "");
                            break;
                        case "th":
                            output += (th != null ? th : "");
                            break;
                        case "ll":
                            output += (ll != null ? ll : "");
                            break;
                        case "me":
                            output += (me != null ? me : "");
                            break;
                        case "ex":
                            output += (ex != null ? ex : "");
                            break;
                        default:
                            output += symbol.charAt(0);
                            remove = 1;
                            break;
                    }
                }
                logEntryFormatTemp = logEntryFormatTemp.substring(remove);
            }
        } catch (Exception ex2) {
            // Logger.OLDSYSERR.println("Error while formattig a LogEntry: " +
            // ex2);
            // ex2.printStackTrace(Logger.OLDSYSERR);
            output = String.valueOf(logEntry);
        }
        if (newLine) {
            output += NEWLINESTRING;
        }
        return output;
    }

}
