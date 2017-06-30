package omnikryptec.net;

import java.io.Serializable;

/**
 * InputListener
 * @author Panzer1119
 */
public interface InputListener extends Serializable {
    
    /**
     * Called on received Inputs
     * @param event InputEvent
     */
    public void inputReceived(InputEvent event);
    
}
