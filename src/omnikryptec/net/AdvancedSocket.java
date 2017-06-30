package omnikryptec.net;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.time.Instant;

/**
 * AdvancedSocket
 * @author Panzer1119
 */
public class AdvancedSocket {
    
    private final AdvancedSocket ME = this;
    private Socket socket = null;
    private InetAddress inetAddress = null;
    private int port = -1;
    private ObjectOutputStream oos = null;
    private ObjectInputStream ois = null;
    private final int threadPoolSize;
    private final Thread thread = null;
    private int connectionTimesMax = 10;
    private int connectionDelayTime = 1000;
    private boolean stopped = false;
    private boolean serverSocket = false;
    private boolean run = false;
    private static Instant instantLast = Instant.now();
    
    public AdvancedSocket(int threadPoolSize) {
        this.threadPoolSize = Math.min(threadPoolSize, Network.THREADPOOL_SIZE_MAX_CLIENT);
    }
    
}
