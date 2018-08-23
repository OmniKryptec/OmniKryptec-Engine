package de.omnikryptec.net;

import de.omnikryptec.util.AdvancedThreadFactory;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.logger.LogLevel;
import de.omnikryptec.util.logger.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * AdvancedSocket
 *
 * @author Panzer1119
 */
public abstract class AdvancedSocket implements ActionListener, Serializable {

    /**
     * A reference to this AdvancedSocket
     */
    public final AdvancedSocket ADVANCEDSOCKET = this;
    /**
     * java.net.Socket Socket which connects to the ServerSocket
     */
    protected Socket socket = null;
    /**
     * InetAddress to connect to
     */
    protected InetAddress inetAddress = null;
    /**
     * Port to connect to
     */
    protected int port = -1;
    /**
     * ObjectOutputStream
     */
    protected ObjectOutputStream oos = null;
    /**
     * ObjectInputStream
     */
    protected ObjectInputStream ois = null;
    /**
     * ThreadPool size
     */
    protected final int threadPoolSize;
    /**
     * AdvancedThreadFactory Receiver
     */
    private final AdvancedThreadFactory advancedThreadFactoryReceiver = new AdvancedThreadFactory();
    /**
     * ThreadPool Receiving Side
     */
    protected ExecutorService executorReceiver = null;
    /**
     * AdvancedThreadFactory Transmitter
     */
    private final AdvancedThreadFactory advancedThreadFactoryTransmitter = new AdvancedThreadFactory();
    /**
     * ThreadPool Transmitting Side
     */
    protected ExecutorService executorTransmitter = null;
    /**
     * Maximum number of tries to (re)connect to a ServerSocket
     */
    protected int connectionTriesMax = Network.CONNECTION_TRIES_MAX_STANDARD;
    /**
     * Milliseconds to wait between each (re)connection try
     */
    protected int connectionDelayTime = Network.CONNECTION_DELAY_TIME_STANDARD;
    /**
     * Maximum number of tries per connection checks
     */
    protected int connectionCheckTriesMax = Network.CONNECTION_CHECK_TRIES_MAX_STANDARD;
    /**
     * Maximum time between sending the Ping and receiving the Pong in
     * milliseconds
     */
    protected int connectionCheckAnswerTimeMax = Network.CONNECTION_ANSWER_TIME_STANDARD;
    /**
     * Delay time between each answer check in milliseconds
     */
    protected int connectionCheckAnswerDelayTime = Network.CONNECTION_ANSWER_DELAY_TIME_STANDARD;
    /**
     * Delay time between each running connection check in milliseconds
     */
    protected int connectionCheckDelayTime = Network.CONNECTION_CHECK_DELAY_TIME_STANDARD;
    /**
     * Delay time between each new connection check in milliseconds
     */
    protected int connectionCheckTimerDelay = Network.CONNECTION_CHECK_TIMER_DELAY_STANDARD;
    /**
     * If this AdvancedSocket is an AdvancedSocket created from a Server
     */
    protected boolean isFromServerSocket = false;
    /**
     * Receiver Thread
     */
    protected Thread threadReceiver = null;
    /**
     * Timer which calls the checkConnection every seconds
     */
    protected Timer timer = null;
    /**
     * If the AdvancedSocket is connected
     */
    protected boolean connected = false;
    /**
     * If the AdvancedSocket is disconnected
     */
    protected boolean disconnected = true;
    /**
     * Timestamp when the AdvancedSocket was connected
     */
    protected Instant instantConnected = null;
    /**
     * Timestamp when the AdvancedSocket was disconnected
     */
    protected Instant instantDisconnected = null;
    /**
     * Timestamp when the lastPong did happen
     */
    protected Instant lastPong = null;

    /**
     * Creates an AdvancedSocket from a Socket with the standard Client ThreadPool size
     *
     * @param socket Socket
     */
    public AdvancedSocket(Socket socket) {
        this(socket, Network.THREADPOOL_SIZE_CLIENT_STANDARD);
    }
    
    /**
     * Creates an AdvancedSocket from a Socket
     *
     * @param socket Socket
     * @param threadPoolSize ThreadPool size
     */
    public AdvancedSocket(Socket socket, int threadPoolSize) {
        this.threadPoolSize = Math.min(threadPoolSize, Network.THREADPOOL_SIZE_CLIENT_MAX);
        init();
        setSocket(socket, false, false);
    }

    /**
     * Creates an AdvancedSocket with the standard Client ThreadPool size
     *
     * @param inetAddress InetAddress
     * @param port Port
     */
    public AdvancedSocket(InetAddress inetAddress, int port) {
        this(inetAddress, port, Network.THREADPOOL_SIZE_CLIENT_STANDARD);
    }

    /**
     * Creates an AdvancedSocket
     *
     * @param inetAddress InetAddress
     * @param port Port
     * @param threadPoolSize ThreadPool size
     */
    public AdvancedSocket(InetAddress inetAddress, int port, int threadPoolSize) {
        this.threadPoolSize = Math.min(threadPoolSize, Network.THREADPOOL_SIZE_CLIENT_MAX);
        init();
        setInetAddress(inetAddress);
        setPort(port);
    }

    /**
     * Initialize the AdvancedSocket
     *
     * @return A reference to this AdvancedSocket
     */
    private final AdvancedSocket init() {
        resetReceiverThread();
        return this;
    }

    /**
     * Resets both ExecutorServices
     *
     * @param immediately If the running Threads should be killed immediately
     * @return A reference to this AdvancedSocket
     */
    private final AdvancedSocket resetExecutors(boolean immediately) {
        advancedThreadFactoryReceiver.setName("AdvancedSocket-" + formatAddressAndPort() + "-Receiver-Thread-%d");
        executorReceiver = resetExecutor(executorReceiver, advancedThreadFactoryReceiver, immediately);
        advancedThreadFactoryTransmitter.setName("AdvancedSocket-" + formatAddressAndPort() + "-Transmitter-Thread-%d");
        executorTransmitter = resetExecutor(executorTransmitter, advancedThreadFactoryTransmitter, immediately);
        return this;
    }

    /**
     * Resets a ThreadPool
     *
     * @param immediately If the running Threads should be killed immediately
     * @return New ThreadPool
     */
    private final ExecutorService resetExecutor(ExecutorService executor, ThreadFactory threadFactory, boolean immediately) {
        try {
            if (executor != null) {
                if (immediately) {
                    executor.shutdownNow();
                } else {
                    executor.shutdown();
                    executor.awaitTermination(1, TimeUnit.MINUTES);
                }
            }
            return Executors.newFixedThreadPool(threadPoolSize, threadFactory);
        } catch (Exception ex) {
            if (Logger.isDebugMode()) {
                Logger.logErr("Error while resetting executor: " + ex, ex);
            }
            return null;
        }
    }

    /**
     * Resets the receiver Thread
     *
     * @return A reference to this AdvancedSocket
     */
    private final AdvancedSocket resetReceiverThread() {
        Util.killThread(threadReceiver, Network.THREAD_KILL_DELAY_TIME_STANDARD, Network.THREAD_KILL_MAX_TIME_STANDARD);
        threadReceiver = new Thread(() -> {
            while (connected) {
                try {
                    final Object object = ois.readObject();
                    if (object == null) {
                        continue;
                    }
                    final Instant instantNow = Instant.now();
                    if (object instanceof NetworkCommand) {
                        final NetworkCommand command = (NetworkCommand) object;
                        switch (command) {
                            case PING:
                                send(NetworkCommand.PONG);
                                break;
                            case PONG:
                                lastPong = instantNow;
                                break;
                        }
                    } else {
                        executorReceiver.execute(() -> {
                            processInput(object, instantNow);
                        });
                    }
                } catch (IOException ex) {
                    if (Logger.isDebugMode()) {
                        Logger.log(formatAddressAndPort() + " disconnected!", LogLevel.WARNING);
                        disconnect(true);
                        break;
                    }
                } catch (Exception ex) {
                    if (Logger.isDebugMode()) {
                        Logger.logErr(String.format("Error while receiving from %s: %s", formatAddressAndPort(), ex), ex);
                    }
                }
            }
        });
        return this;
    }

    /**
     * Checks the connection
     *
     * @return <tt>true</tt> if the connection is open
     */
    public final boolean checkConnection() {
        try {
            final Instant lastPongOld = lastPong;
            boolean isConnected = true;
            for (int i = 0; i < connectionCheckTriesMax; i++) {
                final Instant instantNow = Instant.now();
                send(NetworkCommand.PING);
                while (lastPongOld != lastPong) {
                    if (Duration.between(instantNow, Instant.now()).toMillis() > connectionCheckAnswerTimeMax) {
                        isConnected = false;
                        break;
                    }
                    try {
                        Thread.sleep(connectionCheckAnswerDelayTime);
                    } catch (Exception ex) {
                    }
                }
                if (isConnected) {
                    break;
                } else {
                    try {
                        Thread.sleep(connectionCheckDelayTime);
                    } catch (Exception ex) {
                    }
                }
            }
            return (connected = isConnected);
        } catch (Exception ex) {
            if (Logger.isDebugMode()) {
                Logger.logErr(String.format("Error while checking connection to %s: %s", formatAddressAndPort(), ex), ex);
            }
            return false;
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
     * Processes Inputs
     *
     * @param object Input
     * @param timestamp Timestamp
     */
    public abstract void processInput(Object object, Instant timestamp);

    /**
     * Called when a connection was successfully established
     *
     * @param timestamp Timestamp
     */
    public abstract void onConnected(Instant timestamp);

    /**
     * Called when a connection was successfully disconnected
     *
     * @param timestamp Timestamp
     */
    public abstract void onDisconnected(Instant timestamp);

    /**
     * Connects the AdvancedSocket
     *
     * @return <tt>true</tt> if the connection was successfully established
     */
    public final boolean connect() {
        return connect(false);
    }

    /**
     * Connects the AdvancedSocket
     *
     * @param createNewSocket If a new Socket should be created
     * @return <tt>true</tt> if the connection was successfully established
     */
    public final boolean connect(boolean createNewSocket) {
        return connect(createNewSocket, 0);
    }

    /**
     * Connects the AdvancedSocket
     *
     * @param createNewSocket If a new Socket should be created
     * @param tries How many tries it took till now
     * @return <tt>true</tt> if the connection was successfully established
     */
    private final boolean connect(boolean createNewSocket, int tries) {
        if (connected) {
            if (Logger.isDebugMode()) {
                Logger.log("Can not connect to " + formatAddressAndPort() + ", because there is already a connection established!", LogLevel.WARNING);
            }
            return false;
        }
        tries++;
        if (tries > connectionTriesMax) {
            return false;
        }
        try {
            resetReceiverThread();
            resetExecutors(true);
            resetTimer(connectionCheckTimerDelay);
            if (createNewSocket) {
                closeStreams();
            }
            connected = connectSocket(createNewSocket);
            if (connected) {
                if (Logger.isDebugMode()) {
                    Logger.log("Connected successfully to " + formatAddressAndPort(), LogLevel.FINE);
                }
                instantDisconnected = null;
                onConnected((instantConnected = Instant.now()));
            }
            disconnected = !connected;
            if (!connected) { //FIXME Das muesste !checkConnection() sein!
                try {
                    Thread.sleep(connectionDelayTime);
                } catch (Exception ex) {
                }
                return connect(createNewSocket, tries);
            } else {
                threadReceiver.start();
                if (connectionCheckTimerDelay > 0) {
                    timer.start();
                }
                return connected;
            }
        } catch (Exception ex) {
            if (Logger.isDebugMode()) {
                Logger.logErr(String.format("Error while connecting to %s: %s", formatAddressAndPort(), ex), ex);
            }
            return false;
        }
    }

    /**
     * Disconnects immediately the AdvancedSocket
     *
     * @return <tt>true</tt> if the connection was successfully closed
     */
    public final boolean disconnect() {
        return disconnect(true);
    }

    /**
     * Disconnects the AdvancedSocket
     *
     * @param immediately If the AdvancedSocket should be disconnected
     * immediately
     * @return <tt>true</tt> if the connection was successfully closed
     */
    public final boolean disconnect(boolean immediately) {
        if (disconnected) {
            if (Logger.isDebugMode()) {
                Logger.log("Can not disconnect from " + formatAddressAndPort() + ", because there is no connection established!", LogLevel.WARNING);
            }
            return false;
        }
        try {
            onDisconnected((instantDisconnected = Instant.now()));
            resetTimer(0);
            resetReceiverThread();
            resetExecutors(immediately);
            closeStreams();
            if (Network.closeSocket(socket)) {
                socket = null;
                disconnected = true;
                if (Logger.isDebugMode()) {
                    Logger.log("Disconnected successfully from " + formatAddressAndPort(), LogLevel.FINE);
                }
            }
            connected = !disconnected;
            return disconnected;
        } catch (Exception ex) {
            if (Logger.isDebugMode()) {
                Logger.logErr(String.format("Error while disconnecting from %s: %s", formatAddressAndPort(), ex), ex);
            }
            return false;
        }
    }

    /**
     * Connects the Socket
     *
     * @param createNewSocket If a new Socket should be created
     * @return <tt>true</tt> if the connection was successfully established
     */
    private final boolean connectSocket(boolean createNewSocket) {
        try {
            if (Logger.isDebugMode()) {
                Logger.log("Started connecting to " + formatAddressAndPort(), LogLevel.FINE);
            }
            if ((!isFromServerSocket && createNewSocket) || socket == null) {
                Network.closeSocket(socket);
                socket = new Socket(inetAddress, port);
            }
            setSocket(socket, true, createNewSocket);
            return true;
        } catch (IOException ex) {
            if (Logger.isDebugMode()) {
                Logger.log(String.format("Could not establish a connection to %s", formatAddressAndPort()), LogLevel.WARNING);
            }
            return false;
        } catch (Exception ex) {
            if (Logger.isDebugMode()) {
                Logger.logErr(String.format("Error while connecting Socket to %s: %s", formatAddressAndPort(), ex), ex);
            }
            return false;
        }
    }

    /**
     * Sends an Object to the connect ServerSocket
     *
     * @param object Data
     * @return A reference to this AdvancedSocket
     */
    public final AdvancedSocket send(Object object) {
        if (socket == null || !connected) {
            if (Logger.isDebugMode()) {
                Logger.log("Could not send Object, because there is no Socket or its not connected!", LogLevel.WARNING);
            }
            return this;
        }
        Logger.log("SENT: " + object);
        executorTransmitter.execute(() -> {
            try {
                getObjectOutputStream().writeObject(object);
                getObjectOutputStream().flush();
            } catch (SocketException ex) {
                connected = false;
                if (Logger.isDebugMode()) {
                    Logger.log(formatAddressAndPort() + " disconnected!", LogLevel.WARNING);
                    disconnect(true);
                }
            } catch (Exception ex) {
                if (Logger.isDebugMode()) {
                    Logger.logErr(String.format("Error while sending to %s: %s", formatAddressAndPort(), ex), ex);
                }
            }
        });
        return this;
    }

    /**
     * Returns the InetAddress formatted
     *
     * @return Formatted InetAddress
     */
    public final String formatAddress() {
        return Network.formatInetAddress(inetAddress);
    }

    /**
     * Returns the InetAddress and Port formatted
     *
     * @return Formatted InetAddress and Port
     */
    public final String formatAddressAndPort() {
        return Network.formatInetAddressAndPort(inetAddress, port);
    }

    /**
     * Returns the ObjectOutputStream
     *
     * @return ObjectOutputStream
     */
    public final ObjectOutputStream getObjectOutputStream() {
        waitForStream(oos);
        return oos;
    }

    /**
     * Returns the ObjectInputStream
     *
     * @return ObjectInputStream
     */
    public final ObjectInputStream getObjectInputStream() {
        waitForStream(ois);
        return ois;
    }

    /**
     * Waits until the stream is no longer null
     *
     * @param object Stream
     */
    protected final void waitForStream(Object object) {
        if (object != null) {
            return;
        }
        int i = 0;
        while (object == null) {
            try {
                Thread.sleep(Network.STREAM_DELAY_TIME);
            } catch (Exception ex) {
            }
            i++;
            if ((i * Network.STREAM_DELAY_TIME) >= Network.STREAM_TIME_MAX) {
                break;
            }
        }
    }

    /**
     * Closes the Streams
     *
     * @return A reference to this AdvancedSocket
     */
    private final AdvancedSocket closeStreams() {
        try {
            if (oos != null) {
                oos.close();
            }
        } catch (Exception ex) {
            if (Logger.isDebugMode()) {
                Logger.logErr("Error while closing old ObjectOutputStream: " + ex, null);
            }
        }
        oos = null;
        try {
            if (ois != null) {
                ois.close();
            }
        } catch (Exception ex) {
            if (Logger.isDebugMode()) {
                Logger.logErr("Error while closing old ObjectInputStream: " + ex, null);
            }
        }
        ois = null;
        return this;
    }

    /**
     * Sets the Streams
     *
     * @param socket Socket from where the new Streams are coming
     * @param closeOldStreams If the old Streams should get closed
     * @return A reference to this AdvancedSocket
     */
    private final AdvancedSocket setStreams(Socket socket, boolean closeOldStreams) {
        if (closeOldStreams) {
            closeStreams();
        }
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            connected = false;
            if (Logger.isDebugMode()) {
                Logger.log("Could not set ObjectOutputStream from Socket, because its already closed!", LogLevel.WARNING);
            }
        } catch (Exception ex) {
            if (Logger.isDebugMode()) {
                Logger.logErr("Error while setting ObjectOutputStream: " + ex, ex);
            }
        }
        try {
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            connected = false;
            if (Logger.isDebugMode()) {
                Logger.log("Could not set ObjectInputStream from Socket, because its already closed!", LogLevel.WARNING);
            }
        } catch (Exception ex) {
            if (Logger.isDebugMode()) {
                Logger.logErr("Error while setting ObjectInputStream: " + ex, ex);
            }
        }
        return this;
    }

    /**
     * Returns the Socket
     *
     * @return Socket
     */
    public final Socket getSocket() {
        return socket;
    }

    /**
     * Sets the Socket and Address and closes the old Streams
     *
     * @param socket Socket
     * @return A reference to this AdvancedSocket
     */
    public final AdvancedSocket setSocket(Socket socket) {
        return setSocket(socket, true, true);
    }
    
    /**
     * Sets the Socket and Address
     *
     * @param socket Socket
     * @param setStreams If the streams should be setted
     * @param closeOldStreams If the old Streams should get closed
     * @return A reference to this AdvancedSocket
     */
    protected final AdvancedSocket setSocket(Socket socket, boolean setStreams, boolean closeOldStreams) {
        if (socket != null) {
            this.socket = socket;
            if (setStreams) {
                setStreams(socket, closeOldStreams);
            }
            setInetAddress(socket.getInetAddress());
            setPort(socket.getPort());
        }
        return this;
    }

    /**
     * Returns the InetAddress
     * @return InetAddress
     */
    public final InetAddress getInetAddress() {
        return inetAddress;
    }

    /**
     * Sets the InetAddress
     *
     * @param inetAddress InetAddress to connect to
     * @return A reference to this AdvancedSocket
     */
    public final AdvancedSocket setInetAddress(InetAddress inetAddress) {
        if (inetAddress != null) {
            this.inetAddress = inetAddress;
        } else {
            try {
                this.inetAddress = InetAddress.getLocalHost();
            } catch (Exception ex) {
                this.inetAddress = null;
                if (Logger.isDebugMode()) {
                    Logger.logErr("Can not resolve LocalHost from InetAddress: " + ex, ex);
                }
            }
        }
        return this;
    }

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
     * @return A reference to this AdvancedSocket
     */
    public final AdvancedSocket setPort(int port) {
        if (!Network.checkTCPPort(port)) {
            this.port = Network.PORT_STANDARD;
            return this;
        }
        this.port = port;
        return this;
    }

    /**
     * Returns the maximum number of tries to (re)connect to a ServerSocket
     *
     * @return Maximum number of tries to (re)connect to a ServerSocket
     */
    public final int getConnectionTriesMax() {
        return connectionTriesMax;
    }

    /**
     * Sets the maximum number of tries to (re)connect to a ServerSocket
     *
     * @param connectionTimesMax Maximum number of tries to (re)connect to a
     * ServerSocket
     * @return A reference to this AdvancedSocket
     */
    public final AdvancedSocket setConnectionTimesMax(int connectionTimesMax) {
        this.connectionTriesMax = connectionTimesMax;
        return this;
    }

    /**
     * Returns the milliseconds to wait between each (re)connection try
     *
     * @return Milliseconds to wait between each (re)connection try
     */
    public final int getConnectionDelayTime() {
        return connectionDelayTime;
    }

    /**
     * Sets the milliseconds to wait between each (re)connection try
     *
     * @param connectionDelayTime Milliseconds to wait between each
     * (re)connection try
     * @return A reference to this AdvancedSocket
     */
    public final AdvancedSocket setConnectionDelayTime(int connectionDelayTime) {
        this.connectionDelayTime = connectionDelayTime;
        return this;
    }

    /**
     * Returns if this AdvancedSocket is an AdvancedSocket created from a Server
     *
     * @return <tt>true</tt> if this AdvancedSocket is an AdvancedSocket created
     * from a Server
     */
    public final boolean isFromServerSocket() {
        return isFromServerSocket;
    }

    /**
     * Sets if this AdvancedSocket is an AdvancedSocket created from a Server
     *
     * @param isFromServerSocket If this AdvancedSocket is an AdvancedSocket
     * created from a Server
     * @return A reference to this AdvancedSocket
     */
    public final AdvancedSocket setFromServerSocket(boolean isFromServerSocket) {
        this.isFromServerSocket = isFromServerSocket;
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
     * Returns the Timestamp when the connection was established or null
     *
     * @return Timestamp of connection opening
     */
    public final Instant getInstantConnected() {
        return instantConnected;
    }

    /**
     * Returns the Timestamp when the connection was closed or null
     *
     * @return Timestamp of connection closing
     */
    public final Instant getInstantDisconnected() {
        return instantDisconnected;
    }

    /**
     * Returns the Duration how long the connection is/was open
     *
     * @return Duration of the connection time
     */
    public final Duration getConnectionDuration() {
        if (instantConnected != null) {
            if (instantDisconnected != null) {
                return Duration.between(instantConnected, instantDisconnected);
            } else {
                return Duration.between(instantConnected, Instant.now());
            }
        } else {
            return Duration.ZERO;
        }
    }

    /**
     * Returns when the last Pong was received
     *
     * @return Timestamp of the last Pong
     */
    public final Instant getLastPong() {
        return lastPong;
    }

    /**
     * Returns the maximum number of tries per connection checks
     *
     * @return Maximum number of tries per connection checks
     */
    public final int getConnectionCheckTriesMax() {
        return connectionCheckTriesMax;
    }

    /**
     * Sets the maximum number of tries per connection checks
     *
     * @param connectionCheckTriesMax Maximum number of tries per connection
     * checks
     * @return A reference to this AdvancedSocket
     */
    public final AdvancedSocket setConnectionCheckTriesMax(int connectionCheckTriesMax) {
        this.connectionCheckTriesMax = connectionCheckTriesMax;
        return this;
    }

    /**
     * Returns the maximum time between sending the Ping and receiving the Pong
     * in milliseconds
     *
     * @return Maximum time between sending the Ping and receiving the Pong in
     * milliseconds
     */
    public final int getConnectionCheckAnswerTimeMax() {
        return connectionCheckAnswerTimeMax;
    }

    /**
     * Sets the maximum time between sending the Ping and receiving the Pong in
     * milliseconds
     *
     * @param connectionCheckAnswerTimeMax Maximum time between sending the Ping
     * and receiving the Pong in milliseconds
     * @return A reference to this AdvancedSocket
     */
    public final AdvancedSocket setConnectionCheckAnswerTimeMax(int connectionCheckAnswerTimeMax) {
        this.connectionCheckAnswerTimeMax = connectionCheckAnswerTimeMax;
        return this;
    }

    /**
     * Returns the delay time between each answer check in milliseconds
     *
     * @return Delay time between each answer check in milliseconds
     */
    public final int getConnectionCheckAnswerDelayTime() {
        return connectionCheckAnswerDelayTime;
    }

    /**
     * Sets the delay time between each answer check in milliseconds
     *
     * @param connectionCheckAnswerDelayTime Delay time between each answer
     * check in milliseconds
     * @return A reference to this AdvancedSocket
     */
    public final AdvancedSocket setConnectionCheckAnswerDelayTime(int connectionCheckAnswerDelayTime) {
        this.connectionCheckAnswerDelayTime = connectionCheckAnswerDelayTime;
        return this;
    }

    /**
     * Returns the delay time between each running connection check in
     * milliseconds
     *
     * @return Delay time between each running connection check in milliseconds
     */
    public final int getConnectionCheckDelayTime() {
        return connectionCheckDelayTime;
    }

    /**
     * Sets the delay time between each running connection check in milliseconds
     *
     * @param connectionCheckDelayTime Delay time between each running
     * connection check in milliseconds
     * @return A reference to this AdvancedSocket
     */
    public final AdvancedSocket setConnectionCheckDelayTime(int connectionCheckDelayTime) {
        this.connectionCheckDelayTime = connectionCheckDelayTime;
        return this;
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
     * @return A reference to this AdvancedSocket
     */
    public final AdvancedSocket setConnectionCheckTimerDelay(int connectionCheckTimerDelay) {
        this.connectionCheckTimerDelay = connectionCheckTimerDelay;
        return this;
    }

    /**
     * Returns if the AdvancedSocket is connected
     *
     * @return <tt>true</tt> if the connection is open
     */
    public final boolean isConnected() {
        return connected;
    }

    /**
     * Returns if the AdvancedSocket is disconnected
     *
     * @return <tt>true</tt> if the connection was closed
     */
    public final boolean isDisconnected() {
        return disconnected;
    }

    @Override
    public final void actionPerformed(ActionEvent e) {
        if (e.getSource() == timer) {
            checkConnection();
        }
    }

    @Override
    public String toString() {
        return String.format("%s to %s, Running time: %ds, Connected: %b, Disconnected: %b", getClass().getSimpleName(), formatAddressAndPort(), getConnectionDuration().getSeconds(), connected, disconnected);
    }

}
