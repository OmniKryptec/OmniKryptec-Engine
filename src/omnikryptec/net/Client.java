package omnikryptec.net;

import java.net.InetAddress;
import java.net.Socket;

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

    public Client(InetAddress inetAddress, int port, boolean connectDirect, int threadPoolSize) {
        super(inetAddress, port, connectDirect, threadPoolSize);
    }

    @Override
    public void processInput(Object object) {
        
    }
    
}
