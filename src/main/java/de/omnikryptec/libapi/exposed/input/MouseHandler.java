/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.libapi.exposed.input;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import de.omnikryptec.event.EventBus;
import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.window.InputEvent;
import de.omnikryptec.util.settings.KeySettings;

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
    // Temporary variables
    private byte[] buttonsLastTime = null;
    
    public MouseHandler(EventBus bus) {
        this(0);
        bus.register(this);
    }
    
    @EventSubscription
    public void onButtonInput(InputEvent.MouseButtonEvent ev) {
        this.buttons[ev.button] = (byte) ev.action;
    }
    
    @EventSubscription
    public void onPosChangeEvent(InputEvent.MousePositionEvent ev) {
        this.position.x = ev.xPos;
        this.position.y = ev.yPos;
    }
    
    @EventSubscription
    public void onScrollEvent(InputEvent.MouseScrollEvent ev) {
        this.scrollOffset.x = ev.xChange;
        this.scrollOffset.y = ev.yChange;
    }
    
    @EventSubscription
    public void onCursorEnterEvent(InputEvent.CursorInWindowEvent ev) {
        this.insideWindow.set(ev.entered);
    }
    
    public MouseHandler(final long window) {
        this.window = window;
        this.mouseButtonCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(final long window, final int button, final int action, final int mods) {
                if (MouseHandler.this.ME.window != window) {
                    return;
                }
                synchronized (MouseHandler.this.buttons) {
                    MouseHandler.this.buttons[button] = (byte) action;
                }
            }
        };
        this.cursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(final long window, final double xpos, final double ypos) {
                if (MouseHandler.this.ME.window != window) {
                    return;
                }
                synchronized (MouseHandler.this.position) {
                    MouseHandler.this.position.x = xpos;
                    MouseHandler.this.position.y = ypos;
                }
            }
        };
        this.scrollCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(final long window, final double xoffset, final double yoffset) {
                if (MouseHandler.this.ME.window != window) {
                    return;
                }
                synchronized (MouseHandler.this.scrollOffset) {
                    MouseHandler.this.scrollOffset.x = xoffset;
                    MouseHandler.this.scrollOffset.y = yoffset;
                }
            }
        };
        this.cursorEnterCallback = new GLFWCursorEnterCallback() {
            @Override
            public void invoke(final long window, final boolean entered) {
                if (MouseHandler.this.ME.window != window) {
                    return;
                }
                MouseHandler.this.insideWindow.set(entered);
            }
        };
    }
    
    @Override
    public synchronized InputHandler init() {
        GLFW.glfwSetMouseButtonCallback(this.window, this.mouseButtonCallback);
        GLFW.glfwSetCursorPosCallback(this.window, this.cursorPosCallback);
        GLFW.glfwSetScrollCallback(this.window, this.scrollCallback);
        GLFW.glfwSetCursorEnterCallback(this.window, this.cursorEnterCallback);
        return this;
    }
    
    @Override
    public synchronized InputHandler preUpdate(final double currentTime, final KeySettings keySettings) {
        this.buttonsLastTime = Arrays.copyOf(this.buttons, this.buttons.length);
        return this;
    }
    
    @Override
    public synchronized InputHandler update(final double currentTime, final KeySettings keySettings) {
        for (int i = 0; i < this.buttons.length; i++) {
            if (this.buttonsLastTime[i] != this.buttons[i]) {
                keySettings.updateKeys(currentTime, i, false, this.buttons[i]);
            }
        }
        return this;
    }
    
    @Override
    public synchronized InputHandler postUpdate(final double currentTime, final KeySettings keySettings) {
        //this.buttonsLastTime = null; // Is this good for performance or not? // Makes no sense
        return this;
    }
    
    @Override
    public synchronized InputHandler close() {
        this.mouseButtonCallback.close();
        this.cursorPosCallback.close();
        this.scrollCallback.close();
        this.cursorEnterCallback.close();
        return this;
    }
    
    public synchronized byte getButtonState(final int buttonCode) {
        return this.buttons[buttonCode];
    }
    
    public synchronized boolean isButtonUnknown(final int buttonCode) {
        return this.buttons[buttonCode] == KeySettings.KEY_UNKNOWN;
    }
    
    public synchronized boolean isButtonNothing(final int buttonCode) {
        return this.buttons[buttonCode] == KeySettings.KEY_NOTHING;
    }
    
    public synchronized boolean isButtonReleased(final int buttonCode) {
        return this.buttons[buttonCode] == KeySettings.KEY_RELEASED;
    }
    
    public synchronized boolean isButtonPressed(final int buttonCode) {
        return this.buttons[buttonCode] == KeySettings.KEY_PRESSED;
    }
    
    public synchronized boolean isButtonRepeated(final int buttonCode) {
        return this.buttons[buttonCode] == KeySettings.KEY_REPEATED;
    }
    
    public synchronized Vector2dc getPosition() {
        return this.position;
    }
    
    public synchronized Vector2dc getScrollOffset() {
        return this.scrollOffset;
    }
    
    public synchronized boolean isInsideWindow() {
        return this.insideWindow.get();
    }
    
    public int size() {
        return this.buttons.length;
    }
    
    public long getWindow() {
        return this.window;
    }
    
}
