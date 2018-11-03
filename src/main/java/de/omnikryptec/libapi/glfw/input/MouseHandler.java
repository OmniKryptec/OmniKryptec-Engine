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

package de.omnikryptec.libapi.glfw.input;

import de.omnikryptec.util.settings.KeySettings;
import org.joml.Vector2d;
import org.lwjgl.glfw.*;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class MouseHandler implements InputHandler {
    
    private final byte[] buttons = new byte[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private final Vector2d position = new Vector2d(0.0, 0.0);
    private final Vector2d scrollOffset = new Vector2d(0.0, 0.0);
    private final MouseHandler ME = this;
    private final long window;
    private final GLFWMouseButtonCallback mouseButtonCallback;
    private final GLFWCursorPosCallback cursorPosCallback;
    private final GLFWScrollCallback scrollCallback;
    private final GLFWCursorEnterCallback cursorEnterCallback;
    private final AtomicBoolean insideWindow = new AtomicBoolean(false);
    // Temp
    private byte[] buttonsLastTime = null;
    
    public MouseHandler(long window) {
        this.window = window;
        this.mouseButtonCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                if (ME.window != window) {
                    return;
                }
                synchronized (buttons) {
                    buttons[button] = (byte) action;
                }
            }
        };
        this.cursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                if (ME.window != window) {
                    return;
                }
                synchronized (position) {
                    position.x = xpos;
                    position.y = ypos;
                }
            }
        };
        this.scrollCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                if (ME.window != window) {
                    return;
                }
                synchronized (scrollOffset) {
                    scrollOffset.x = xoffset;
                    scrollOffset.y = yoffset;
                }
            }
        };
        this.cursorEnterCallback = new GLFWCursorEnterCallback() {
            @Override
            public void invoke(long window, boolean entered) {
                if (ME.window != window) {
                    return;
                }
                insideWindow.set(entered);
            }
        };
    }
    
    @Override
    public synchronized InputHandler init() {
        GLFW.glfwSetMouseButtonCallback(window, mouseButtonCallback);
        GLFW.glfwSetCursorPosCallback(window, cursorPosCallback);
        GLFW.glfwSetScrollCallback(window, scrollCallback);
        GLFW.glfwSetCursorEnterCallback(window, cursorEnterCallback);
        return this;
    }
    
    @Override
    public synchronized InputHandler preUpdate(double currentTime, KeySettings keySettings) {
        buttonsLastTime = Arrays.copyOf(buttons, buttons.length);
        return this;
    }
    
    @Override
    public synchronized InputHandler update(double currentTime, KeySettings keySettings) {
        for (int i = 0; i < buttons.length; i++) {
            if (buttonsLastTime[i] != buttons[i]) {
                keySettings.updateKeys(currentTime, i, false);
            }
        }
        return this;
    }
    
    @Override
    public synchronized InputHandler postUpdate(double currentTime, KeySettings keySettings) {
        buttonsLastTime = null; //TODO Is this good for performance or not?
        return this;
    }
    
    @Override
    public synchronized InputHandler close() {
        mouseButtonCallback.close();
        cursorPosCallback.close();
        scrollCallback.close();
        cursorEnterCallback.close();
        return this;
    }
    
    public synchronized byte getButtonState(int buttonCode) {
        return buttons[buttonCode];
    }
    
    public synchronized boolean isButtonUnknown(int buttonCode) {
        return buttons[buttonCode] == KeySettings.KEY_UNKNOWN;
    }
    
    public synchronized boolean isButtonNothing(int buttonCode) {
        return buttons[buttonCode] == KeySettings.KEY_NOTHING;
    }
    
    public synchronized boolean isButtonReleased(int buttonCode) {
        return buttons[buttonCode] == KeySettings.KEY_RELEASED;
    }
    
    public synchronized boolean isButtonPressed(int buttonCode) {
        return buttons[buttonCode] == KeySettings.KEY_PRESSED;
    }
    
    public synchronized boolean isButtonRepeated(int buttonCode) {
        return buttons[buttonCode] == KeySettings.KEY_REPEATED;
    }
    
    public synchronized Vector2d getPosition() {
        return position;
    }
    
    public synchronized Vector2d getScrollOffset() {
        return scrollOffset;
    }
    
    public synchronized boolean isInsideWindow() {
        return insideWindow.get();
    }
    
    public int size() {
        return buttons.length;
    }
    
    public long getWindow() {
        return window;
    }
    
}
