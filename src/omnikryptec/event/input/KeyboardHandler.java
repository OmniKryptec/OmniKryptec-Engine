package omnikryptec.event.input;

import java.util.Arrays;
import omnikryptec.settings.KeySettings;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

/**
 * KeyboardHandler
 *
 * @author Panzer1119
 */
public class KeyboardHandler implements InputHandler {

    private final KeyboardHandler ME = this;
    private final long window;
    private final GLFWKeyCallback keyCallback;
    protected final InputState[] keys = new InputState[65536];
    private InputState[] keys_lastTime = null;

    public KeyboardHandler(long window) {
        this.window = window;
        this.keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (ME.window != window) {
                    return;
                }
                keys[key] = InputState.ofState(action);
            }
        };
    }

    public final GLFWKeyCallback initKeybCallback() {
        GLFW.glfwSetKeyCallback(window, keyCallback);
        return keyCallback;
    }
    
    @Override
    public final KeyboardHandler close() {
        keyCallback.close();
        return this;
    }

    public final InputState getKeyState(int keyCode) {
        return keys[keyCode];
    }

    public final boolean isKeyNothing(int keyCode) {
        return keys[keyCode] == InputState.NOTHING;
    }

    public final boolean isKeyReleased(int keyCode) {
        return keys[keyCode] == InputState.RELEASED;
    }

    public final boolean isKeyPressed(int keyCode) {
        return keys[keyCode] == InputState.PRESSED;
    }

    public final boolean isKeyRepeated(int keyCode) {
        return keys[keyCode] == InputState.REPEATED;
    }

    @Override
    public final KeyboardHandler preUpdate() {
        keys_lastTime = Arrays.copyOf(keys, keys.length);
        return this;
    }
    
    @Override
    public final KeyboardHandler updateKeySettings(double currentTime, KeySettings keySettings) {
        for (int i = 0; i < keys.length; i++) {
            if (keys_lastTime[i] != keys[i]) {
                keySettings.updateKeys(currentTime, i, true);
            }
        }
        return this;
    }

}
