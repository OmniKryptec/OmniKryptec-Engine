package omnikryptec.logger;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import omnikryptec.logger.Logger.ErrorLevel;

/**
 *
 * @author Panzer1119
 */
public class LogEntry implements Serializable {
    
    public static final String NEWLINESTRING = "\n";

    private Object logentry = null;
    private Instant timestamp = null;
    private Thread thread = null;
    private StackTraceElement stacktraceelement = null;
    private Exception exception = null;
    private ErrorLevel level = ErrorLevel.INFO;
    private String dateTimeFormat = Logger.STANDARD_DATETIMEFORMAT;
    private boolean printTimestamp = false;
    private boolean printExtraInformation = false;
    private boolean printLevel = false;
    private boolean debug = false;
    private boolean newLine = true;

    public LogEntry(Object logentry, Instant timestamp, ErrorLevel level) {
        this(logentry, timestamp, level, Logger.STANDARD_DATETIMEFORMAT, null, null);
    }

    public LogEntry(Object logentry, Instant timestamp, ErrorLevel level, String dateTimeFormat, Thread thread, StackTraceElement stacktraceelement) {
        this.logentry = logentry;
        this.timestamp = timestamp;
        this.level = level;
        this.dateTimeFormat = dateTimeFormat;
        this.thread = thread;
        this.stacktraceelement = stacktraceelement;
    }

    public Object getLogEntry() {
        return logentry;
    }

    public LogEntry setLogEntry(Object logentry) {
        this.logentry = logentry;
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
        return stacktraceelement;
    }

    public LogEntry setStackTraceElement(StackTraceElement stacktraceelement) {
        this.stacktraceelement = stacktraceelement;
        return this;
    }

    public Exception getException() {
        return exception;
    }

    public LogEntry setException(Exception exception) {
        this.exception = exception;
        return this;
    }

    public ErrorLevel getLevel() {
        return level;
    }

    public LogEntry setLevel(ErrorLevel level) {
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

    public boolean isPrintTimestamp() {
        return printTimestamp;
    }

    public LogEntry setPrintTimestamp(boolean printTimestamp) {
        this.printTimestamp = printTimestamp;
        return this;
    }

    public boolean isPrintExtraInformation() {
        return printExtraInformation;
    }

    public LogEntry setPrintExtraInformation(boolean printExtraInformation) {
        this.printExtraInformation = printExtraInformation;
        return this;
    }

    public boolean isDebug() {
        return debug;
    }

    public LogEntry setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public boolean isPrintLevel() {
        return printLevel;
    }

    public LogEntry setPrintLevel(boolean printLevel) {
        this.printLevel = printLevel;
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

    @Override
    public String toString() {
        String temp_datetime = String.format("[%s]", LocalDateTime.ofInstant(getTimeStamp(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(dateTimeFormat)));
        String temp_extrainformation_format = "";
        String temp_extrainformation = "";
        if(getThread() != null) {
            temp_extrainformation_format = "[%s] [%s.%s(%s:%s)]";
            temp_extrainformation = String.format(temp_extrainformation_format, thread.getName(), getStackTraceElement().getClassName(), getStackTraceElement().getMethodName(), getStackTraceElement().getFileName(), getStackTraceElement().getLineNumber());
        } else {
            temp_extrainformation_format = "[%s.%s(%s:%s)]";
            temp_extrainformation = String.format(temp_extrainformation_format, getStackTraceElement().getClassName(), getStackTraceElement().getMethodName(), getStackTraceElement().getFileName(), getStackTraceElement().getLineNumber());
        }
        String temp_level = String.format("[%s]", level.toString());
        Object msg = getLogEntry();
        String output = "";
        if(printTimestamp || debug) {
            output += temp_datetime;
        }
        if(printExtraInformation || debug) {
            if (printTimestamp || debug) {
                    output += " ";
            }
            output += temp_extrainformation;
        }
        if(printLevel || debug) {
            if(printExtraInformation || printTimestamp || debug) {
                    output += " ";
            }
            output += temp_level;
        }
        if(printTimestamp || printExtraInformation || printLevel || debug) {
            output += ": ";
        }
        output += msg;
        if(level == ErrorLevel.ERROR && exception != null) {
            for(StackTraceElement e : exception.getStackTrace()) {
                output += NEWLINESTRING + e;
            }
        }
        if(newLine) {
            output += NEWLINESTRING;
        }
        return output;
    }

}
