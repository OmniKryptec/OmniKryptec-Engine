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

import java.net.InetAddress;
import java.net.UnknownHostException;

import de.omnikryptec.old.util.logger.Commands;
import de.omnikryptec.old.util.logger.LogLevel;
import de.omnikryptec.old.util.logger.Logger;

/**
 * ClientTest
 *
 * @author Panzer1119
 */
public class ClientTest {

    protected static final String server_test = "localhost";

    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        Logger.setDebugMode(true);
        Logger.setMinimumLogLevel(LogLevel.FINEST);
        Logger.log("Program started!");
        final Client client = new Client(InetAddress.getByName(server_test), ServerTest.PORT);
        new Thread(() -> {
            try {
                client.connect(true);
            } catch (Exception ex) {
                Logger.logErr("Client connecting error: " + ex, ex);
            }
        }).start();
        Logger.log("Program is now pausing!");
        Thread.sleep(5000);
        Logger.log("Program finished pausing!");
        client.send("Test");
        new Thread(() -> {
            try {
                client.disconnect(true);
            } catch (Exception ex) {
                Logger.logErr("Client disconnecting error: " + ex, ex);
            }
        }).start();
        Logger.log("Program is now pausing!");
        Thread.sleep(5000);
        Logger.log("Program finished pausing!");
        Logger.log("Program finished completely!");
        Commands.COMMANDEXIT.run("-java");
    }

}
