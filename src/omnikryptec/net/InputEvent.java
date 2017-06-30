package omnikryptec.net;

import java.time.Instant;

/**
 * InputEvent
 * @author Panzer1119
 */
public class InputEvent {
    
    /**
     * (Unique) ID
     */
    private long id;
    /**
     * Timestamp
     */
    private Instant timestamp;
    /**
     * Client where the Input comes from
     */
    private Client client;
    /**
     * InputType of this Input
     */
    private InputType inputType;
    /**
     * Data
     */
    private Object[] data;
    
    /**
     * Creates an InputEvent with an automatically generated ID
     * @param timestamp Timestamp
     * @param inputType InputType
     * @param data Data
     */
    public InputEvent(Instant timestamp, InputType inputType, Object... data) {
        this(timestamp, null, inputType, data);
    }
    
    /**
     * Creates an InputEvent with an automatically generated ID
     * @param timestamp Timestamp
     * @param client Client where the Input comes from
     * @param inputType InputType
     * @param data Data
     */
    public InputEvent(Instant timestamp, Client client, InputType inputType, Object... data) {
        this(Network.generateID(), timestamp, client, inputType, data);
    }
    
    /**
     * Creates an InputEvent
     * @param id (Unique) ID
     * @param timestamp Timestamp
     * @param inputType InputType
     * @param data Data
     */
    public InputEvent(long id, Instant timestamp, InputType inputType, Object... data) {
        this(id, timestamp, null, inputType, data);
    }
    
    /**
     * Creates an InputEvent
     * @param id (Unique) ID
     * @param timestamp Timestamp
     * @param client Client where the Input comes from
     * @param inputType InputType
     * @param data Data
     */
    public InputEvent(long id, Instant timestamp, Client client, InputType inputType, Object... data) {
        this.id = id;
        this.timestamp = timestamp;
        this.client = client;
        this.inputType = inputType;
        this.data = data;
    }

    /**
     * Returns the ID
     * @return ID
     */
    public final long getID() {
        return id;
    }

    /**
     * Sets the ID
     * @param id ID
     * @return A reference to this InputEvent
     */
    protected final InputEvent setID(long id) {
        this.id = id;
        return this;
    }

    /**
     * Returns the Timestamp
     * @return Timestamp
     */
    public final Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the Timestamp
     * @param timestamp Timestamp
     * @return A reference to this InputEvent
     */
    protected final InputEvent setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }
    
    /**
     * Returns the Client
     * @return Client
     */
    public final Client getClient() {
        return client;
    }
    
    /**
     * Sets the Client
     * @param client Client
     * @return A reference to this InputEvent
     */
    protected final InputEvent setClient(Client client) {
        this.client = client;
        return this;
    }

    /**
     * Returns the InputType
     * @return InputType
     */
    public final InputType getInputType() {
        return inputType;
    }

    /**
     * Sets the InputType
     * @param inputType InputType
     * @return A reference to this InputEvent
     */
    protected final InputEvent setInputType(InputType inputType) {
        this.inputType = inputType;
        return this;
    }

    /**
     * Returns the Data
     * @return Data
     */
    public final Object[] getData() {
        return data;
    }

    /**
     * Sets the Data
     * @param data Data
     * @return A reference to this InputEvent
     */
    protected final InputEvent setData(Object[] data) {
        this.data = data;
        return this;
    }
    
}
