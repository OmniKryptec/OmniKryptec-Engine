package omnikryptec.logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import omnikryptec.logger.LogEntry.LogLevel;

/**
 *
 * @author Panzer1119
 */
public class SystemInputStream {
    
    public static final char NEWLINECHAR = '\n';
    public static final int NEWLINEINT = (int) NEWLINECHAR;
    public static final byte NEWLINEBYTE = (byte) NEWLINEINT;
    
    private final InputStream inputStreamOriginal;
    private final InputStream inputStreamNew;
    private final Thread thread;
    private final LinkedList<Byte> buffer = new LinkedList<>();
    private final ArrayList<Byte> lineBuffer = new ArrayList<>();
    private boolean isActive = false;
    
    public SystemInputStream(InputStream inputStreamOriginal) {
        this.inputStreamOriginal = inputStreamOriginal;
        this.thread = new Thread(() -> {
            Logger.log("Thread System-InputStream started");
            try {
                while(true) {
                    processData((byte) inputStreamOriginal.read());
                }
            } catch (Exception ex) {
                Logger.logErr("Error while reading the System-InputStream: " + ex, ex);
            }
            Logger.log("Thread System-InputStream stopped");
        });
        this.inputStreamNew = new InputStream() {
            
            @Override
            public int read() throws IOException {
                int read = inputStreamOriginal.read();
                if(read == -1) {
                    setActive(false);
                }
                processData((byte) read);
                return read;
            }
            
        };
        /*
        this.inputStreamNew = new InputStream() {
            
            @Override
            public int read() throws IOException {
                synchronized(buffer) {
                    Logger.log("Read-Try: " + buffer.size());
                    while(buffer.size() <= 0 && false) {
                        
                    }
                    Logger.log("Can Read: " + buffer.size());
                    return buffer.pollFirst();
                }
            }

            @Override
            public int available() throws IOException {
                //return buffer.size();
                return 1;
            }
            
        };*/
    }
    
    public InputStream getOriginalInputStream() {
        return inputStreamOriginal;
    }
    
    public InputStream getNewInputStream() {
        return inputStreamNew;
    }
    
    public SystemInputStream setActive(boolean isActive) {
        if(!this.isActive && isActive) {
            //thread.start();
        } else if(this.isActive && !isActive) {
            //thread.interrupt();
            //thread.stop();
        }
        this.isActive = isActive;
        return this;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    private void processData(byte data) {
        synchronized(buffer) {
            //buffer.addLast(data);
            if(data == NEWLINEBYTE) {
                final byte[] dataAll = new byte[lineBuffer.size()];
                for(int i = 0; i < dataAll.length; i++) {
                    dataAll[i] = lineBuffer.get(i);
                }
                lineBuffer.clear();
                final String temp = new String(dataAll);
                final LogEntry logEntry = new LogEntry(temp, Instant.now(), temp.startsWith(Command.COMMANDSTART) ? LogLevel.COMMAND : LogLevel.INPUT);
                Logger.log(logEntry);
            } else {
                lineBuffer.add(data);
            }
        }
    }

}
