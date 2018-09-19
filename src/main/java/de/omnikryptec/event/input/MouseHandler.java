/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.event.input;

import de.omnikryptec.display.Display;
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
    
    protected final InputState[] buttons = new InputState[100];
    protected final Vector2f position = new Vector2f();
    protected final Vector2f scrollOffset = new Vector2f();
    private final MouseHandler ME = this;
    private final long window;
    private final GLFWMouseButtonCallback mouseButtonCallback;
    private final GLFWCursorPosCallback cursorPosCallback;
    private final GLFWScrollCallback scrollCallback;
    private final GLFWCursorEnterCallback cursorEnterCallback;
    protected boolean insideWindow = false;
    private InputState[] buttons_lastTime = null;
    
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
    
    public final synchronized InputState getButtonState(int buttonCode) {
        return buttons[buttonCode];
    }
    
    public final synchronized boolean isButtonNothing(int buttonCode) {
        return buttons[buttonCode] == InputState.NOTHING;
    }
    
    public final synchronized boolean isButtonReleased(int buttonCode) {
        return buttons[buttonCode] == InputState.RELEASED;
    }
    
    public final synchronized boolean isButtonPressed(int buttonCode) {
        return buttons[buttonCode] == InputState.PRESSED;
    }
    
    public final synchronized boolean isKeyRepeated(int buttonCode) {
        return false;
    }
    
    public final synchronized Vector2f getPosition() {
        return position;
    }
    
    public final synchronized float getPosRefinedX() {
        float f = position.x - Display.getViewportData()[0];
        if (f > Display.getViewportData()[2] || f < 0) {
            return -1;
        }
        return f;
    }
    
    public final synchronized float getPosRefinedY() {
        float f = position.y - Display.getViewportData()[1];
        if (f > Display.getViewportData()[3] || f < 0) {
            return -1;
        }
        return Display.getViewportData()[3] - f;
    }
    
    public final synchronized Vector2f getPosInVP() {
        return new Vector2f(getPosRefinedX(), getPosRefinedY());
    }
    
    public final synchronized Vector2f getScrollOffset() {
        return scrollOffset;
    }
    
    public final synchronized boolean isInsideWindow() {
        return insideWindow;
    }
    
    @Override
    public final synchronized MouseHandler preUpdate() {
        buttons_lastTime = Arrays.copyOf(buttons, buttons.length);
        return this;
    }
    
    @Override
    public final synchronized MouseHandler updateKeySettings(double currentTime, KeySettings keySettings) {
        for (int i = 0; i < buttons.length; i++) {
            if (buttons_lastTime[i] != buttons[i]) {
                keySettings.updateKeys(currentTime, i, false);
            }
        }
        return this;
    }
    
}
