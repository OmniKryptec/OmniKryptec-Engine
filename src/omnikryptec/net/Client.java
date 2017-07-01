package omnikryptec.net;

import java.net.InetAddress;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import omnikryptec.util.logger.LogEntry.LogLevel;
import omnikryptec.util.logger.Logger;

/**
 * Client
 *
 * @author Panzer1119
 */
public class Client extends AdvancedSocket implements InputListenerManager {

    private final HashMap<Long, InputEvent> waitingAnswers = new HashMap<>();
    private final ExecutorService executorInputProcessor = Executors.newFixedThreadPool(1);

    public Client(Socket socket, int threadPoolSize) {
        super(socket, threadPoolSize);
    }

    public Client(InetAddress inetAddress, int port, int threadPoolSize) {
        super(inetAddress, port, threadPoolSize);
    }

    @Override
    public final synchronized void processInput(Object object, Instant timestamp) {
        if (object instanceof InputEvent) {
            final InputEvent event = (InputEvent) object;
            if ((event.getInputType() == InputType.ANSWER) && waitingAnswers.containsKey(event.getID())) {
                waitingAnswers.put(event.getID(), event);
            } else {
                fireInputEvent(event, executorInputProcessor);
            }
        } else {
            fireInputEvent(new InputEvent(timestamp, InputType.RAW_MESSAGE_RECEIVED, object), executorInputProcessor);
        }
    }

    @Override
    public final synchronized void onConnected(Instant timestamp) {
        send(InputType.CLIENT_LOGGED_IN);
    }

    @Override
    public final synchronized void onDisconnected(Instant timestamp) {
        send(InputType.CLIENT_LOGGED_OUT);
    }

    public final synchronized Client answer(InputEvent message, Object... answer) {
        return this.answer(message, Instant.now(), answer);
    }

    public final synchronized Client answer(InputEvent message, Instant timestamp, Object... answer) {
        return this.send(new InputEvent(message.getID(), timestamp, this, InputType.ANSWER, answer));
    }

    public final synchronized Client send(Object object, Object... data) {
        return this.send(object, Instant.now(), data);
    }

    public final synchronized Client send(Object object, Instant timestamp, Object... data) {
        return this.send(new InputEvent(timestamp, this, InputType.MESSAGE_RECEIVED, data));
    }

    public final synchronized Client send(InputEvent event) {
        super.send(event);
        return this;
    }

    public final synchronized InputEvent getAnswer(long id) {
        return getAnswer(id, Duration.ofSeconds(10));
    }

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

}
