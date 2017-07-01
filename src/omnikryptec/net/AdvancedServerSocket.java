package omnikryptec.net;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.swing.Timer;
import omnikryptec.util.Util;
import omnikryptec.util.logger.LogEntry.LogLevel;
import omnikryptec.util.logger.Logger;

/**
 * AdvancedServerSocket
 *
 * @author Panzer1119
 */
public abstract class AdvancedServerSocket implements ActionListener, Serializable {

    /**
     * A reference to this AdvancedServerSocket
     */
    public final AdvancedServerSocket ADVANCEDSERVERSOCKET = this;
    /**
     * java.net.ServerSocket ServerSocket which accepts all Sockets
     */
    private ServerSocket serverSocket = null;
    /**
     * Port to start on
     */
    private int port = -1;
    /**
     * List with all accepted AdvancedSockets
     */
    private final ArrayList<AdvancedSocket> socketsAccepted = new ArrayList<>();
    /**
     * ThreadPool size
     */
    private final int threadPoolSize;
    /**
     * ThreadPool Acceptor
     */
    private ExecutorService executorReceiver = null;
    /**
     * Acceptor Thread
     */
    private Thread threadAcceptor = null;
    /**
     * If the AdvancedServerSocket is started
     */
    private boolean started = false;
    /**
     * If the AdvancedServerSocket is stopped
     */
    private boolean stopped = true;
    /**
     * Timestamp when the AdvancedServerSocket was started
     */
    private Instant instantStarted = null;
    /**
     * Timestamp when the AdvancedServerSocket was stopped
     */
    private Instant instantStopped = null;
    /**
     * Delay time between each new connection check in milliseconds
     */
    private int connectionCheckTimerDelay = Network.CONNECTION_CHECK_TIMER_DELAY_STANDARD;
    /**
     * Timer which calls the checkConnection every seconds
     */
    private Timer timer = null;
    /**
     * If disconnected AdvancedSockets should be deleted from the ArrayList
     */
    private boolean deleteDisconnectedAdvancedSockets = true;

    /**
     * Creates an AdvancedServerSocket from a ServerSocket
     *
     * @param serverSocket ServerSocket
     * @param threadPoolSize ThreadPool size
     */
    public AdvancedServerSocket(ServerSocket serverSocket, int threadPoolSize) {
        this.threadPoolSize = Math.min(threadPoolSize, Network.THREADPOOL_SIZE_SERVER_MAX);
        init();
        setServerSocket(serverSocket);
    }

    /**
     * Creates an AdvancedServerSocket
     *
     * @param port Port
     * @param threadPoolSize ThreadPool size
     */
    public AdvancedServerSocket(int port, int threadPoolSize) {
        this.threadPoolSize = Math.min(threadPoolSize, Network.THREADPOOL_SIZE_SERVER_MAX);
        init();
        setPort(port);
    }

    /**
     * Initialize the AdvancedServerSocket
     *
     * @return A reference to this AdvancedServerSocket
     */
    private final AdvancedServerSocket init() {
        resetReceiverThread();
        return this;
    }

    /**
     * Resets the receiver Thread
     *
     * @return A reference to this AdvancedServerSocket
     */
    private final AdvancedServerSocket resetReceiverThread() {
        Util.killThread(threadAcceptor, Network.THREAD_KILL_DELAY_TIME_STANDARD, Network.THREAD_KILL_MAX_TIME_STANDARD);
        threadAcceptor = new Thread(() -> {
            while (started) {
                try {
                    final Socket socket = serverSocket.accept();
                    Instant instantNow = Instant.now();
                    executorReceiver.execute(() -> {
                        synchronized (socketsAccepted) {
                            final AdvancedSocket advancedSocket = onConnected(socket, instantNow);
                            if(advancedSocket != null) {
                                advancedSocket.setConnectionCheckTimerDelay(-1);
                                socketsAccepted.add(advancedSocket);
                            }
                        }
                    });
                } catch (IOException ex) {
                    if (Logger.isDebugMode()) {
                        Logger.log("Server on Port " + port + " stopped!", LogLevel.WARNING);
                    }
                    started = false;
                } catch (Exception ex) {
                    if (Logger.isDebugMode()) {
                        Logger.logErr(String.format("Error while accepting Socket from Port %d: %s", port, ex), ex);
                    }
                }
            }
        });
        return this;
    }

    /**
     * Resets all ExecutorServices
     *
     * @param immediately If the running Threads should be killed immediately
     * @return A reference to this AdvancedServerSocket
     */
    private final AdvancedServerSocket resetExecutors(boolean immediately) {
        executorReceiver = resetExecutor(executorReceiver, immediately);
        return this;
    }

    /**
     * Resets a ThreadPool
     *
     * @param immediately If the running Threads should be killed immediately
     * @return New ThreadPool
     */
    private final ExecutorService resetExecutor(ExecutorService executor, boolean immediately) {
        try {
            if (executor != null) {
                if (immediately) {
                    executor.shutdownNow();
                } else {
                    executor.shutdown();
                    executor.awaitTermination(1, TimeUnit.MINUTES);
                }
            }
            return Executors.newFixedThreadPool(threadPoolSize);
        } catch (Exception ex) {
            if (Logger.isDebugMode()) {
                Logger.logErr("Error while resetting executor: " + ex, ex);
            }
            return null;
        }
    }

    /**
     * Resets the Timer
     *
     * @param delay Delay
     */
    private final void resetTimer(int delay) {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        if (timer == null) {
            timer = new Timer(delay, this);
        }
    }

    /**
     * Starts the AdvancedServerSocket
     *
     * @return <tt>true</tt> if the ServerSocket was successfully started
     */
    public final boolean start() {
        return start(false);
    }

    /**
     * Starts the AdvancedServerSocket
     *
     * @param createNewServerSocket If a new ServerSocket should be created
     * @return <tt>true</tt> if the ServerSocket was successfully started
     */
    public final boolean start(boolean createNewServerSocket) {
        if (started) {
            if (Logger.isDebugMode()) {
                Logger.log("Can not start a ServerSocket on Port " + port + ", because there is already a ServerSocket running!", LogLevel.WARNING);
            }
            return false;
        }
        if (!Network.registerPort(port, this)) {
            if (Logger.isDebugMode()) {
                Logger.log("Can not start a ServerSocket on Port " + port + ", because the Port can not be registered!", LogLevel.WARNING);
            }
            return false;
        }
        try {
            resetReceiverThread();
            resetExecutors(true);
            resetTimer(connectionCheckTimerDelay);
            closeSockets();
            started = startServerSocket(createNewServerSocket);
            if (started) {
                if (Logger.isDebugMode()) {
                    Logger.log("Started successfully Server on Port " + port, LogLevel.FINE);
                }
                instantStopped = null;
                instantStarted = Instant.now();
            }
            stopped = !started;
            if (started) {
                threadAcceptor.start();
                if (connectionCheckTimerDelay > 0) {
                    timer.start();
                }
            }
            return started;
        } catch (Exception ex) {
            if (Logger.isDebugMode()) {
                Logger.logErr(String.format("Error while starting Server on Port %d: %s", port, ex), ex);
            }
            return false;
        }
    }

    /**
     * Starts the ServerSocket
     *
     * @param createNewServerSocket If a new ServerSocket should be created
     * @return <tt>true</tt> if the ServerSocket was successfully started
     */
    private final boolean startServerSocket(boolean createNewServerSocket) {
        try {
            if (Logger.isDebugMode()) {
                Logger.log("Started ServerSocket on Port " + port, LogLevel.FINE);
            }
            if (createNewServerSocket || serverSocket == null) {
                Network.closeServerSocket(serverSocket);
                serverSocket = new ServerSocket(port);
            }
            return true;
        } catch (Exception ex) {
            if (Logger.isDebugMode()) {
                Logger.logErr(String.format("Error while starting ServerSocket on Port %d: %s", port, ex), ex);
            }
            return false;
        }
    }

    /**
     * Stops immediately the AdvancedServerSocket
     *
     * @return <tt>true</tt> if the ServerSocket was stopped successfully
     */
    public final boolean stop() {
        return stop(true);
    }

    /**
     * Stops the AdvancedServerSocket
     *
     * @param immediately If the ServerSocket should be stopped immediately
     * @return <tt>true</tt> if the ServerSocket was stopped successfully
     */
    public final boolean stop(boolean immediately) {
        if (stopped) {
            if (Logger.isDebugMode()) {
                Logger.log("Can not stop Server on Port " + port + ", because there is no Server running!", LogLevel.WARNING);
            }
            return false;
        }
        try {
            instantStopped = Instant.now();
            resetTimer(0);
            resetReceiverThread();
            resetExecutors(immediately);
            closeSockets();
            if (Network.closeServerSocket(serverSocket)) {
                serverSocket = null;
                stopped = true;
            }
            started = !stopped;
            if (stopped) {
                Network.unregisterPort(port);
            }
            return stopped;
        } catch (Exception ex) {
            if (Logger.isDebugMode()) {
                Logger.logErr(String.format("Error while stopping Server on Port %d: %s", port, ex), ex);
            }
            return false;
        }
    }
    
    /**
     * Checks all connections to the accepted Sockets
     * @return A reference to thos AdvancedServerSocket
     */
    private final AdvancedServerSocket checkConnections() {
        synchronized (socketsAccepted) {
            final Iterator<AdvancedSocket> i = socketsAccepted.iterator();
            while(i.hasNext()) {
                final AdvancedSocket socket = i.next();
                if (!socket.checkConnection()) {
                    socket.disconnect(true);
                    if (deleteDisconnectedAdvancedSockets) {
                        i.remove();
                    }
                }
            }
        }
        return this;
    }

    /**
     * Closes all accepted sockets
     *
     * @return A reference to this AdvancedServerSocket
     */
    private final AdvancedServerSocket closeSockets() {
        synchronized (socketsAccepted) {
            socketsAccepted.stream().forEach((socket) -> {
                if (socket != null) {
                    socket.disconnect(true);
                }
            });
            socketsAccepted.clear();
        }
        return this;
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
    public AdvancedSocket onConnected(Socket socket, Instant timestamp) {
        final AdvancedSocket advancedSocket = new AdvancedSocket(socket, threadPoolSize) {
            @Override
            public void processInput(Object object, Instant timestamp) {
                ADVANCEDSERVERSOCKET.processInput(object, this, timestamp);
            }

            @Override
            public void onConnected(Instant timestamp) {
                //Nothing
            }

            @Override
            public void onDisconnected(Instant timestamp) {
                ADVANCEDSERVERSOCKET.onDisconnected(this, timestamp);
            }
        };
        advancedSocket.connect(false);
        return advancedSocket;
    }

    /**
     * Called when a connection from a socket was disconnected
     *
     * @param timestamp Timestamp
     */
    public abstract void onDisconnected(AdvancedSocket socket, Instant timestamp);

    /**
     * Returns the Port
     *
     * @return Port
     */
    public final int getPort() {
        return port;
    }

    /**
     * Sets the Port
     *
     * @param port Port to connect to
     * @return A reference to this AdvancedServerSocket
     */
    public final AdvancedServerSocket setPort(int port) {
        if (!Network.checkPort(port)) {
            this.port = Network.PORT_STANDARD;
            return this;
        }
        this.port = port;
        return this;
    }

    /**
     * Sets the ServerSocket
     *
     * @param serverSocket ServerSocket
     * @return A reference to this AdvancedServerSocket
     */
    public final AdvancedServerSocket setServerSocket(ServerSocket serverSocket) {
        if (serverSocket != null) {
            this.serverSocket = serverSocket;
            setPort(serverSocket.getLocalPort());
        }
        return this;
    }

    /**
     * Returns the ThreadPool size
     *
     * @return ThreadPool size
     */
    public final int getThreadPoolSize() {
        return threadPoolSize;
    }

    /**
     * Returns if the AdvancedServerSocket is started
     *
     * @return <tt>true</tt> if the AdvancedServerSocket is started
     */
    public final boolean isStarted() {
        return started;
    }

    /**
     * Returns if the AdvancedServerSocket is stopped
     *
     * @return <tt>true</tt> if the AdvancedServerSocket is stopped
     */
    public final boolean isStopped() {
        return stopped;
    }

    /**
     * Returns the Timestamp when the ServerSocket was started or null
     *
     * @return Timestamp of starting
     */
    public final Instant getInstantStarted() {
        return instantStarted;
    }

    /**
     * Returns the Timestamp when the ServerSocket was stopped or null
     *
     * @return Timestamp of stopping
     */
    public final Instant getInstantStopped() {
        return instantStopped;
    }

    /**
     * Returns the Duration how long the ServerSocket is/was running
     *
     * @return Duration of the running time
     */
    public final Duration getRunningDuration() {
        if (instantStarted != null) {
            if (instantStopped != null) {
                return Duration.between(instantStarted, instantStopped);
            } else {
                return Duration.between(instantStarted, Instant.now());
            }
        } else {
            return Duration.ZERO;
        }
    }

    /**
     * Returns the delay time between each new connection check in milliseconds
     *
     * @return Delay time between each new connection check in milliseconds
     */
    public final int getConnectionCheckTimerDelay() {
        return connectionCheckTimerDelay;
    }

    /**
     * Sets the delay time between each new connection check in milliseconds
     *
     * @param connectionCheckTimerDelay Delay time between each new connection
     * check in milliseconds
     * @return A reference to this AdvancedServerSocket
     */
    public final AdvancedServerSocket setConnectionCheckTimerDelay(int connectionCheckTimerDelay) {
        this.connectionCheckTimerDelay = connectionCheckTimerDelay;
        return this;
    }

    /**
     * Returns if disconnected AdvancedSockets should be deleted from the ArrayList
     * @return <tt>true</tt> if disconnected AdvancedSockets should be deleted from the ArrayList
     */
    public final boolean isDeletingDisconnectedAdvancedSockets() {
        return deleteDisconnectedAdvancedSockets;
    }

    /**
     * Sets if disconnected AdvancedSockets should be deleted from the ArrayList
     * @param deleteDisconnectedAdvancedSockets If disconnected AdvancedSockets should be deleted from the ArrayList
     * @return A reference to this AdvancedServerSocket
     */
    public final AdvancedServerSocket setDeleteDisconnectedAdvancedSockets(boolean deleteDisconnectedAdvancedSockets) {
        this.deleteDisconnectedAdvancedSockets = deleteDisconnectedAdvancedSockets;
        return this;
    }

    @Override
    public final void actionPerformed(ActionEvent e) {
        if (e.getSource() == timer) {
            checkConnections();
        }
    }

}
