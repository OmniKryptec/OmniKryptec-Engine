package omnikryptec.net;

/**
 * Some Network Constants and methods
 * @author Panzer1119
 */
public class Network {
    
    public static final int PORT_MIN = 1000;
    public static final int PORT_STANDARD = 1234;
    public static final int PORT_MAX = 0xFFFF;
    
    public static final long generateID() {
        return System.nanoTime();
    }
    
}
