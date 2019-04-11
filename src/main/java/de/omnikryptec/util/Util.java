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

public class Util {
    
    public static <T> T ensureNonNull(final T obj) {
        return ensureNonNull(obj, null, 2);
    }
    
    public static <T> T ensureNonNull(final T obj, final String message) {
        return ensureNonNull(obj, message, 2);
    }
    
    private static <T> T ensureNonNull(final T obj, final String message, int cut) {
        if (obj == null) {
            final NullPointerException exc = message == null ? new NullPointerException()
                    : new NullPointerException(message);
            final StackTraceElement[] nst = new StackTraceElement[exc.getStackTrace().length - cut];
            System.arraycopy(exc.getStackTrace(), cut, nst, 0, exc.getStackTrace().length - cut);
            exc.setStackTrace(nst);
            throw exc;
        }
        return obj;
    }
    
    public static void stripStacktrace(RuntimeException ex, int cut) {
        final StackTraceElement[] nst = new StackTraceElement[ex.getStackTrace().length - cut];
        System.arraycopy(ex.getStackTrace(), cut, nst, 0, ex.getStackTrace().length - cut);
        ex.setStackTrace(nst);
        throw ex;
    }
    
    public static void stripStacktrace(Exception ex, int cut) throws Exception {
        final StackTraceElement[] nst = new StackTraceElement[ex.getStackTrace().length - cut];
        System.arraycopy(ex.getStackTrace(), cut, nst, 0, ex.getStackTrace().length - cut);
        ex.setStackTrace(nst);
        throw ex;
    }
}
