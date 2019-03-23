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

import de.omnikryptec.event.EventBus;
import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.window.InputEvent;
import de.omnikryptec.util.settings.KeySettings;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.lwjgl.glfw.*;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class MouseHandler implements InputHandler {
    
    private final byte[] buttons = new byte[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private final Vector2d position = new Vector2d(0.0, 0.0);
    private final Vector2d scrollOffset = new Vector2d(0.0, 0.0);
//    private final long window;
//    private final GLFWMouseButtonCallback mouseButtonCallback;
//    private final GLFWCursorPosCallback cursorPosCallback;
//    private final GLFWScrollCallback scrollCallback;
//    private final GLFWCursorEnterCallback cursorEnterCallback;
    private final AtomicBoolean insideWindow = new AtomicBoolean(false);
    // Temporary variables
    private byte[] buttonsLastTime = null;

    @Override
    public boolean init(EventBus bus) {
        bus.register(this);
        return true;
    }

    @Override
    public boolean deinit(EventBus bus) {
        bus.unregister(this);
        return true;
    }
    @EventSubscription
    public void onButtonInput(InputEvent.MouseButtonEvent ev) {
        buttons[ev.button] = (byte) ev.action;
    }
    
    @EventSubscription
    public void onPosChangeEvent(InputEvent.MousePositionEvent ev) {
        position.x = ev.xPos;
        position.y = ev.yPos;
    }
    
    @EventSubscription
    public void onScrollEvent(InputEvent.MouseScrollEvent ev) {
        scrollOffset.x = ev.xChange;
        scrollOffset.y = ev.yChange;
    }
    
    @EventSubscription
    public void onCursorEnterEvent(InputEvent.CursorInWindowEvent ev) {
        insideWindow.set(ev.entered);
    }
    
//    //@Deprecated //FIXME Whats with the Callbacks? Are they necessary?
//    public MouseHandler(long window) {
//        this.window = window;
//        this.mouseButtonCallback = new GLFWMouseButtonCallback() {
//            @Override
//            public void invoke(final long window, final int button, final int action, final int mods) {
//                if (MouseHandler.this.window != window) {
//                    return;
//                }
//                synchronized (MouseHandler.this.buttons) {
//                    MouseHandler.this.buttons[button] = (byte) action;
//                }
//            }
//        };
//        this.cursorPosCallback = new GLFWCursorPosCallback() {
//            @Override
//            public void invoke(final long window, final double xpos, final double ypos) {
//                if (MouseHandler.this.window != window) {
//                    return;
//                }
//                synchronized (MouseHandler.this.position) {
//                    MouseHandler.this.position.x = xpos;
//                    MouseHandler.this.position.y = ypos;
//                }
//            }
//        };
//        this.scrollCallback = new GLFWScrollCallback() {
//            @Override
//            public void invoke(final long window, final double xoffset, final double yoffset) {
//                if (MouseHandler.this.window != window) {
//                    return;
//                }
//                synchronized (MouseHandler.this.scrollOffset) {
//                    MouseHandler.this.scrollOffset.x = xoffset;
//                    MouseHandler.this.scrollOffset.y = yoffset;
//                }
//            }
//        };
//        this.cursorEnterCallback = new GLFWCursorEnterCallback() {
//            @Override
//            public void invoke(final long window, final boolean entered) {
//                if (MouseHandler.this.window != window) {
//                    return;
//                }
//                MouseHandler.this.insideWindow.set(entered);
//            }
//        };
//    }
    
//    @Override
//    public synchronized boolean init() {
//        GLFW.glfwSetMouseButtonCallback(window, mouseButtonCallback);
//        GLFW.glfwSetCursorPosCallback(window, cursorPosCallback);
//        GLFW.glfwSetScrollCallback(window, scrollCallback);
//        GLFW.glfwSetCursorEnterCallback(window, cursorEnterCallback);
//        return true;
//    }
    
    @Override
    public synchronized boolean preUpdate(double currentTime, KeySettings keySettings) {
        buttonsLastTime = Arrays.copyOf(buttons, buttons.length);
        return true;
    }
    
    @Override
    public synchronized boolean update(double currentTime, KeySettings keySettings) {
        for (int i = 0; i < buttons.length; i++) {
            if (buttonsLastTime[i] != buttons[i]) {
                keySettings.updateKeys(currentTime, i, false, buttons[i]);
            }
        }
        return true;
    }
    
    @Override
    public synchronized boolean postUpdate(double currentTime, KeySettings keySettings) {
        //buttonsLastTime = null; // Is this good for performance or not? // Makes no sense
        return true;
    }
    
    @Override
    public synchronized boolean close() {
//        mouseButtonCallback.close();
//        cursorPosCallback.close();
//        scrollCallback.close();
//        cursorEnterCallback.close();
        return true;
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
    
    public synchronized Vector2dc getPosition() {
        return position;
    }
    
    public synchronized Vector2dc getScrollOffset() {
        return scrollOffset;
    }
    
    public synchronized boolean isInsideWindow() {
        return insideWindow.get();
    }
    
    public int size() {
        return buttons.length;
    }
    
//    public long getWindow() {
//        return window;
//    }

    
}
