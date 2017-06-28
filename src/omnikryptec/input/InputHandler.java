package omnikryptec.input;

import omnikryptec.settings.KeySettings;

/**
 * InputHandler
 *
 * @author Panzer1119
 */
public interface InputHandler {

    public InputHandler preUpdate();

    public InputHandler close();

    public InputHandler updateKeySettings(double currentTime, KeySettings keySettings);

}
