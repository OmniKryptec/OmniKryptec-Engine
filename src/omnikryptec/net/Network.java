package omnikryptec.net;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import omnikryptec.util.logger.Logger;

/**
 * Some Network Constants and methods
 * @author Panzer1119
 */
public class Network {
    
    /**
     * Lowest possible Port number
     */
    public static final int PORT_MIN = 1000;
    /**
     * Standard Port number
     */
    public static final int PORT_STANDARD = 1234;
    /**
     * Highest possible Port number
     */
    public static final int PORT_MAX = 0xFFFF;
    
    /**
     * All Ports that are currently in use
     */
    private static final ArrayList<Integer> registeredPorts = new ArrayList<>();
    
    /**
     * Maximum number of Threads in a Client ThreadPool
     */
    public static final int THREADPOOL_SIZE_CLIENT_MAX = 10;
    /**
     * Milliseconds to wait between each time a Stream is null
     */
    public static final int STREAM_DELAY_TIME = 10;
    /**
     * Maximum time to wait for a Stream to get not null in milliseconds
     */
    public static final int STREAM_TIME_MAX = 5000;
    
    /**
     * Standard maximum number of (re)connections
     */
    public static final int CONNECTION_TRIES_MAX_STANDARD = 3;
    /**
     * Standard delay time between each (re)connection try in milliseconds
     */
    public static final int CONNECTION_DELAY_TIME_STANDARD = 1000;
    /**
     * Standard maximum number of tries per connection checks
     */
    public static final int CONNECTION_CHECK_TRIES_MAX_STANDARD = 3;
    /**
     * Standard maximum time between sending the Ping and receiving the Pong in milliseconds
     */
    public static final int CONNECTION_ANSWER_TIME_STANDARD = 1000;
    /**
     * Standard delay time between each answer check in milliseconds
     */
    public static final int CONNECTION_ANSWER_DELAY_TIME_STANDARD = 100;
    /**
     * Standard delay time between each connection check in milliseconds
     */
    public static final int CONNECTION_CHECK_DELAY_TIME_STANDARD = 500;
    /**
     * Standard delay time between each new connection check in milliseconds
     */
    public static final int CONNECTION_CHECK_TIMER_DELAY_STANDARD = 5000;
    
    /**
     * Standard delay time between each try to kill the Thread
     */
    public static final int THREAD_KILL_DELAY_TIME_STANDARD = 10;
    /**
     * Standard maximum time to wait for a Thread to die
     */
    public static final int THREAD_KILL_MAX_TIME_STANDARD = 100;
    
    /**
     * Generates a (hopefully) unique ID for Messages
     * @return New ID
     */
    public static final long generateID() {
        return System.nanoTime();
    }
    
    /**
     * Checks if the given Port is already in use
     * @param port Port to get checked
     * @return <tt>true</tt> if the Port is already in use
     */
    public static final boolean portExists(int port) {
        return registeredPorts.contains(port);
    }
    
    /**
     * Checks if the given Port is not in use and between the lowest and highest possible Port number
     * @param port Port to get checked
     * @return <tt>true</tt> if the Port is not in use and is a possible Port number
     */
    public static final boolean checkPort(int port) {
        if(portExists(port)) {
            return false;
        }
        return ((port >= PORT_MIN) && (port <= PORT_MAX));
    }
    
    /**
     * Registers a Port to be used from now
     * @param port Port to get registered
     * @return <tt>true</tt> if the Port was successfully registered
     */
    public static final boolean registerPort(int port) {
        if(!checkPort(port)) {
            return false;
        }
        registeredPorts.add(port);
        return true;
    }
    
    /**
     * Unregisters a Port to no longer be used
     * @param port Port to get unregistered
     * @return <tt>true</tt> if the Port was successfully unregistered
     */
    public static final boolean unregisterPort(int port) {
        if(!portExists(port)) {
            return false;
        }
        registeredPorts.remove(registeredPorts.indexOf(port));
        return true;
    }
    
    /**
     * Formats an InetAddress
     * @param inetAddress InetAddress to get formatted
     * @return Formatted InetAddress
     */
    public final static String formatInetAddress(InetAddress inetAddress) {
        return String.format("\"%s\"", ((inetAddress != null) ? inetAddress.getHostAddress() : ""));
    }
    
    /**
     * Formats an InetAddress and a Port
     * @param inetAddress InetAddress to get formatted
     * @param port Port to get formatted
     * @return Formatted InetAddress and Port
     */
    public final static String formatInetAddressAndPort(InetAddress inetAddress, int port) {
        return String.format("\"%s:%d\"", ((inetAddress != null) ? inetAddress.getHostAddress() : ""), port);
    }
    
    public static final boolean closeSocket(Socket socket) {
        if(socket == null) {
            return true;
        }
        try {
            socket.close();
            while(!socket.isClosed()) {
                socket.close();
            }
            return true;
        } catch (Exception ex) {
            if(Logger.isDebugMode()) {
                Logger.logErr("Error while closing Socket: " + ex, ex);
            }
            return false;
        }
    }
    
}
