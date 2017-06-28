package omnikryptec.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

/**
 * KeyboardHandler
 *
 * @author Panzer1119
 */
public class KeyboardHandler {

    private final KeyboardHandler ME = this;
    private final long window;
    private final GLFWKeyCallback keyCallback;
    public final InputState[] keys = new InputState[65536];

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

}
