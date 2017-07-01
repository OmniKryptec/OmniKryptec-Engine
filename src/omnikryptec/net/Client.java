package omnikryptec.net;

import java.net.InetAddress;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import omnikryptec.util.logger.LogEntry.LogLevel;
import omnikryptec.util.logger.Logger;

/**
 * Client
 * @author Panzer1119
 */
public class Client extends AdvancedSocket implements InputListenerManager {
    
    private final HashMap<Long, InputEvent> waitingAnswers = new HashMap<>();
    
    public Client(Socket socket, int threadPoolSize) {
        super(socket, threadPoolSize);
    }
    
    public Client(InetAddress inetAddress, int port, int threadPoolSize) {
        super(inetAddress, port, threadPoolSize);
    }
    
    @Override
    public final synchronized void processInput(Object object, Instant timestamp) {
        if(object instanceof InputEvent) {
            final InputEvent event = (InputEvent) object;
            if(waitingAnswers.containsKey(event.getID())) {
                waitingAnswers.put(event.getID(), event);
            }
            fireInputEvent(event);
        } else {
            fireInputEvent(new InputEvent(timestamp, InputType.MESSAGE_RECEIVED, object));
        }
    }

    @Override
    public final void onConnected(Instant timestamp) {
        send(InputType.CLIENT_LOGGED_IN);
    }

    @Override
    public final void onDisconnected(Instant timestamp) {
        send(InputType.CLIENT_LOGGED_OUT);
    }

    public final Client send(Object object, Instant timestamp, Object... data) {
        send(new InputEvent(timestamp, this, InputType.MESSAGE_RECEIVED, data));
        return this;
    }
    
    public final synchronized InputEvent getAnswer(long id) {
        return getAnswer(id, Duration.ofSeconds(10));
    }
    
    public final synchronized InputEvent getAnswer(long id, Duration maxWaitingDuration) {
        if(waitingAnswers.containsKey(id)) {
            Logger.log(String.format("For \"%d\" is already an answer expected!", id), LogLevel.WARNING);
            return null;
        }
        final Instant instantStarted = Instant.now();
        waitingAnswers.put(id, null);
        InputEvent event = null;
        while(((event = waitingAnswers.get(id)) == null) && (Duration.between(instantStarted, Instant.now()).compareTo(maxWaitingDuration) < 0)) {
            try {
                Thread.sleep(1);
            } catch (Exception ex) {
            }
        }
        waitingAnswers.remove(id);
        return event;
    }
    
}
