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

package de.omnikryptec.old.util;

import de.omnikryptec.old.main.OmniKryptecEngine;
import de.omnikryptec.old.util.logger.LogLevel;
import de.omnikryptec.old.util.logger.Logger;

import java.util.Properties;
import java.util.function.Consumer;

/**
 * Util
 *
 * @author Panzer1119
 */
public class Util {

    public static final String LINE_TERM = "\n";

    public static final String replaceAll(String string, String toReplace, String with) {
	if (string == null || toReplace == null || with == null) {
	    return "";
	}
	while (string.indexOf(toReplace) != NOT_FOUND) {
	    string = string.replace("\\" + toReplace, with);
	}
	return string;
    }

    public static final String[] adjustLength(String[] array, boolean returnnew) {
	String[] names = array;
	int tmp = 0;
	for (int i = 0; i < names.length; i++) {
	    tmp = Math.max(tmp, names[i].length());
	}
	if (returnnew) {
	    array = new String[names.length];
	}
	StringBuilder s;
	for (int i = 0; i < names.length; i++) {
	    s = new StringBuilder(names[i]);
	    while (s.length() < tmp) {
		s.append(' ');
	    }
	    array[i] = s.toString();
	}
	return array;
    }

    public static final String[] merge(Object... b) {
	String[][] array = createString2d(b);
	String[] newone = new String[] { "" };
	for (int i = 0; i < array.length; i++) {
	    newone = mergeS(newone, array[i]);
	}
	return newone;
    }

    public static final String[] mergeS(String[] a, String[] b) {
	if (a.length == 0) {
	    return b;
	}
	if (b.length == 0) {
	    return a;
	}
	String[] newone = new String[Math.max(a.length, b.length)];
	for (int i = 0; i < newone.length; i++) {
	    newone[i] = (a.length == 1 ? a[0] : a[i]) + (b.length == 1 ? b[0] : b[i]);
	}
	return newone;
    }

    private static String[][] createString2d(Object[] objs) {
	String[][] array = new String[objs.length][];
	for (int i = 0; i < array.length; i++) {
	    if (objs[i] instanceof String[]) {
		array[i] = (String[]) objs[i];
	    } else if (objs[i] instanceof String) {
		array[i] = new String[] { (String) objs[i] };
	    }
	}
	return array;
    }

    /**
     * Stops a Thread until its dead or the Time is over
     *
     * @param thread    Thread to get stopped
     * @param delayTime Time between each stopping try in milliseconds
     * @param maxTime   Maximum time to wait for in milliseconds
     */
    @SuppressWarnings("deprecation")
    public static final void killThread(Thread thread, int delayTime, int maxTime) {
	if (thread == null) {
	    return;
	}
	int times = 0;
	while ((thread.isAlive() && !thread.isInterrupted())
		&& ((delayTime == -1 || maxTime == -1) || ((times * delayTime) < maxTime))) {
	    try {
		thread.interrupt();
		// FIXME deprecated?!
		thread.stop();
		if (delayTime > 0) {
		    Thread.sleep(delayTime);
		}
	    } catch (Exception ex) {
		if (Logger.isDebugMode()) {
		    Logger.logErr(String.format("Error while killing Thread \"%s\": %s", thread.getName(), ex), ex);
		}
	    }
	    times++;
	}
    }

    @Deprecated // evil/weird hack
    public static final double getCurrentTime() {
	try {
	    return OmniKryptecEngine.instance().getDisplayManager().getCurrentTime();
	} catch (Exception ex) {
	    return System.currentTimeMillis();
	}
    }

    public static final float extractPrio(Class<?> clazz, float def) {
	if (clazz.isAnnotationPresent(Priority.class)) {
	    return clazz.getAnnotation(Priority.class).value();
	} else {
	    if (Logger.isDebugMode()) {
		Logger.log("No priority-annotation found in class: " + clazz.getName(), LogLevel.INFO);
	    }
	    return def;
	}
    }

    public static final float extractLvl(Class<?> clazz, float def) {
	if (clazz.isAnnotationPresent(Level.class)) {
	    return clazz.getAnnotation(Level.class).value();
	} else {
	    if (Logger.isDebugMode()) {
		Logger.log("No level-annotation found in class: " + clazz.getName(), LogLevel.INFO);
	    }
	    return def;
	}
    }

    public static final <T> void consume(Consumer<T> consumer, T t) {
	try {
	    consumer.accept(t);
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    public static final <S, F> void finish(boolean succeeded, Consumer<S> success, Consumer<F> failure, S s, F f) {
	try {
	    if (succeeded && success != null) {
		success.accept(s);
	    } else if (!succeeded && failure != null) {
		failure.accept(f);
	    }
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    public static <T> T ensureNonNull(T obj, String message) {
	if (obj == null) {
	    NullPointerException exc = new NullPointerException(message);
	    StackTraceElement[] nst = new StackTraceElement[exc.getStackTrace().length - 1];
	    System.arraycopy(exc.getStackTrace(), 1, nst, 0, exc.getStackTrace().length - 1);
	    exc.setStackTrace(nst);
	    throw exc;
	}
	return obj;
    }

    @Deprecated // Properties is very old
    public static String getString(Properties p, String key, String defaults) {
	return p == null ? defaults : Returner.of(p.getProperty(key)).or(defaults);
    }

}
