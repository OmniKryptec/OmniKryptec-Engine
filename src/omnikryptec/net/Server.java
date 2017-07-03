package omnikryptec.net;

import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

/**
 * Server
 *
 * @author Panzer1119
 */
public class Server extends AdvancedServerSocket implements InputListenerManager, Serializable {

    /**
     * List with all registered Clients
     */
    private final ArrayList<Client> registeredClients = new ArrayList<>();
    /**
     * ThreadPool for processing InputEvents
     */
    private final ExecutorService executorInputProcessor = Executors.newFixedThreadPool(1);

    /**
     * Creates a Server from a ServerSocket with the standard Server ThreadPool size
     *
     * @param serverSocket ServerSocket
     */
    public Server(ServerSocket serverSocket) {
        super(serverSocket);
    }

    /**
     * Creates a Server from a ServerSocket
     *
     * @param serverSocket ServerSocket
     * @param threadPoolSize ThreadPool size
     */
    public Server(ServerSocket serverSocket, int threadPoolSize) {
        super(serverSocket, threadPoolSize);
    }

    /**
     * Creates a Server with the standard Server ThreadPool size
     *
     * @param port Port
     */
    public Server(int port) {
        super(port);
    }

    /**
     * Creates a Server
     *
     * @param port Port
     * @param threadPoolSize ThreadPool size
     */
    public Server(int port, int threadPoolSize) {
        super(port, threadPoolSize);
    }

    @Override
    public final synchronized void processInput(Object object, AdvancedSocket socket, Instant timestamp) {
        if (object instanceof InputEvent) {
            final InputEvent event = (InputEvent) object;
            if (event.getInputType() != null && socket != null) {
                switch (event.getInputType()) {
                    case CLIENT_LOGGED_OUT:
                        if (Logger.isDebugMode()) {
                            Logger.log("\"" + socket + "\" logged out", LogLevel.FINER);
                        }
                        if (socket instanceof Client) {
                            final Client client = (Client) socket;
                            registeredClients.remove(client);
                        }
                        broadcast(event, true, getRegisteredClientsAsArray());
                        break;
                    case CLIENT_LOGGED_IN:
                        if (Logger.isDebugMode()) {
                            Logger.log("\"" + socket + "\" logged in", LogLevel.FINER);
                        }
                        broadcast(event, true, getRegisteredClientsAsArray());
                        if (socket instanceof Client) {
                            final Client client = (Client) socket;
                            if (!registeredClients.contains(client)) {
                                registeredClients.add(client);
                            }
                        }
                        break;
                    case BROADCAST:
                        if (Logger.isDebugMode()) {
                            Logger.log("\"" + socket + "\" broadcasted: " + event, LogLevel.FINER);
                        }
                        if (event.getData() == null || event.getData().length == 0 || event.getData()[0] == null || !(event.getData()[0] instanceof AdvancedSocket[])) {
                            broadcast(event, true, getRegisteredClientsAsArray());
                        } else {
                            boolean whitelist = true;
                            if (event.getData().length >= 2 && event.getData()[1] instanceof Boolean) {
                                whitelist = (Boolean) event.getData()[1];
                            }
                            final AdvancedSocket[] sockets = (AdvancedSocket[]) event.getData()[0];
                            broadcast(event, whitelist, sockets);
                        }
                        break;
                    default:
                        fireInputEvent(event, executorInputProcessor);
                        break;
                }
            } else {
                fireInputEvent(event, executorInputProcessor);
            }
        } else {
            fireInputEvent(new InputEvent(timestamp, socket, InputType.RAW_MESSAGE_RECEIVED, object), executorInputProcessor);
        }
    }

    @Override
    public final synchronized AdvancedSocket onConnected(Socket socket, Instant timestamp) {
        return super.onConnected(socket, timestamp);
    }

    @Override
    public final synchronized boolean onDisconnected(AdvancedSocket socketDisconnected, Instant timestamp) {
        if (socketDisconnected == null) {
            return true;
        }
        return true;
    }

    /**
     * Returns all to this Server registered Clients
     * @return Registered Clients
     */
    protected final AdvancedSocket[] getRegisteredClientsAsArray() {
        return registeredClients.toArray(new AdvancedSocket[registeredClients.size()]);
    }

}
