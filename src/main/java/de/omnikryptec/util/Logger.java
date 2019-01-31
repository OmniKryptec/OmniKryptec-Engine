package de.omnikryptec.util;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import de.codemakers.base.logger.LogLevel;

//TODO log msg format
public class Logger {
    
    public static enum LogType {
        Debug(-1, false, 2), Info(0, false, 3), Warning(1, true, 0), Error(2, true, 2);
        
        private final int importance;
        private final boolean red;
        private final String dif;
        
        private LogType(int imp, boolean red, int dif) {
            this.importance = imp;
            this.red = red;
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < dif; i++) {
                builder.append(' ');
            }
            this.dif = builder.toString();
        }
    }
    
    private static final Map<Class<?>, Logger> loggerCache = new HashMap<>();
    
    private static PrintStream out = System.out;
    private static PrintStream err = System.err;
    
    private static boolean classDebug = false;
    private static LogType minlevel = LogType.Info;
    
    public static Logger getLogger(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        Logger logger = loggerCache.get(clazz);
        if (logger == null) {
            logger = new Logger(clazz);
            loggerCache.put(clazz, logger);
        }
        return logger;
    }
    
    public static void log(Class<?> clazz, LogType type, Object... msgs) {
        if (type.importance >= minlevel.importance) {
            LocalDateTime now = LocalDateTime.now();
            StringBuilder builder = new StringBuilder();
            builder.append("[ " + type);
            builder.append(type.dif);
            builder.append(" ]");
            builder.append("[ " + now.format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")) + " ]");
            if (clazz != null) {
                String n = classDebug ? clazz.getName() : clazz.getSimpleName();
                if (n.isEmpty()) {
                    String[] array = clazz.getTypeName().split("\\.");
                    n = array[array.length - 1];
                }
                builder.append("[ " + n + " ]");
            }
            builder.append(' ');
            LogType nextType = type;
            for (Object m : msgs) {
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
    
    public static void setClassDebug(boolean b) {
        classDebug = b;
    }
    
    public static void setMinLogType(LogType type) {
        minlevel = type == null ? LogType.Info : type;
    }
    
    public static LogType getMinLogType() {
        return minlevel;
    }
    
    public static boolean isClassDebug() {
        return classDebug;
    }
    
    private Class<?> clazz;
    
    private Logger(Class<?> clazz) {
        this.clazz = clazz;
    }
    
    public void log(LogType type, Object... msgs) {
        log(clazz, type, msgs);
    }
    
    public void debug(Object... msgs) {
        log(clazz, LogType.Debug, msgs);
    }
    
    public void info(Object... msgs) {
        log(clazz, LogType.Info, msgs);
    }
    
    public void warn(Object... msgs) {
        log(clazz, LogType.Warning, msgs);
    }
    
    public void error(Object... msgs) {
        log(clazz, LogType.Error, msgs);
    }
}
