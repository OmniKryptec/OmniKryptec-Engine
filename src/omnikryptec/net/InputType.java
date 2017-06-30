package omnikryptec.net;

/**
 * InputType
 * @author Panzer1119
 */
public enum InputType {
    MESSAGE_RECEIVED    (0),
    CLIENT_LOGGED_IN    (1),
    CLIENT_LOGGED_OUT   (2);
    
    private final int value;
    
    InputType(int value) {
        this.value = value;
    }
    
    public final int getValue() {
        return value;
    }
    
    public static final InputType ofValue(int value) {
        for(InputType inputType : values()) {
            if(inputType.getValue() == value) {
                return inputType;
            }
        }
        return null;
    }
}
