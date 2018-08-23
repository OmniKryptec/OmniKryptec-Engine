package de.omnikryptec.event.input;

import org.lwjgl.glfw.GLFW;

/**
 * InputState
 *
 * @author Panzer1119
 */
public enum InputState {

    NOTHING(-1),
    RELEASED(GLFW.GLFW_RELEASE),
    PRESSED(GLFW.GLFW_PRESS),
    REPEATED(GLFW.GLFW_REPEAT);

    private final int state;

    InputState(int state) {
        this.state = state;
    }

    public final int getState() {
        return state;
    }

    public static final InputState ofState(int state) {
        for (InputState inputState : values()) {
            if (inputState.getState() == state) {
                return inputState;
            }
        }
        return null;
    }

}
