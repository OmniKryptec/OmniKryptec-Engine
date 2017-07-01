package omnikryptec.net;

import java.net.Socket;
import java.time.Instant;

/**
 * Server
 *
 * @author Panzer1119
 */
public class Server extends AdvancedServerSocket {

    public Server(int port, int threadPoolSize) {
        super(port, threadPoolSize);
        AdvancedSocket socket = null;
        Client client = (Client) socket;
    }

    @Override
    public void processInput(Object object, AdvancedSocket socket, Instant timestamp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AdvancedSocket onConnected(Socket socket, Instant timestamp) {
        return super.onConnected(socket, timestamp); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onDisconnected(AdvancedSocket socket, Instant timestamp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
