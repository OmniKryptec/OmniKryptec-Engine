package omnikryptec.logger;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import omnikryptec.logger.Logger.LogLevel;

/**
 *
 * @author Panzer1119
 */
public class LogEntry implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1045037976976108198L;
	
	public static final String STANDARD_DATETIMEFORMAT = "dd.MM.yyyy HH:mm:ss.SSS";
    public static final String STANDARD_LOGENTRYFORMAT = "dtclthll: meex";
    public static final String NEWLINESTRING = "\n";

    private Object logEntry = null;
    private Instant timestamp = null;
    private Thread thread = null;
    private StackTraceElement stackTraceElement = null;
    private Exception exception = null;
    private LogLevel level = LogLevel.INFO;
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
    
    public LogEntry(Object logEntry, Instant timestamp, LogLevel level) {
        this(logEntry, timestamp, level, null, null);
    }
    
    public LogEntry(Object logEntry, Instant timestamp, LogLevel level, Thread thread, StackTraceElement stackTraceElement) {
        this(logEntry, timestamp, level, STANDARD_DATETIMEFORMAT, STANDARD_LOGENTRYFORMAT, thread, stackTraceElement);
    }

    public LogEntry(Object logEntry, Instant timestamp, LogLevel level, String dateTimeFormat, String logEntryFormat, Thread thread, StackTraceElement stackTraceElement) {
        this.logEntry = logEntry;
        this.timestamp = timestamp;
        this.level = level;
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

    public LogLevel getLevel() {
        return level;
    }

    public LogEntry setLevel(LogLevel level) {
        this.level = level;
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
     * Symbol   Meaning                 Examples
     * ------   -------                 --------
     *  dt      Date and Time           [11.05.2017 17:57:10.258] (Format can vary)
     *  cl      Class and Line          [omnikryptec.test.Test.main(Test.java:22)]
     *  th      Thread                  [main]
     *  ll      Log Level               [ERROR]
     *  me      Text                    Test
     *  ex      Exception StackTrace    omnikryptec.test.Test.lambda$main$0(Test.java:43)
     *                                  java.lang.Thread.run(Thread.java:745)
     * 
     * @param logEntryFormat String New LogEntry Format
     */
    public void setLogEntryFormat(String logEntryFormat) {
        this.logEntryFormat = logEntryFormat;
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
            dt = String.format("[%s]", LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(dateTimeFormat)));
        } catch (Exception ex2) {
            dt = null;
            Logger.OLDSYSERR.println(ex2);
        }
        if(stackTraceElement != null) {
            try {
                cl = String.format("[%s.%s(%s:%s)]", stackTraceElement.getClassName(), stackTraceElement.getMethodName(), stackTraceElement.getFileName(), stackTraceElement.getLineNumber());
            } catch (Exception ex2) {
                cl = null;
            }
        } else {
            cl = null;
        }
        try {
            th = String.format("[%s]", (thread != null ? thread.getName() : (stackTraceElement != null ? stackTraceElement.getClassName() : "-")));
        } catch (Exception ex2) {
            th = null;
        }
        try {
            ll = String.format("[%s]", level.toString());
        } catch (Exception ex2) {
            ll = null;
        }
        if(level == LogLevel.ERROR && exception != null) {
            try {
                ex = "";
                for(StackTraceElement e : exception.getStackTrace()) {
                    ex += NEWLINESTRING + e;
                }
            } catch (Exception ex2) {
                ex = null;
            }
        }
        try {
            while(!logEntryFormatTemp.isEmpty()) {
                int remove = 2;
                temp_1:
                for(int i = 0; i < (logEntryFormatTemp.length() - 1); i++) {
                    final String symbol = logEntryFormatTemp.substring(i, i + 2);
                    switch(symbol) {
                        case "dt":
                            output += (dt != null ? dt : "");
                            break temp_1;
                        case "cl":
                            output += (cl != null ? cl : "");
                            break temp_1;
                        case "th":
                            output += (th != null ? th : "");
                            break temp_1;
                        case "ll":
                            output += (ll != null ? ll : "");
                            break temp_1;
                        case "me":
                            output += (me != null ? me : "");
                            break temp_1;
                        case "ex":
                            output += (ex != null ? ex : "");
                            break temp_1;
                        default:
                            output += symbol.charAt(0);
                            remove = 1;
                            break temp_1;
                    }
                }
                logEntryFormatTemp = logEntryFormatTemp.substring(remove);
            }
        } catch (Exception ex2) {
            Logger.OLDSYSERR.println(ex2);
        }
        if(newLine) {
            output += NEWLINESTRING;
        }
        return output;
    }

}
