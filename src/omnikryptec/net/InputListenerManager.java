package omnikryptec.net;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import omnikryptec.util.logger.Logger;

/**
 * InputListenerManager
 * @author Panzer1119
 */
public interface InputListenerManager {
    
    final ArrayList<InputListener> inputListeners = new ArrayList<>();
    
    public String getName();
    
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
            Logger.logErr(getName() + ": FIRING EVENT ON: " + inputListener + " / " + inputListeners.size(), new Exception());
            if (executor != null) {
                executor.execute(() -> {
                    inputListener.inputReceived(event.copy());
                });
            } else {
                inputListener.inputReceived(event.copy());
            }
        });
        return this;
    }
    
}
