/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.net;

import de.omnikryptec.util.AdvancedThreadFactory;
import de.omnikryptec.util.logger.LogLevel;
import de.omnikryptec.util.logger.Logger;

import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
     * AdvancedThreadFactory Processor
     */
    private final AdvancedThreadFactory advancedThreadFactoryProcessor = new AdvancedThreadFactory("Server-" + Integer.toHexString(hashCode()) + "-Thread-%d");
    /**
     * ThreadPool for processing InputEvents
     */
    private final ExecutorService executorInputProcessor = Executors.newFixedThreadPool(1, advancedThreadFactoryProcessor);

    /**
     * Creates a Server from a ServerSocket with the standard Server ThreadPool
     * size
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
                        fireInputEvent(event, null /*executorInputProcessor*/);
                        break;
                }
            } else {
                fireInputEvent(event, null /*executorInputProcessor*/);
            }
        } else {
            fireInputEvent(new InputEvent(timestamp, socket, InputType.RAW_MESSAGE_RECEIVED, object), null /*executorInputProcessor*/);
        }
    }

    @Override
    public final synchronized AdvancedSocket onConnected(Socket socket, Instant timestamp) {
        final Client client = new Client(socket, threadPoolSize);
        //client.addInputListener(this);
        client.addInputListener(new InputListener() {
            @Override
            public void inputReceived(InputEvent event) {
                //Logger.log("RECEIVED: " + event, LogLevel.WARNING);
                processInput(event, event.getAdvancedSocket(), event.getTimestamp());
            }
        });
        client.setFromServerSocket(true);
        client.connect(false);
        return client;
    }

    @Override
    public final synchronized boolean onDisconnected(AdvancedSocket socketDisconnected, Instant timestamp) {
        if (socketDisconnected == null) {
            return true;
        }
        if (socketDisconnected instanceof Client) {
            //((Client) socketDisconnected).removeInputListener(this);
        }
        return true;
    }

    /**
     * Returns all to this Server registered Clients
     *
     * @return Registered Clients
     */
    protected final AdvancedSocket[] getRegisteredClientsAsArray() {
        return registeredClients.toArray(new AdvancedSocket[registeredClients.size()]);
    }
/*
    @Override
    public void inputReceived(InputEvent event) {
        //Logger.log("RECEIVED: " + event, LogLevel.WARNING);
        processInput(event, event.getAdvancedSocket(), event.getTimestamp());
    }
*/

    @Override
    public String getName() {
        return "SERVER";
    }
    
}
