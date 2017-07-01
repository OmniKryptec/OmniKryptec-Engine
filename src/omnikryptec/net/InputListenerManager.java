package omnikryptec.net;

import java.util.ArrayList;

/**
 * InputListenerManager
 * @author Panzer1119
 */
public interface InputListenerManager {
    
    final ArrayList<InputListener> inputListeners = new ArrayList<>();
    
    default InputListenerManager addInputListener(InputListener inputListener) {
        if(inputListener == null || inputListeners.contains(inputListener)) {
            return this;
        }
        inputListeners.add(inputListener);
        return this;
    }
    
    default InputListenerManager removeInputListener(InputListener inputListener) {
        if(inputListener == null || !inputListeners.contains(inputListener)) {
            return this;
        }
        inputListeners.remove(inputListener);
        return this;
    }
    
    default InputListenerManager fireInputEvent(InputEvent event) {
        inputListeners.stream().forEach((inputListener) -> {
            inputListener.inputReceived(event);
        });
        return this;
    }
    
}
