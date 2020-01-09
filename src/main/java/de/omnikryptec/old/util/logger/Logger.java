/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.old.util.logger;

import java.awt.*;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Panzer1119 &amp; pcfreak9000
 */
public class Logger {

    protected static final PrintStream OLDSYSOUT = System.out;
    protected static final PrintStream OLDSYSERR = System.err;
    protected static final InputStream OLDSYSIN = System.in;
    public static final SystemOutputStream NEWSYSOUT = new SystemOutputStream(OLDSYSOUT, false);
    public static final SystemOutputStream NEWSYSERR = new SystemOutputStream(OLDSYSERR, true);
    public static final SystemInputStream NEWSYSIN = new SystemInputStream(OLDSYSIN);
    private static ExecutorService THREADPOOL = null;

    static {
	initializeThreadPool();
	Runtime.getRuntime().addShutdownHook(new Thread(() -> {
	    try {
		THREADPOOL.shutdown();
		THREADPOOL.awaitTermination(1, TimeUnit.MINUTES);
	    } catch (Exception ex) {
	    }
	}));
	Commands.initialize();
    }

    public static LogLevel minimumLogLevel = LogLevel.FINEST;
    public static final Console CONSOLE = new Console();
    public static final ArrayList<LogEntry> LOG = new ArrayList<>();
    protected static boolean blockInput = false;

    public static String DATETIMEFORMAT = LogEntry.STANDARD_DATETIMEFORMAT;
    public static String LOGENTRYFORMAT = LogEntry.STANDARD_LOGENTRYFORMAT;
    private static boolean debugMode = false;
    private static boolean enabled = false;

    public static final void setDebugMode(boolean debugMode) {
	Logger.debugMode = debugMode;
    }

    public static final boolean isDebugMode() {
	return debugMode;
    }

    public static final boolean enableLoggerRedirection(boolean enable) {
	if (enable && !enabled) {
	    System.setOut(NEWSYSOUT);
	    System.setErr(NEWSYSERR);
	    System.setIn(NEWSYSIN.getNewInputStream());
	    NEWSYSIN.setActive(true);
	    enabled = true;
	    return true;
	} else if (!enable && enabled) {
	    System.setOut(OLDSYSOUT);
	    System.setErr(OLDSYSERR);
	    NEWSYSIN.setActive(false);
	    System.setIn(OLDSYSIN);
	    enabled = false;
	    return true;
	} else {
	    return false;
	}
    }

    public static final Console showConsoleDirect() {
	return showConsole(null);
    }

    public static final Console showConsole(Component c) {
	new Thread(() -> CONSOLE.showConsole(c)).start();
	try {
	    Thread.sleep(1000);
	} catch (Exception ex) {
	}
	return CONSOLE;
    }

    public static LogEntry logErr(Object message, Exception ex) {
	LogEntry logEntry = NEWSYSERR.getLogEntry(message, Instant.now()).setException(ex);
	log(logEntry);
	return logEntry;
    }

    public static final LogEntry log(Object message) {
	return log(message, LogLevel.INFO);
    }

    public static final LogEntry log(Object message, LogLevel logLevel) {
	return log(message, logLevel, logLevel.isBad());
    }

    public static final LogEntry log(Object message, LogLevel logLevel, boolean error) {
	return log(message, logLevel, error, true);
    }

    public static final LogEntry log(Object message, LogLevel logLevel, boolean error, boolean newLine) {
	Instant instant = Instant.now();
	LogEntry logEntry = null;
	if (error) {
	    logEntry = NEWSYSERR.getLogEntry(message, instant);
	} else {
	    logEntry = NEWSYSOUT.getLogEntry(message, instant);
	}
	logEntry.setLogLevel(logLevel);
	logEntry.setNewLine(newLine);
	log(logEntry);
	return logEntry;
    }

    public static final void log(LogEntry logEntry) {
	addLogEntry(logEntry);
    }

    private static final void addLogEntry(LogEntry logEntry) {
	if (THREADPOOL.isShutdown() || THREADPOOL.isTerminated()) {
	    initializeThreadPool();
	}
	THREADPOOL.submit(() -> {
	    try {
		SystemOutputStream stream = null;
		if (logEntry.getLogLevel().isBad()) {
		    stream = NEWSYSERR;
		} else {
		    stream = NEWSYSOUT;
		}
		while (blockInput) {
		    try {
			Thread.sleep(1);
		    } catch (Exception ex) {
		    }
		}
		LOG.add(logEntry);
		if (logEntry.getLogLevel() == LogLevel.COMMAND && logEntry.getLogEntry() != null) {
		    boolean found = Command.runCommand(logEntry.getLogEntry().toString().substring(1));
		    if (!found) {
			LogEntry logEntryError = NEWSYSERR.getLogEntry("Command not found!", Instant.now());
			logEntryError.setLogEntryFormat(LogEntryFormatter
				.toggleFormat(logEntryError.getLogEntryFormat(), true, false, true, true, true, true));
			Logger.log(logEntryError);
		    }
		} else {
		    stream.log(logEntry);
		}
		if (CONSOLE.isVisible() && CONSOLE.isShowed()) {
		    CONSOLE.addToConsole(logEntry, false);
		}
	    } catch (Exception ex) {
		ex.printStackTrace(OLDSYSERR);
	    }
	});
    }

    private static final void initializeThreadPool() {
	THREADPOOL = Executors.newFixedThreadPool(1);
    }

    public static final boolean isLoggerRedirectionEnabled() {
	return enabled;
    }

    public static final LogLevel getMinimumLogLevel() {
	return minimumLogLevel;
    }

    public static final void setMinimumLogLevel(LogLevel minimumLogLevel) {
	Logger.minimumLogLevel = minimumLogLevel;
    }

    public static final boolean isMinimumLogLevel(LogLevel logLevel) {
	if (logLevel == null) {
	    return false;
	}
	return logLevel.getLevel() <= minimumLogLevel.getLevel();
    }

    public static final void setDateTimeFormat(String dateTimeFormat) {
	DATETIMEFORMAT = dateTimeFormat;
    }

    public static final void setLogEntryFormat(String logEntryFormat) {
	LOGENTRYFORMAT = logEntryFormat;
    }

}
