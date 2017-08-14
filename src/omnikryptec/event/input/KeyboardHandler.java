package omnikryptec.event.input;

import java.util.Arrays;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import omnikryptec.settings.KeySettings;

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
    private String inputString = "";

    public KeyboardHandler(long window) {
        this.window = window;
        this.keyCallback = new GLFWKeyCallback() {
            @Override
            public final synchronized void invoke(long window, int key, int scancode, int action, int mods) {
                if (ME.window != window) {
                    return;
                }
                final InputState inputState = InputState.ofState(action);
                keys[key] = inputState;
                if(inputState == InputState.PRESSED || inputState == InputState.REPEATED) {
                    final String keyString = GLFW.glfwGetKeyName(key, scancode); //FIXME Da stand irgendwas von, dass man das nicht benutzen soll?
                    if(keyString != null) {
                        inputString += keyString;
                    }
                }
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
    
    public final synchronized KeyboardHandler resetInputString() {
        inputString = "";
        return this;
    }
    
    public final synchronized String getInputString() {
        return inputString;
    }

}
