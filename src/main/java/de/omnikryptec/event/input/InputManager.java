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
import org.lwjgl.glfw.GLFW;

public class InputManager {
    
    private final long window;
    // Keyboard part
    private final KeyboardHandler keyboardHandler;
    private boolean longButtonPressEnabled = false;
    // Mouse part
    private CursorType cursorType = CursorType.DISABLED;
    
    public InputManager(long window) {
        this(window, new KeyboardHandler(window));
    }
    
    public InputManager(long window, KeyboardHandler keyboardHandler) {
        this.window = window;
        this.keyboardHandler = keyboardHandler;
    }
    
    public KeyboardHandler getKeyboardHandler() {
        return keyboardHandler;
    }
    
    public InputManager init() {
        keyboardHandler.init();
        return this;
    }
    
    public InputManager preUpdate() {
        final double currentTime = LibAPIManager.active().getTime();
        if (longButtonPressEnabled) {
            keyboardHandler.preUpdate(currentTime, null); //FIXME KeySettings missing!
        }
        return this;
    }
    
    public InputManager update() {
        final double currentTime = LibAPIManager.active().getTime();
        if (longButtonPressEnabled) {
            keyboardHandler.update(currentTime, null); //FIXME KeySettings missing!
        }
        return this;
    }
    
    public InputManager postUpdate() {
        final double currentTime = LibAPIManager.active().getTime();
        if (longButtonPressEnabled) {
            keyboardHandler.postUpdate(currentTime, null); //FIXME KeySettings missing!
        }
        return this;
    }
    
    public InputManager close() {
        keyboardHandler.close();
        return this;
    }
    
    public boolean isLongButtonPressEnabled() {
        return longButtonPressEnabled;
    }
    
    public InputManager setLongButtonPressEnabled(boolean longButtonPressEnabled) {
        this.longButtonPressEnabled = longButtonPressEnabled;
        return this;
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
    
    public byte getKeyboardKeyState(int keyCode) {
        if (keyCode < 0 || keyCode >= keyboardHandler.size()) {
            return KeyboardHandler.KEY_UNKNOWN;
        }
        return keyboardHandler.getKeyState(keyCode);
    }
    
}
