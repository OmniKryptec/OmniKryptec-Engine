package omnikryptec.logger;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.Instant;
import omnikryptec.logger.Logger.LogLevel;

/**
 *
 * @author Panzer1119
 */
public class SystemOutputStream extends PrintStream {

    private String dateTimeFormat = Logger.STANDARD_DATETIMEFORMAT;
    private boolean errorStream = false;

    public SystemOutputStream(OutputStream out, boolean errorStream) {
        super(out);
        this.errorStream = errorStream;
    }

    @Override
    public void print(char c) {
        print("" + c);
    }

    @Override
    public void print(long l) {
        print("" + l);
    }

    @Override
    public void print(double d) {
        print("" + d);
    }

    @Override
    public void print(float f) {
        print("" + f);
    }

    @Override
    public void print(boolean b) {
        print("" + b);
    }

    @Override
    public void print(int i) {
        print("" + i);
    }

    @Override
    public void print(char[] c) {
        print(new String(c));
    }

    @Override
    public void print(Object o) {
        if(o != null) {
            print(o.toString());
        } else {
            print("" + null);
        }
    }

    @Override
    public void print(String g) {
        print(g, Instant.now(), false);
    }
    
    
    @Override
    public void println(char c) {
        println("" + c);
    }

    @Override
    public void println(long l) {
        println("" + l);
    }

    @Override
    public void println(double d) {
        println("" + d);
    }

    @Override
    public void println(float f) {
        println("" + f);
    }

    @Override
    public void println(boolean b) {
        println("" + b);
    }

    @Override
    public void println(int i) {
        println("" + i);
    }

    @Override
    public void println(char[] c) {
        println(new String(c));
    }

    @Override
    public void println(Object o) {
        if(o != null) {
            println(o.toString());
        } else {
            println("" + null);
        }
    }

    @Override
    public void println(String g) {
        print(g, Instant.now(), true);
    }
    
    private void print(String g, Instant instant, boolean newLine) {
        Logger.log(getLogEntry(g, instant).setNewLine(newLine));
    }

    public void log(LogEntry logEntry) {
        if(Logger.isMinimumLogLevel(logEntry.getLevel())) {
            super.print(logEntry.toString());
        }
    }

    public LogEntry getLogEntry(Object g, Instant timestamp) {
        return new LogEntry(g, timestamp, (errorStream ? LogLevel.ERROR : LogLevel.INFO), dateTimeFormat, getThread(), getStackTraceElement()).setPrintLevel(true).setPrintTimestamp(true).setPrintExtraInformation(true);
    }
    
    protected StackTraceElement[] getStackTraceElements() {
        return Thread.currentThread().getStackTrace();
    }
    
    protected Thread getThread() {
        return Thread.currentThread();
    }
    
    protected StackTraceElement getStackTraceElement() {
        return getStackTraceElement(Thread.currentThread());
    }
    
    protected StackTraceElement getStackTraceElement(Thread thread) {
        int i = 1;
        final String[] forbidden_names = new String[] {this.getClass().getName(), Logger.class.getName(), /*StaticStandard.class.getName(), */SystemOutputStream.class.getName(), SystemInputStream.class.getName(), PrintStream.class.getName(), InputStream.class.getName()};
        while(containsArray(thread.getStackTrace()[i].getClassName(), forbidden_names)) {
            i++;
        }
        return thread.getStackTrace()[i];
    }
    
    private boolean containsArray(String g, String[] array) {
        for(String gg : array) {
            if(g.equals(gg)) {
                return true;
            }
        }
        return false;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public SystemOutputStream setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
        return this;
    }

}
