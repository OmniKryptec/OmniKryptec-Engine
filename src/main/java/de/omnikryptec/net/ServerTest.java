package de.omnikryptec.net;

import de.omnikryptec.util.logger.Commands;
import de.omnikryptec.util.logger.LogLevel;
import de.omnikryptec.util.logger.Logger;

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
