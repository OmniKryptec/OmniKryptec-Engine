package omnikryptec.logger;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
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
    private final ArrayList<Byte> buffer = new ArrayList<>();
    private boolean isActive = false;
    
    public SystemInputStream(InputStream inputStreamOriginal) {
        this.inputStreamOriginal = inputStreamOriginal;
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
    }
    
    public InputStream getOriginalInputStream() {
        return inputStreamOriginal;
    }
    
    public InputStream getNewInputStream() {
        return inputStreamNew;
    }
    
    public SystemInputStream setActive(boolean isActive) {
        if(!this.isActive && isActive) {
            //Schalte an
            //thread.start();
        } else if(this.isActive && !isActive) {
            //Schalte aus
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
        if(data == NEWLINEBYTE) {
            final byte[] dataAll = new byte[buffer.size()];
            for(int i = 0; i < dataAll.length; i++) {
                dataAll[i] = buffer.get(i);
            }
            buffer.clear();
            final String temp = new String(dataAll);
            final LogEntry logEntry = new LogEntry(temp, Instant.now(), temp.startsWith(Command.COMMANDSTART) ? LogLevel.COMMAND : LogLevel.INPUT);
            Logger.log(logEntry);
        } else {
            buffer.add(data);
        }
    }
    
    private final Thread thread = new Thread(new Runnable() {
        
        @Override
        public void run() {
            if(true) { //TODO Das mache ich später
                return;
            }
            try {
                final Scanner scanner = new Scanner(inputStreamOriginal);
            } catch (Exception ex) {
                Logger.OLDSYSERR.println("Error while reading the InputStream: " + ex);
            }
        }
        
    });
    
	/*
	 * private InputStream inputstream = null; private Scanner scanner = null;
	 * private JLogger logger = null; private final Thread thread = new
	 * Thread(new Runnable() {
	 * 
	 * @Override public void run() { try { while(scanner != null &&
	 * scanner.hasNextLine()) { try { logger.sendCommand(scanner.nextLine()); }
	 * catch (Exception ex) { } } } catch (Exception ex) { }
	 * StaticStandard.log("SystemInputSteam closed"); }
	 * 
	 * });
	 * 
	 * public SystemInputStream(InputStream inputstream, JLogger logger) {
	 * this.inputstream = inputstream; this.logger = logger; updateScanner(); }
	 * 
	 * public void updateScanner() { try { stop(); scanner = new
	 * Scanner(inputstream); thread.start(); } catch (Exception ex) {
	 * StaticStandard.logErr("Error while updating scanner: " + ex, ex); } }
	 * 
	 * public void stop() { while(thread.isAlive()) { try { thread.stop(); }
	 * catch (Exception ex) { } } }
	 */

}
