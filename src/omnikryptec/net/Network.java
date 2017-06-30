package omnikryptec.net;

import java.util.ArrayList;

/**
 * Some Network Constants and methods
 * @author Panzer1119
 */
public class Network {
    
    public static final int PORT_MIN = 1000;
    public static final int PORT_STANDARD = 1234;
    public static final int PORT_MAX = 0xFFFF;
    
    private static final ArrayList<Integer> registeredPorts = new ArrayList<>();
    
    public static final int THREADPOOL_SIZE_CLIENT_MAX = 10;
    public static final int STREAM_DELAY_TIME = 10;
    public static final int STREAM_TIME_MAX = 5000;
    
    public static final int CONNECTION_TIMES_MAX_STANDARD = 3;
    public static final int CONNECTION_DELAY_TIME_STANDARD = 1000;
    
    
    public static final long generateID() {
        return System.nanoTime();
    }
    
    public static final boolean portExists(int port) {
        return registeredPorts.contains(port);
    }
    
    public static final boolean checkPort(int port) {
        if(portExists(port)) {
            return false;
        }
        return ((port >= PORT_MIN) && (port <= PORT_MAX));
    }
    
    public static final boolean registerPort(int port) {
        if(!checkPort(port)) {
            return false;
        }
        registeredPorts.add(port);
        return true;
    }
    
    public static final boolean unregisterPort(int port) {
        if(!portExists(port)) {
            return false;
        }
        registeredPorts.remove(registeredPorts.indexOf(port));
        return true;
    }
    
}
