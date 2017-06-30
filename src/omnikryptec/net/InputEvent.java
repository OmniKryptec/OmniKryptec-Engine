package omnikryptec.net;

import java.time.Instant;

/**
 * InputEvent
 * @author Panzer1119
 */
public class InputEvent {
    
    private long id;
    private Instant timestamp;
    private Object client; //FIXME Change this Type to Client!!!
    private InputType inputType;
    private Object[] data;
    
    public InputEvent(Instant timestamp, InputType inputType, Object... data) {
        this(timestamp, null, inputType, data);
    }
    
    public InputEvent(Instant timestamp, Object client, InputType inputType, Object... data) {
        this(Network.generateID(), timestamp, client, inputType, data);
    }
    
    public InputEvent(long id, Instant timestamp, InputType inputType, Object... data) {
        this(id, timestamp, null, inputType, data);
    }
    
    public InputEvent(long id, Instant timestamp, Object client, InputType inputType, Object... data) {
        this.id = id;
        this.timestamp = timestamp;
        this.client = client;
        this.inputType = inputType;
        this.data = data;
    }

    public final long getID() {
        return id;
    }

    protected final InputEvent setID(long id) {
        this.id = id;
        return this;
    }

    public final Instant getTimestamp() {
        return timestamp;
    }

    protected final InputEvent setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }
    
    public final Object getClient() {
        return client;
    }
    
    protected final InputEvent setClient(Object client) {
        this.client = client;
        return this;
    }

    public final InputType getInputType() {
        return inputType;
    }

    protected final InputEvent setInputType(InputType inputType) {
        this.inputType = inputType;
        return this;
    }

    public final Object[] getData() {
        return data;
    }

    protected final InputEvent setData(Object[] data) {
        this.data = data;
        return this;
    }
    
}
