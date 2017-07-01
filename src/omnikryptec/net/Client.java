package omnikryptec.net;

import java.net.InetAddress;
import java.net.Socket;
import java.time.Instant;

/**
 * Client
 * @author Panzer1119
 */
public class Client extends AdvancedSocket implements InputListenerManager {
    
    public Client(Socket socket, int threadPoolSize) {
        super(socket, threadPoolSize);
    }
    
    public Client(InetAddress inetAddress, int port, int threadPoolSize) {
        super(inetAddress, port, threadPoolSize);
    }
    
    @Override
    public final void processInput(Object object, Instant timestamp) {
        if(object instanceof InputEvent) {
            fireInputEvent((InputEvent) object);
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
    
}
