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

import de.codemakers.base.logger.LogLevel;

public class LoggerTest {
    
    public static final void main(String[] args) throws Exception {
        Logger.log(LoggerTest.class, LogLevel.FINEST, "Test Object 1", "Test Object 2");
        Logger.log(LoggerTest.class, LogLevel.FINER, "Test Object 1", "Test Object 2");
        Logger.log(LoggerTest.class, LogLevel.FINE, "Test Object 1", "Test Object 2");
        Logger.log(LoggerTest.class, LogLevel.DEBUG, "Test Object 1", "Test Object 2");
        Logger.log(LoggerTest.class, LogLevel.INFO, "Test Object 1", "Test Object 2");
        Logger.log(LoggerTest.class, LogLevel.COMMAND, "Test Object 1", "Test Object 2");
        Logger.log(LoggerTest.class, LogLevel.INPUT, "Test Object 1", "Test Object 2");
        Logger.log(LoggerTest.class, LogLevel.WARNING, "Test Object 1", "Test Object 2");
        Logger.log(LoggerTest.class, LogLevel.ERROR, "Test Object 1", "Test Object 2");
    }
    
}
