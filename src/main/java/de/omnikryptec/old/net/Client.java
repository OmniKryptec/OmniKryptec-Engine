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

package de.omnikryptec.old.net;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.omnikryptec.old.util.AdvancedThreadFactory;
import de.omnikryptec.old.util.logger.LogLevel;
import de.omnikryptec.old.util.logger.Logger;

/**
 * Client
 *
 * @author Panzer1119
 */
public class Client extends AdvancedSocket implements InputListenerManager, Serializable {

    /**
     * HashMap that is used to wait for answers
     */
    private final HashMap<Long, InputEvent> waitingAnswers = new HashMap<>();
    /**
     * AdvancedThreadFactory Processor
     */
    private final AdvancedThreadFactory advancedThreadFactoryProcessor = new AdvancedThreadFactory("Client-" + Integer.toHexString(hashCode()) + "-Thread-%d");
    /**
     * ThreadPool for processing InputEvents
     */
    private final ExecutorService executorInputProcessor = Executors.newFixedThreadPool(1, advancedThreadFactoryProcessor);

    /**
     * Creates a Client from a Socket with the standard Client ThreadPool size
     *
     * @param socket Socket
     */
    public Client(Socket socket) {
        super(socket);
    }
    
    /**
     * Creates a Client from a Socket
     *
     * @param socket Socket
     * @param threadPoolSize ThreadPool size
     */
    public Client(Socket socket, int threadPoolSize) {
        super(socket, threadPoolSize);
    }

    /**
     * Creates a Client with the standard Client ThreadPool size
     *
     * @param inetAddress InetAddress
     * @param port Port
     */
    public Client(InetAddress inetAddress, int port) {
        super(inetAddress, port);
    }

    /**
     * Creates a Client
     *
     * @param inetAddress InetAddress
     * @param port Port
     * @param threadPoolSize ThreadPool size
     */
    public Client(InetAddress inetAddress, int port, int threadPoolSize) {
        super(inetAddress, port, threadPoolSize);
    }

    @Override
    public final synchronized void processInput(Object object, Instant timestamp) {
        Logger.log("RECEIVED CLIENT: " + object);
        if (object instanceof InputEvent) {
            final InputEvent event = (InputEvent) object;
            if ((event.getInputType() == InputType.ANSWER) && waitingAnswers.containsKey(event.getID())) {
                waitingAnswers.put(event.getID(), event);
            } else {
                fireInputEvent(event, null /*executorInputProcessor*/);
            }
        } else {
            fireInputEvent(new InputEvent(timestamp, InputType.RAW_MESSAGE_RECEIVED, object), null /*executorInputProcessor*/);
        }
    }

    @Override
    public final synchronized void onConnected(Instant timestamp) {
        if (!isFromServerSocket) {
            send(new InputEvent(timestamp, InputType.CLIENT_LOGGED_IN));
        }
    }

    @Override
    public final synchronized void onDisconnected(Instant timestamp) {
        if (!isFromServerSocket) {
            send(new InputEvent(timestamp, InputType.CLIENT_LOGGED_OUT));
        }
    }

    /**
     * Sends an answer on base of the given InputEvent
     * @param message Message to be answered
     * @param answer Answer
     * @return A reference to this Client
     */
    public final synchronized Client answer(InputEvent message, Object... answer) {
        return this.answer(message, Instant.now(), answer);
    }

    /**
     * Sends an answer on base of the given InputEvent
     * @param message Message to be answered
     * @param timestamp Timestamp
     * @param answer Answer
     * @return A reference to this Client
     */
    public final synchronized Client answer(InputEvent message, Instant timestamp, Object... answer) {
        return this.send(new InputEvent(message.getID(), timestamp, this, InputType.ANSWER, answer));
    }

    /**
     * Sends data
     * @param data Data
     * @return A reference to this Client
     */
    public final synchronized Client send(Object... data) {
        return this.send(Instant.now(), data);
    }

    /**
     * Sends data
     * @param timestamp Timestamp
     * @param data Data
     * @return A reference to this Client
     */
    public final synchronized Client send(Instant timestamp, Object... data) {
        return this.send(new InputEvent(timestamp, this, InputType.MESSAGE_RECEIVED, data));
    }

    /**
     * Sends an InputEvent
     * @param event InputEvent
     * @return A reference to this Client
     */
    public final synchronized Client send(InputEvent event) {
        super.send(event);
        return this;
    }

    /**
     * Waits for an answer with the given ID (waits about 10 seconds, then returning null)
     * @param id ID
     * @return Answer or null
     */
    public final synchronized InputEvent getAnswer(long id) {
        return getAnswer(id, Duration.ofSeconds(10));
    }

    /**
     * Waits for an answer with the given ID
     * @param id ID
     * @param maxWaitingDuration Maximum time to wait for the answer
     * @return Answer or null
     */
    public final synchronized InputEvent getAnswer(long id, Duration maxWaitingDuration) {
        if (waitingAnswers.containsKey(id)) {
            Logger.log(String.format("For \"%d\" is already an answer expected!", id), LogLevel.WARNING);
            return null;
        }
        final Instant instantStarted = Instant.now();
        waitingAnswers.put(id, null);
        InputEvent event = null;
        while (((event = waitingAnswers.get(id)) == null) && ((maxWaitingDuration == null) || (Duration.between(instantStarted, Instant.now()).compareTo(maxWaitingDuration) < 0))) {
            try {
                Thread.sleep(1);
            } catch (Exception ex) {
            }
        }
        waitingAnswers.remove(id);
        return event;
    }

    /**
     * Sends an InputEvent and then waits for an answer on the sent InputEvent (waits about 10 seconds, then returning null)
     * @param event InputEvent
     * @return Answer or null
     */
    public final synchronized InputEvent sendAndGetAnswer(InputEvent event) {
        return sendAndGetAnswer(event, Duration.ofSeconds(10));
    }
    
    /**
     * Sends an InputEvent and then waits for an answer on the sent InputEvent
     * @param event InputEvent
     * @param maxWaitingDuration Maximum time to wait for the answer
     * @return Answer or null
     */
    public final synchronized InputEvent sendAndGetAnswer(InputEvent event, Duration maxWaitingDuration) {
        if (event == null) {
            return null;
        }
        final long id = event.getID();
        if (waitingAnswers.containsKey(id)) {
            Logger.log(String.format("For \"%d\" is already an answer expected!", id), LogLevel.WARNING);
            return null;
        }
        final Instant instantStarted = Instant.now();
        waitingAnswers.put(id, null);
        this.send(event);
        InputEvent answer = null;
        while (((answer = waitingAnswers.get(id)) == null) && ((maxWaitingDuration == null) || (Duration.between(instantStarted, Instant.now()).compareTo(maxWaitingDuration) < 0))) {
            try {
                Thread.sleep(1);
            } catch (Exception ex) {
            }
        }
        waitingAnswers.remove(id);
        return answer;
    }

    @Override
    public String getName() {
        return "CLIENT";
    }

}
