package omnikryptec.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;

/**
 * AdvancedSocket
 * @author Panzer1119
 */
public abstract class AdvancedSocket {
    
    private final AdvancedSocket ME = this;
    private Socket socket = null;
    private InetAddress inetAddress = null;
    private int port = -1;
    private ObjectOutputStream oos = null;
    private ObjectInputStream ois = null;
    private final int threadPoolSize;
    private ExecutorService executor = null;
    private Thread thread = null;
    private int connectionTimesMax = Network.CONNECTION_TIMES_MAX_STANDARD;
    private int connectionDelayTime = Network.CONNECTION_DELAY_TIME_STANDARD;
    private boolean stopped = false;
    private boolean isFromServerSocket = false;
    private boolean run = false;
    //private static Instant instantLast = Instant.now();
    
    public AdvancedSocket(Socket socket, int threadPoolSize) {
        this(socket.getInetAddress(), socket.getPort(), threadPoolSize);
        this.socket = socket;
        connect(false);
    }
    
    public AdvancedSocket(InetAddress inetAddress, int port, int threadPoolSize) {
        this(inetAddress, port, false, threadPoolSize);
    }
    
    public AdvancedSocket(InetAddress inetAddress, int port, boolean connectDirect, int threadPoolSize) {
        this.threadPoolSize = Math.min(threadPoolSize, Network.THREADPOOL_SIZE_CLIENT_MAX);
        resetExecutor(true);
        setInetAddress(inetAddress);
        setPort(port);
        if(connectDirect) {
            connect(true);
        }
    }
    
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
    
    public abstract void processInput(Object object);
    
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
    
    public final boolean disconnect() {
        
        return true;
    }
    
    public final ObjectOutputStream getObjectOutputStream() {
        waitForStream(oos);
        return oos;
    }
    
    public final ObjectInputStream getObjectInputStream() {
        waitForStream(ois);
        return ois;
    }
    
    protected final void waitForStream(Object object) {
        int i = 0;
        while(object == null) {
            try {
                Thread.sleep(Network.STREAM_DELAY_TIME);
            } catch (Exception ex) {
            }
            i++;
            if(i >= Network.STREAM_TIMES_MAX) {
                break;
            }
        }
    }
    
    public final Socket getSocket() {
        return socket;
    }
    
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
    
    public final int getPort() {
        return port;
    }
    
    public final AdvancedSocket setPort(int port) {
        if(!Network.checkPort(port)) {
            this.port = Network.PORT_STANDARD;
            return this;
        }
        this.port = port;
        return this;
    }

    public final int getConnectionTimesMax() {
        return connectionTimesMax;
    }

    public final AdvancedSocket setConnectionTimesMax(int connectionTimesMax) {
        this.connectionTimesMax = connectionTimesMax;
        return this;
    }

    public final int getConnectionDelayTime() {
        return connectionDelayTime;
    }

    public final AdvancedSocket setConnectionDelayTime(int connectionDelayTime) {
        this.connectionDelayTime = connectionDelayTime;
        return this;
    }

    public final boolean isFromServerSocket() {
        return isFromServerSocket;
    }

    public final AdvancedSocket setFromServerSocket(boolean isFromServerSocket) {
        this.isFromServerSocket = isFromServerSocket;
        return this;
    }

    public final int getThreadPoolSize() {
        return threadPoolSize;
    }
    
}
