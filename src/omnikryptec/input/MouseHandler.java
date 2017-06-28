package omnikryptec.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.util.vector.Vector2f;

/**
 * MouseHandler
 *
 * @author Panzer1119
 */
public class MouseHandler {

    private final MouseHandler ME = this;
    private final long window;
    private final GLFWMouseButtonCallback mouseButtonCallback;
    private final GLFWCursorPosCallback cursorPosCallback;
    private final GLFWScrollCallback scrollCallback;
    private final GLFWCursorEnterCallback cursorEnterCallback;
    public final InputState[] buttons = new InputState[100];
    public final Vector2f position = new Vector2f();
    public final Vector2f scrollOffset = new Vector2f();
    public boolean entered = false;

    public MouseHandler(long window) {
        this.window = window;
        this.mouseButtonCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                if (ME.window != window) {
                    return;
                }
                buttons[button] = InputState.ofState(action);
            }
        };
        this.cursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                if (ME.window != window) {
                    return;
                }
                position.x = (float) xpos;
                position.y = (float) ypos;
            }
        };
        this.scrollCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                if (ME.window != window) {
                    return;
                }
                scrollOffset.x = (float) xoffset;
                scrollOffset.y = (float) yoffset;
            }
        };
        this.cursorEnterCallback = new GLFWCursorEnterCallback() {
            @Override
            public void invoke(long window, boolean entered) {
                if (ME.window != window) {
                    return;
                }
                ME.entered = entered;
            }
        };
    }

    public final GLFWMouseButtonCallback initMouseButtonCallback() {
        GLFW.glfwSetMouseButtonCallback(window, mouseButtonCallback);
        return mouseButtonCallback;
    }

    public final GLFWCursorPosCallback initCursorPosCallback() {
        GLFW.glfwSetCursorPosCallback(window, cursorPosCallback);
        return cursorPosCallback;
    }

    public final GLFWScrollCallback initScrollCallback() {
        GLFW.glfwSetScrollCallback(window, scrollCallback);
        return scrollCallback;
    }

    public final MouseHandler close() {
        mouseButtonCallback.close();
        cursorPosCallback.close();
        scrollCallback.close();
        return this;
    }

    public final InputState getButtonState(int buttonCode) {
        return buttons[buttonCode];
    }

    public final boolean isButtonNothing(int buttonCode) {
        return buttons[buttonCode] == InputState.NOTHING;
    }

    public final boolean isButtonReleased(int buttonCode) {
        return buttons[buttonCode] == InputState.RELEASED;
    }

    public final boolean isButtonPressed(int buttonCode) {
        return buttons[buttonCode] == InputState.PRESSED;
    }

    public final boolean isKeyRepeated(int buttonCode) {
        return false;
    }

    public final Vector2f getPosition() {
        return position;
    }

    public final Vector2f getScrollOffset() {
        return scrollOffset;
    }

    public final boolean isEntered() {
        return entered;
    }

}
