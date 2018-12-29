/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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
        return ensureNonNull(obj, null);
    }
    
    public static <T> T ensureNonNull(final T obj, final String message) {
        if (obj == null) {
            final NullPointerException exc = message == null ? new NullPointerException()
                    : new NullPointerException(message);
            final StackTraceElement[] nst = new StackTraceElement[exc.getStackTrace().length - 1];
            System.arraycopy(exc.getStackTrace(), 1, nst, 0, exc.getStackTrace().length - 1);
            exc.setStackTrace(nst);
            throw exc;
        }
        return obj;
    }
    
}
