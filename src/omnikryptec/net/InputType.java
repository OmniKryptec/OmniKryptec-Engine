package omnikryptec.net;

import java.io.Serializable;

/**
 * InputType
 * @author Panzer1119
 */
public enum InputType implements Serializable {
    MESSAGE_RECEIVED        (0),
    CLIENT_LOGGED_IN        (1),
    CLIENT_LOGGED_OUT       (2),
    ANSWER                  (3),
    RAW_MESSAGE_RECEIVED    (4);
    
    /**
     * Value to serialize
     */
    private final int value;
    
    InputType(int value) {
        this.value = value;
    }
    
    /**
     * Returns the Value
     * @return Value
     */
    public final int getValue() {
        return value;
    }
    
    /**
     * Returns the InputType of a Value
     * @param value Value
     * @return InputType that matches the Value
     */
    public static final InputType ofValue(int value) {
        for(InputType inputType : values()) {
            if(inputType.getValue() == value) {
                return inputType;
            }
        }
        return null;
    }
}
