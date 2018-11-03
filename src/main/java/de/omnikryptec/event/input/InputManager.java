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

import de.omnikryptec.libapi.glfw.LibAPIManager;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.settings.KeySettings;
import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;

public class InputManager {
    
    private final long window;
    // Keyboard part
    private final KeyboardHandler keyboardHandler;
    private boolean longButtonPressEnabled = false;
    // Mouse part
    private final MouseHandler mouseHandler;
    private CursorType cursorType = CursorType.DISABLED;
    
    public InputManager(long window) {
        this(window, new KeyboardHandler(window), new MouseHandler(window));
    }
    
    public InputManager(long window, KeyboardHandler keyboardHandler, MouseHandler mouseHandler) {
        this.window = window;
        this.keyboardHandler = keyboardHandler;
        this.mouseHandler = mouseHandler;
    }
    
    public KeyboardHandler getKeyboardHandler() {
        return keyboardHandler;
    }
    
    public MouseHandler getMouseHandler() {
        return mouseHandler;
    }
    
    public InputManager init() {
        keyboardHandler.init();
        mouseHandler.init();
        return this;
    }
    
    public InputManager preUpdate() {
        final double currentTime = LibAPIManager.active().getTime();
        final KeySettings keySettings = null; //FIXME KeySettings missing!
        if (longButtonPressEnabled) {
            keyboardHandler.preUpdate(currentTime, keySettings);
            mouseHandler.preUpdate(currentTime, keySettings);
        }
        //TODO JoystickHandler needs to be updated anyway
        return this;
    }
    
    public InputManager update() {
        final double currentTime = LibAPIManager.active().getTime();
        keyboardHandler.clearInputString();
        final KeySettings keySettings = null; //FIXME KeySettings missing!
        if (longButtonPressEnabled) {
            keyboardHandler.update(currentTime, keySettings);
            mouseHandler.update(currentTime, keySettings);
        }
        //TODO JoystickHandler needs to be updated anyway
        return this;
    }
    
    public InputManager postUpdate() {
        final double currentTime = LibAPIManager.active().getTime();
        final KeySettings keySettings = null; //FIXME KeySettings missing!
        if (longButtonPressEnabled) {
            keyboardHandler.postUpdate(currentTime, keySettings);
            mouseHandler.postUpdate(currentTime, keySettings);
        }
        //TODO JoystickHandler needs to be updated anyway
        return this;
    }
    
    public InputManager close() {
        keyboardHandler.close();
        mouseHandler.close();
        return this;
    }
    
    public boolean isLongButtonPressEnabled() {
        return longButtonPressEnabled;
    }
    
    public InputManager setLongButtonPressEnabled(boolean longButtonPressEnabled) {
        this.longButtonPressEnabled = longButtonPressEnabled;
        return this;
    }
    
    public long getWindow() {
        return window;
    }
    
    // Keyboard part
    
    public byte getKeyboardKeyState(int keyCode) {
        if (keyCode < 0 || keyCode >= keyboardHandler.size()) {
            return KeySettings.KEY_UNKNOWN;
        }
        return keyboardHandler.getKeyState(keyCode);
    }
    
    public boolean isKeyboardKeyPressed(int keyCode) {
        if (keyCode < 0 || keyCode >= keyboardHandler.size()) {
            return false;
        }
        return keyboardHandler.isKeyPressed(keyCode) || keyboardHandler.isKeyRepeated(keyCode);
    }
    
    // Mouse part
    
    public byte getMouseButtonState(int buttonCode) {
        if (buttonCode < 0 || buttonCode >= mouseHandler.size()) {
            return KeySettings.KEY_UNKNOWN;
        }
        return mouseHandler.getButtonState(buttonCode);
    }
    
    public boolean isMouseButtonPressed(int buttonCode) {
        if (buttonCode < 0 || buttonCode >= mouseHandler.size()) {
            return false;
        }
        return mouseHandler.isButtonPressed(buttonCode); // || mouseHandler.isButtonRepeated(buttonCode); //TODO Can a mouse buttons state be "REPEATED"?
    }
    
    public Vector2d getMousePosition() {
        return mouseHandler.getPosition();
    }
    
    public Vector2d getMouseScrollOffset() {
        return mouseHandler.getScrollOffset();
    }
    
    public boolean isMouseInsideWindow() {
        return mouseHandler.isInsideWindow();
    }
    
    public CursorType getCursorType() {
        return cursorType;
    }
    
    public InputManager setCursorType(CursorType cursorType) {
        Util.ensureNonNull(cursorType);
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, cursorType.getState());
        this.cursorType = cursorType;
        return this;
    }
    
}
