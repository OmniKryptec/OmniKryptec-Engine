package omnikryptec.debug;

import java.io.PrintStream;

public class Logger {
	
	private static final PrintStream OLD_SYSOUT = System.out;
	private static final PrintStream OLD_SYSERR = System.err;
	
	private static boolean enabled=false;
	
	
	public static void enabledLoggerRedirection(boolean b){
		if(b&&!enabled){
			
		}else if(!b&&enabled){
			System.setOut(OLD_SYSOUT);
			System.setErr(OLD_SYSERR);
		}
	}
	
	public static enum ErrorLevel{
		FINEST,FINER,FINE,INFO,WARNING,ERROR;
	}
	
	
	
}
