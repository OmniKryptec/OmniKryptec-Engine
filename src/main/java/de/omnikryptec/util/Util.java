package de.omnikryptec.util;

public class Util {

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

}
