package omnikryptec.debug;

import java.io.PrintStream;

public class Logger {

	public static final PrintStream OLDSYSOUT = System.out;
	public static final PrintStream OLDSYSERR = System.err;
	public static final LoggerPreStream OUTPRE = new LoggerPreStream(OLDSYSOUT);
	public static final LoggerPreStream ERRPRE = new LoggerPreStream(OLDSYSERR);
	private static final PrintStream SYSOUTOVERRIDE = new PrintStream(OUTPRE);
	private static final PrintStream SYSERROVERRIDE = new PrintStream(ERRPRE);
        
	static {
		OUTPRE.setErrorLevel(ErrorLevel.INFO);
		ERRPRE.setErrorLevel(ErrorLevel.ERROR);
	}

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
			System.setOut(SYSOUTOVERRIDE);
			System.setErr(SYSERROVERRIDE);
		} else if (!b && enabled) {
			System.setOut(OLDSYSOUT);
			System.setErr(OLDSYSERR);
		}
	}

	public static void log(Object tolog, ErrorLevel level) {
		log(tolog, level, level.isBad());
	}

	public static void log(Object tolog, ErrorLevel level, boolean error) {
		log(tolog, level, error, true);
	}

	public static void log(Object tolog, ErrorLevel level, boolean error, boolean ln) {
		final ErrorLevel oldlvl;
		final PrintStream usethis;
		if (error) {
			oldlvl = ERRPRE.getErrorLevel();
			ERRPRE.setErrorLevel(level);
			usethis = SYSERROVERRIDE;
		} else {
			oldlvl = OUTPRE.getErrorLevel();
			OUTPRE.setErrorLevel(level);
			usethis = SYSOUTOVERRIDE;
		}
		if (ln) {
			usethis.println(tolog);
		} else {
			usethis.print(tolog);
		}
		if (error) {
			ERRPRE.setErrorLevel(oldlvl);
		} else {
			OUTPRE.setErrorLevel(oldlvl);
		}
	}

	public static boolean isLoggerRedirectionEnabled() {
		return enabled;
	}

}
