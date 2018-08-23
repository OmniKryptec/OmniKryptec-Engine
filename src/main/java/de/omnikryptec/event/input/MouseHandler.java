package de.omnikryptec.event.input;

import de.omnikryptec.settings.KeySettings;
import org.joml.Vector2f;
import org.lwjgl.glfw.*;

import java.util.Arrays;

/**
 * MouseHandler
 *
 * @author Panzer1119
 */
public class MouseHandler implements InputHandler {

	private final MouseHandler ME = this;
	private final long window;
	private final GLFWMouseButtonCallback mouseButtonCallback;
	private final GLFWCursorPosCallback cursorPosCallback;
	private final GLFWScrollCallback scrollCallback;
	private final GLFWCursorEnterCallback cursorEnterCallback;
	protected final InputState[] buttons = new InputState[100];
	private InputState[] buttons_lastTime = null;
	protected final Vector2f position = new Vector2f();
	protected final Vector2f scrollOffset = new Vector2f();
	protected boolean insideWindow = false;

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
				insideWindow = entered;
			}
		};
	}

	// damit nicht unused
	final GLFWCursorEnterCallback __getGLFWCEC() {
		return cursorEnterCallback;
	}

	public final MouseHandler initCallbacks() {
		initMouseButtonCallback();
		initCursorPosCallback();
		initScrollCallback();
		return this;
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

	@Override
	public final MouseHandler close() {
		closeMouseButtonCallback();
		closeCursorPosCallback();
		closeScrollCallback();
		return this;
	}

	public final MouseHandler closeMouseButtonCallback() {
		mouseButtonCallback.close();
		return this;
	}

	public final MouseHandler closeCursorPosCallback() {
		cursorPosCallback.close();
		return this;
	}

	public final MouseHandler closeScrollCallback() {
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

	public final boolean isInsideWindow() {
		return insideWindow;
	}

	@Override
	public final MouseHandler preUpdate() {
		buttons_lastTime = Arrays.copyOf(buttons, buttons.length);
		return this;
	}

	@Override
	public final MouseHandler updateKeySettings(double currentTime, KeySettings keySettings) {
		for (int i = 0; i < buttons.length; i++) {
			if (buttons_lastTime[i] != buttons[i]) {
				keySettings.updateKeys(currentTime, i, false);
			}
		}
		return this;
	}

}
