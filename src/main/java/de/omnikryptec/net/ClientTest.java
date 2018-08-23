package de.omnikryptec.net;

import de.omnikryptec.util.logger.Commands;
import de.omnikryptec.util.logger.LogLevel;
import de.omnikryptec.util.logger.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
