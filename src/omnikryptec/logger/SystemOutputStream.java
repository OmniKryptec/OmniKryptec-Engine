package omnikryptec.logger;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.Instant;
import omnikryptec.logger.Logger.ErrorLevel;

/**
 *
 * @author Paul
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
        Logger.log(getLogEntry(g, Instant.now()).setPrintLevel(true).setPrintTimestamp(true).setPrintExtraInformation(true));
    }

    public void log(LogEntry logentry) {
        super.print(logentry.toString());
    }

    public LogEntry getLogEntry(String g, Instant timestamp) {
        return new LogEntry(g, timestamp, (errorStream ? ErrorLevel.ERROR : ErrorLevel.INFO), dateTimeFormat, getThread(), getStackTraceElement());
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

    /*
     * public DateTimeFormatter getDateTimeFormatter() { return dtf; }
     * 
     * public void setDateTimeFormatter(DateTimeFormatter dtf) { this.dtf = dtf;
     * }
     */

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public SystemOutputStream setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
        return this;
    }

}
