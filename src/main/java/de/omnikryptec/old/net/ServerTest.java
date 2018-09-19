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

package de.omnikryptec.old.net;

import de.omnikryptec.old.util.logger.Commands;
import de.omnikryptec.old.util.logger.LogLevel;
import de.omnikryptec.old.util.logger.Logger;

/**
 * ServerTest
 * @author Panzer1119
 */
public class ServerTest {
    
    protected static final String client_test = "192.168.6.115";
    public static final int PORT = 2017;
    
    public static void main(String[] args) throws InterruptedException {
        Logger.setDebugMode(true);
        Logger.setMinimumLogLevel(LogLevel.FINEST);
        Logger.log("Program started!");
        final Server server = new Server(PORT);
        new Thread(() -> {
            try {
                server.start(true);
            } catch (Exception ex) {
                Logger.logErr("Server starting error: " + ex, ex);
            }
        }).start();
        Logger.log("Program is now pausing!");
        Thread.sleep(30000);
        Logger.log("Program finished pausing!");
        new Thread(() -> {
            try {
                server.stop(true);
            } catch (Exception ex) {
                Logger.logErr("Server stopping error: " + ex, ex);
            }
        }).start();
        Logger.log("Program is now pausing!");
        Thread.sleep(5000);
        Logger.log("Program finished pausing!");
        Logger.log("Program finished completely!");
        Commands.COMMANDEXIT.run("-java");
    }
    
}
