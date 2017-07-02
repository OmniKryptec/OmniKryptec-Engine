package omnikryptec.net;

import java.net.Socket;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server
 *
 * @author Panzer1119
 */
public class Server extends AdvancedServerSocket implements InputListenerManager {

    /**
     * ThreadPool for processing InputEvents
     */
    private final ExecutorService executorInputProcessor = Executors.newFixedThreadPool(1);
    
    public Server(int port, int threadPoolSize) {
        super(port, threadPoolSize);
    }

    @Override
    public final synchronized void processInput(Object object, AdvancedSocket socket, Instant timestamp) {
        if (object instanceof InputEvent) {
            final InputEvent event = (InputEvent) object;
            //if ((event.getInputType() == InputType.ANSWER) && waitingAnswers.containsKey(event.getID())) {
            //    waitingAnswers.put(event.getID(), event);
            //} else {
                fireInputEvent(event, executorInputProcessor);
            //}
        } else {
            fireInputEvent(new InputEvent(timestamp, socket, InputType.RAW_MESSAGE_RECEIVED, object), executorInputProcessor);
        }
    }

    @Override
    public final synchronized AdvancedSocket onConnected(Socket socket, Instant timestamp) {
        return super.onConnected(socket, timestamp);
    }

    @Override
    public final synchronized boolean onDisconnected(AdvancedSocket socket, Instant timestamp) {
        return true;
    }

}
