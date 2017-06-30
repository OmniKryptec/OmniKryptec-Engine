package omnikryptec.net;

import java.net.InetAddress;
import java.net.Socket;
import java.time.Instant;

/**
 * Client
 * @author Panzer1119
 */
public class Client extends AdvancedSocket {
    
    public Client(Socket socket, int threadPoolSize) {
        super(socket, threadPoolSize);
    }
    
    public Client(InetAddress inetAddress, int port, int threadPoolSize) {
        super(inetAddress, port, threadPoolSize);
    }
    
    @Override
    public void processInput(Object object, Instant timestamp) {
        
    }

    @Override
    public void onConnected(Instant timestamp) {
        
    }

    @Override
    public void onDisconnected(Instant timestamp) {
        
    }
    
}
