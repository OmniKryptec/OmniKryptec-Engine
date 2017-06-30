package omnikryptec.event.input;

import org.lwjgl.glfw.GLFW;

/**
 * CursorType
 *
 * @author Panzer1119
 */
public enum CursorType {
    NORMAL      (GLFW.GLFW_CURSOR_NORMAL),
    HIDDEN      (GLFW.GLFW_CURSOR_HIDDEN),
    DISABLED    (GLFW.GLFW_CURSOR_DISABLED);

    private final int state;

    CursorType(int state) {
        this.state = state;
    }

    public final int getState() {
        return state;
    }

    public static final CursorType ofState(int state) {
        for (CursorType cursorType : values()) {
            if (cursorType.getState() == state) {
                return cursorType;
            }
        }
        return null;
    }
}
