package omnikryptec.net;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;

/**
 * InputEvent
 * @author Panzer1119
 */
public class InputEvent implements Serializable {
    
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
    private AdvancedSocket socket;
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
     * @param socket AdvancedSocket where the Input comes from
     * @param inputType InputType
     * @param data Data
     */
    public InputEvent(Instant timestamp, AdvancedSocket socket, InputType inputType, Object... data) {
        this(Network.generateID(), timestamp, socket, inputType, data);
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
     * @param socket AdvancedSocket where the Input comes from
     * @param inputType InputType
     * @param data Data
     */
    public InputEvent(long id, Instant timestamp, AdvancedSocket socket, InputType inputType, Object... data) {
        this.id = id;
        this.timestamp = timestamp;
        this.socket = socket;
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
     * Returns the AdvancedSocket
     * @return AdvancedSocket
     */
    public final AdvancedSocket getAdvancedSocket() {
        return socket;
    }
    
    /**
     * Sets the AdvancedSocket
     * @param socket AdvancedSocket
     * @return A reference to this InputEvent
     */
    protected final InputEvent setAdvancedSocket(AdvancedSocket socket) {
        this.socket = socket;
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
    
    /**
     * Returns a copy of this InputEvent
     * @return A copy of this InputEvent
     */
    public final InputEvent copy() {
        return new InputEvent(id, Instant.ofEpochMilli(timestamp.toEpochMilli()), socket, inputType, data);
    }

    @Override
    public String toString() {
        return String.format("InputEvent ID: %d, Timestamp: %s, Sender: \"%s\", Type: %s, Data: %s", id, timestamp, socket, inputType, Arrays.toString(data));
    }
    
}
