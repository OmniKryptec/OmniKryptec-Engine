package omnikryptec.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import omnikryptec.util.logger.LogEntry.LogLevel;
import omnikryptec.util.logger.Logger;

/**
 * AdvancedSocket
 * @author Panzer1119
 */
public abstract class AdvancedSocket implements Serializable {
    
    /**
     * A reference to this AdvancedSocket
     */
    public final AdvancedSocket ME = this;
    /**
     * java.net.Socket Socket which connects to the ServerSocket
     */
    private Socket socket = null;
    /**
     * InetAddress to connect to
     */
    private InetAddress inetAddress = null;
    /**
     * Port to connect to
     */
    private int port = -1;
    /**
     * ObjectOutputStream
     */
    private ObjectOutputStream oos = null;
    /**
     * ObjectInputStream
     */
    private ObjectInputStream ois = null;
    /**
     * ThreadPool size
     */
    private final int threadPoolSize;
    /**
     * ThreadPool
     */
    private ExecutorService executor = null;
    /**
     * Listener Thread
     */
    private Thread thread = null;
    /**
     * Maximum number of tries to (re)connect to a ServerSocket
     */
    private int connectionTimesMax = Network.CONNECTION_TIMES_MAX_STANDARD;
    /**
     * Milliseconds to wait between each (re)connection try
     */
    private int connectionDelayTime = Network.CONNECTION_DELAY_TIME_STANDARD;
    /**
     * If this AdvancedSocket is stopped
     */
    private boolean stopped = false;
    /**
     * If this AdvancedSocket is an AdvancedSocket created from a Server
     */
    private boolean isFromServerSocket = false;
    /**
     * If this AdvancedSocket is running
     */
    private boolean run = false;
    /**
     * Last Check
     */
    private static Instant instantLast = Instant.now();
    
    /**
     * Creates an AdvancedSocket from a Socket
     * @param socket Socket
     * @param threadPoolSize ThreadPool size
     */
    public AdvancedSocket(Socket socket, int threadPoolSize) {
        this(socket.getInetAddress(), socket.getPort(), threadPoolSize);
        this.socket = socket;
        connect(false);
    }
    
    /**
     * Creates an AdvancedSocket which does not connect immediately
     * @param inetAddress InetAddress
     * @param port Port
     * @param threadPoolSize ThreadPool size
     */
    public AdvancedSocket(InetAddress inetAddress, int port, int threadPoolSize) {
        this(inetAddress, port, false, threadPoolSize);
    }
    
    /**
     * Creates an AdvancedSocket
     * @param inetAddress InetAddress
     * @param port Port
     * @param connectDirect If the connection should be established immediately
     * @param threadPoolSize ThreadPool size
     */
    public AdvancedSocket(InetAddress inetAddress, int port, boolean connectDirect, int threadPoolSize) {
        this.threadPoolSize = Math.min(threadPoolSize, Network.THREADPOOL_SIZE_CLIENT_MAX);
        resetExecutor(true);
        setInetAddress(inetAddress);
        setPort(port);
        if(connectDirect) {
            connect(true);
        }
    }
    
    /**
     * Resets the ThreadPool
     * @param immediately If the running Threads should be killed immediately
     * @return A reference to this AdvancedSocket
     */
    protected final AdvancedSocket resetExecutor(boolean immediately) {
        try {
            if(executor != null) {
                if(immediately) {
                    executor.shutdownNow();
                } else {
                    executor.shutdown();
                    executor.awaitTermination(1, TimeUnit.MINUTES);
                }
            }
            this.executor = Executors.newFixedThreadPool(threadPoolSize);
        } catch (Exception ex) {
            Logger.logErr("Error while resetting executor: " + ex, ex);
        }
        return this;
    }
    
    /**
     * Processes Inputs
     * @param object Input
     */
    public abstract void processInput(Object object);
    
    /**
     * Connects this AdvancedSocket to the given Address
     * @param createNewSocket If a new Socket should be created
     * @return <tt>true</tt> if the connection was successfully established
     */
    public final boolean connect(boolean createNewSocket) {
        if(thread != null && thread.isAlive()) {
            if(Logger.isDebugMode()) {
                Logger.log("Can not create new Thread for AdvancedSocket, because there is already a Thread running!", LogLevel.WARNING);
            }
            return false;
        }
        thread = new Thread(() -> {
            stopped = false;
            run = true;
            int i = 0;
            while(run) {
                try {
                    if((!isFromServerSocket && createNewSocket) || socket == null) {
                        resetExecutor(true);
                        Logger.log(String.format("Started new connection to \"%s:%d\"", inetAddress.getHostAddress(), port), LogLevel.FINE);
                        socket = new Socket(inetAddress, port);
                        Logger.log(String.format("Connected successfully to \"%s:%d\"", inetAddress.getHostAddress(), port), LogLevel.FINE);
                    }
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    ois = new ObjectInputStream(socket.getInputStream());
                    boolean disconnected = false;
                    while(!disconnected) {
                        try {
                            final Object object = ois.readObject();
                            executor.execute(() -> {
                                try {
                                    processInput(object);
                                } catch (Exception ex) {
                                }
                            });
                        } catch (IOException ex) {
                            Logger.log(String.format("Disconnected from Server \"%s:%d\"", inetAddress.getHostAddress(), port), LogLevel.FINE);
                            disconnected = true;
                            break;
                        }
                    }
                    if(disconnected && !stopped) {
                        connect(true);
                    }
                    run = false;
                    break;
                } catch (Exception ex) {
                    Logger.logErr(String.format("Error while connecting to \"%s:%d\": %s", inetAddress.getHostAddress(), port, ex), null);
                }
                try {
                    Thread.sleep(connectionDelayTime);
                } catch (Exception ex) {
                }
                i++;
                if(i >= connectionTimesMax || isFromServerSocket) {
                    break;
                }
            }
        });
        thread.start();
        return true;
    }
    
    /**
     * Disconnects this AdvancedSocket
     * @return <tt>true</tt> if the connection was successfully disconnected
     */
    public final boolean disconnect() {
        
        return true;
    }
    
    /**
     * Returns the ObjectOutputStream
     * @return ObjectOutputStream
     */
    public final ObjectOutputStream getObjectOutputStream() {
        waitForStream(oos);
        return oos;
    }
    
    /**
     * Returns the ObjectInputStream
     * @return ObjectInputStream
     */
    public final ObjectInputStream getObjectInputStream() {
        waitForStream(ois);
        return ois;
    }
    
    /**
     * Waits until the stream is no longer null
     * @param object Stream
     */
    protected final void waitForStream(Object object) {
        int i = 0;
        while(object == null) {
            try {
                Thread.sleep(Network.STREAM_DELAY_TIME);
            } catch (Exception ex) {
            }
            i++;
            if((i * Network.STREAM_DELAY_TIME) >= Network.STREAM_TIME_MAX) {
                break;
            }
        }
    }
    
    /**
     * Returns the Socket
     * @return Socket
     */
    public final Socket getSocket() {
        return socket;
    }
    
    /**
     * Sets the Socket and Address
     * @param socket Socket
     * @return A reference to this AdvancedSocket
     */
    public final AdvancedSocket setSocket(Socket socket) {
        this.socket = socket;
        if(socket != null) {
            setInetAddress(socket.getInetAddress());
            setPort(socket.getPort());
        } else {
            setInetAddress(null);
            setPort(-1);
        }
        return this;
    }
    
    public final InetAddress getInetAddress() {
        return inetAddress;
    }
    
    /**
     * Sets the InetAddress
     * @param inetAddress InetAddress to connect to
     * @return A reference to this AdvancedSocket
     */
    public final AdvancedSocket setInetAddress(InetAddress inetAddress) {
        if(inetAddress != null) {
            this.inetAddress = inetAddress;
        } else {
            try {
                this.inetAddress = InetAddress.getLocalHost();
            } catch (Exception ex) {
                this.inetAddress = null;
            }
        }
        return this;
    }
    
    /**
     * Returns the Port
     * @return Port
     */
    public final int getPort() {
        return port;
    }
    
    /**
     * Sets the Port
     * @param port Port to connect to
     * @return A reference to this AdvancedSocket
     */
    public final AdvancedSocket setPort(int port) {
        if(!Network.checkPort(port)) {
            this.port = Network.PORT_STANDARD;
            return this;
        }
        this.port = port;
        return this;
    }

    /**
     * Returns the maximum number of tries to (re)connect to a ServerSocket
     * @return Maximum number of tries to (re)connect to a ServerSocket
     */
    public final int getConnectionTimesMax() {
        return connectionTimesMax;
    }

    /**
     * Sets the maximum number of tries to (re)connect to a ServerSocket
     * @param connectionTimesMax Maximum number of tries to (re)connect to a ServerSocket
     * @return A reference to this AdvancedSocket
     */
    public final AdvancedSocket setConnectionTimesMax(int connectionTimesMax) {
        this.connectionTimesMax = connectionTimesMax;
        return this;
    }

    /**
     * Returns the milliseconds to wait between each (re)connection try
     * @return Milliseconds to wait between each (re)connection try
     */
    public final int getConnectionDelayTime() {
        return connectionDelayTime;
    }

    /**
     * Sets the milliseconds to wait between each (re)connection try
     * @param connectionDelayTime Milliseconds to wait between each (re)connection try
     * @return A reference to this AdvancedSocket
     */
    public final AdvancedSocket setConnectionDelayTime(int connectionDelayTime) {
        this.connectionDelayTime = connectionDelayTime;
        return this;
    }

    /**
     * Returns if this AdvancedSocket is an AdvancedSocket created from a Server
     * @return <tt>true</tt> if this AdvancedSocket is an AdvancedSocket created from a Server
     */
    public final boolean isFromServerSocket() {
        return isFromServerSocket;
    }

    /**
     * Sets if this AdvancedSocket is an AdvancedSocket created from a Server
     * @param isFromServerSocket If this AdvancedSocket is an AdvancedSocket created from a Server
     * @return A reference to this AdvancedSocket
     */
    public final AdvancedSocket setFromServerSocket(boolean isFromServerSocket) {
        this.isFromServerSocket = isFromServerSocket;
        return this;
    }

    /**
     * Returns the ThreadPool size
     * @return ThreadPool size
     */
    public final int getThreadPoolSize() {
        return threadPoolSize;
    }
    
}
