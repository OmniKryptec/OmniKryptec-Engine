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

import de.omnikryptec.util.Logger.LogType;

public class LoggedException<T extends Exception> {

    private T exception;
    private Logger logger;

    public LoggedException(final Class<?> clazz, final T exc) {
        this(Logger.getLogger(clazz), exc);
    }

    public LoggedException(final Logger logger, final T exc) {
        this.exception = exc;
        this.logger = logger;
    }

    public void thrrow() throws T {
        this.logger.log(LogType.Error, "An exception occured: ");
        throw this.exception;
    }

    public void print() {
        this.logger.log(LogType.Error, "An exception occured: ");
        this.exception.printStackTrace();
    }

}
