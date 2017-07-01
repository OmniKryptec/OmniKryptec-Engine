package omnikryptec.net;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

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
    
    default InputListenerManager fireInputEvent(InputEvent event, ExecutorService executor) {
        inputListeners.stream().forEach((inputListener) -> {
            executor.execute(() -> {
                inputListener.inputReceived(event);
            });
        });
        return this;
    }
    
}
