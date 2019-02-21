/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.util;

import de.codemakers.base.CJP;
import de.codemakers.base.logger.AdvancedLeveledLogger;
import de.codemakers.base.logger.LogLevel;

import java.util.HashMap;
import java.util.Map;

//TODO log msg format
public class Logger {
    
    private static final Map<Class<?>, Logger> LOGGER_CACHE = new HashMap<>();
    //private static PrintStream out = System.out;
    //private static PrintStream err = System.err;
    //private static boolean classDebug = false;
    //private static LogType minlevel = LogType.Info;
    private static boolean DEBUG_CLASS_NAME = false;
    //private static LogLevel MINIMUM_LOG_LEVEL = LogLevel.INFO;
    
    static {
        CJP.addLoggerClass(Logger.class);
    }
    
    protected final Class<?> clazz;
    
    private Logger(final Class<?> clazz) {
        this.clazz = clazz;
    }
    
    public static Logger getLogger(final Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        return LOGGER_CACHE.computeIfAbsent(clazz, Logger::new);
    }
    
    public static void log(Class<?> clazz, LogLevel logLevel, Object... objects) {
        if (logLevel == null) {
            logLevel = LogLevel.INFO;
        }
        if (getMinimumLogLevel().isThisLevelMoreImportant(logLevel)) {
            return;
        }
        String temp = "";
        if (clazz != null && false) { //FIXME Hmm, why the clazz, P's Logger is already doing this internally?
            final String clazz_name = getClassName(clazz);
            temp += "<";
            temp += clazz_name;
            temp += ">";
        }
        LogLevel lastLogLevel = logLevel;
        for (Object object : objects) {
            if (object instanceof LogLevel) {
                lastLogLevel = (LogLevel) object;
                temp += "\n";
            } else if (getMinimumLogLevel().isThisLevelLessImportantOrEqual(lastLogLevel)) {
                temp += object;
            }
        }
        de.codemakers.base.logger.Logger.log(temp, logLevel);
    }
    
    public static void logDebug(Class<?> clazz, Object... objects) {
        log(clazz, LogLevel.DEBUG, objects);
    }
    
    public static void logInfo(Class<?> clazz, Object... objects) {
        log(clazz, LogLevel.INFO, objects);
    }
    
    public static void logWarning(Class<?> clazz, Object... objects) {
        log(clazz, LogLevel.WARNING, objects);
    }
    
    public static void logError(Class<?> clazz, Object... objects) {
        log(clazz, LogLevel.ERROR, objects);
    }
    
    public static void handleError(Class<?> clazz, Throwable throwable) {
        if (clazz != null) {
            de.codemakers.base.logger.Logger.handleError(throwable, getClassName(clazz)); //FIXME Hmm, why the clazz, P's Logger is already doing this internally?
        } else {
            de.codemakers.base.logger.Logger.handleError(throwable);
        }
    }
    
    protected static String getClassName(Class<?> clazz) {
        String clazz_name = DEBUG_CLASS_NAME ? clazz.getName() : clazz.getSimpleName();
        if (clazz_name.isEmpty()) {
            final String[] split = clazz.getTypeName().split("\\.");
            clazz_name = split[split.length - 1];
        }
        return clazz_name;
    }
    
    /*
    public static void log(final Class<?> clazz, final LogType type, final Object... msgs) {
        if (type.importance >= minlevel.importance) {
            final LocalDateTime now = LocalDateTime.now();
            final StringBuilder builder = new StringBuilder();
            builder.append("[ " + type);
            builder.append(type.dif);
            builder.append(" ]");
            builder.append("[ " + now.format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")) + " ]");
            if (clazz != null) {
                String n = classDebug ? clazz.getName() : clazz.getSimpleName();
                if (n.isEmpty()) {
                    final String[] array = clazz.getTypeName().split("\\.");
                    n = array[array.length - 1];
                }
                builder.append(" <" + n + ">");
            }
            builder.append(' ');
            LogType nextType = type;
            for (final Object m : msgs) {
                if (m instanceof LogType) {
                    nextType = (LogType) m;
                } else if (m != null && nextType.importance >= minlevel.importance) {
                    builder.append(m);
                    builder.append('\n');
                    //nextType = type; //reset so every other LogType must be specified?
                }
            }
            if (type.red) {
                err.print(builder.toString());
            } else {
                out.print(builder.toString());
            }
        }
    }
    */
    
    public static LogLevel getMinimumLogLevel() {
        return de.codemakers.base.logger.Logger.getLogger(AdvancedLeveledLogger.class).getMinimumLogLevel();
    }
    
    public static void setMinimumLogLevel(LogLevel logLevel) {
        if (logLevel == null) {
            logLevel = LogLevel.INFO;
        }
        de.codemakers.base.logger.Logger.getLogger(AdvancedLeveledLogger.class).setMinimumLogLevel(logLevel);
    }
    
    public static boolean isDebugClassName() {
        return DEBUG_CLASS_NAME;
    }
    
    public static void setDebugClassName(boolean debugClassName) {
        DEBUG_CLASS_NAME = debugClassName;
    }
    
    /*
    public static LogType getMinLogType() {
        return minlevel;
    }
    
    public static void setMinLogType(final LogType type) {
        minlevel = type == null ? LogType.Info : type;
    }
    
    public static boolean isClassDebug() {
        return classDebug;
    }
    
    public static void setClassDebug(final boolean b) {
        classDebug = b;
    }
    */
    
    public void log(LogLevel logLevel, Object... objects) {
        log(clazz, logLevel, objects);
    }
    
    public void logDebug(Object... objects) {
        log(clazz, LogLevel.DEBUG, objects);
    }
    
    public void logInfo(Object... objects) {
        log(clazz, LogLevel.INFO, objects);
    }
    
    public void logWarning(Object... objects) {
        log(clazz, LogLevel.WARNING, objects);
    }
    
    public void logError(Object... objects) {
        log(clazz, LogLevel.ERROR, objects);
    }
    
    public void handleError(Throwable throwable) {
        handleError(clazz, throwable);
    }
    
    /*
    public void log(final LogType type, final Object... msgs) {
        log(this.clazz, type, msgs);
    }
    
    public void debug(final Object... msgs) {
        log(this.clazz, LogType.Debug, msgs);
    }
    
    public void info(final Object... msgs) {
        log(this.clazz, LogType.Info, msgs);
    }
    
    public void warn(final Object... msgs) {
        log(this.clazz, LogType.Warning, msgs);
    }
    
    public void error(final Object... msgs) {
        log(this.clazz, LogType.Error, msgs);
    }
    
    public enum LogType {
        Debug(-1, false, 2),
        Info(0, false, 3),
        Warning(1, true, 0),
        Error(2, true, 2);
        
        private final int importance;
        private final boolean red;
        private final String dif;
        
        LogType(final int imp, final boolean red, final int dif) {
            this.importance = imp;
            this.red = red;
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < dif; i++) {
                builder.append(' ');
            }
            this.dif = builder.toString();
        }
    }
    */
    
}
