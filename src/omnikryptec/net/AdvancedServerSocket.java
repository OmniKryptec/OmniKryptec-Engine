package omnikryptec.net;

import java.io.Serializable;
import java.net.ServerSocket;
import java.time.Instant;

/**
 * AdvancedServerSocket
 *
 * @author Panzer1119
 */
public abstract class AdvancedServerSocket implements Serializable {

    private final AdvancedServerSocket ME = this;
    private ServerSocket serverSocket = null;
    private int port = -1;

    public AdvancedServerSocket() {
        
    }

    /**
     * Processes Inputs from sockets
     *
     * @param object Input
     * @param timestamp Timestamp
     */
    public abstract void processInput(Object object, AdvancedSocket socket, Instant timestamp);

    /**
     * Called when a connection from a socket was successfully accepted
     *
     * @param timestamp Timestamp
     */
    public abstract void onConnected(AdvancedSocket socket, Instant timestamp);

    /**
     * Called when a connection from a socket was disconnected
     *
     * @param timestamp Timestamp
     */
    public abstract void onDisconnected(AdvancedSocket socket, Instant timestamp);

}
