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

import de.omnikryptec.libapi.glfw.LibAPIManager;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.settings.KeySettings;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector4d;
import org.joml.Vector4dc;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.atomic.AtomicReference;

public class InputManager {
    
    private final long window;
    private final AtomicReference<Double> currentTime = new AtomicReference<>(0.0);
    // Keyboard part
    private final KeyboardHandler keyboardHandler;
    // Mouse part
    private final MouseHandler mouseHandler;
    // Mouse delta part
    private final Vector2d mousePositionLastTime = new Vector2d(0.0, 0.0);
    private final Vector2d mouseScrollOffsetLastTime = new Vector2d(0.0, 0.0);
    private final Vector2d mousePositionDelta = new Vector2d(0.0, 0.0);
    private final Vector2d mouseScrollOffsetDelta = new Vector2d(0.0, 0.0);
    /**
     * x = Mouse Pos X Delta <br>
     * y = Mouse Pos Y Delta <br>
     * z = Mouse Scroll X Delta <br>
     * w = Mouse Scroll Y Delta
     */
    private final Vector4d mouseDelta = new Vector4d(0.0, 0.0, 0.0, 0.0);
    private boolean longButtonPressEnabled = false;
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
        // final double currentTime = LibAPIManager.active().getTime(); //TODO Remove
        currentTime.set(LibAPIManager.active().getTime());
        final double currentTime_ = currentTime.get();
        final KeySettings keySettings = null; // FIXME KeySettings missing!
        if (longButtonPressEnabled) {
            keyboardHandler.preUpdate(currentTime_, keySettings);
            mouseHandler.preUpdate(currentTime_, keySettings);
        }
        JoystickHandler.preUpdateAll(currentTime_, keySettings);
        return this;
    }
    
    public InputManager update() {
        // final double currentTime = LibAPIManager.active().getTime();
        final double currentTime_ = currentTime.get();
        keyboardHandler.clearInputString();
        final KeySettings keySettings = null; // FIXME KeySettings missing!
        if (longButtonPressEnabled) {
            keyboardHandler.update(currentTime_, keySettings);
            mouseHandler.update(currentTime_, keySettings);
        }
        JoystickHandler.updateAll(currentTime_, keySettings);
        return this;
    }
    
    public InputManager postUpdate() {
        // final double currentTime = LibAPIManager.active().getTime();
        final double currentTime_ = currentTime.get();
        final KeySettings keySettings = null; // FIXME KeySettings missing!
        if (longButtonPressEnabled) {
            keyboardHandler.postUpdate(currentTime_, keySettings);
            mouseHandler.postUpdate(currentTime_, keySettings);
        }
        JoystickHandler.postUpdateAll(currentTime_, keySettings);
        return this;
    }
    
    public InputManager close() {
        keyboardHandler.close();
        mouseHandler.close();
        JoystickHandler.closeAll();
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
    
    public Vector2dc getMousePosition() {
        return mouseHandler.getPosition();
    }
    
    public Vector2dc getMouseScrollOffset() {
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
    
    // Mouse delta part
    
    private void updateMouseDeltas(Vector2d mousePosition, Vector2d mouseScrollOffset) {
        mousePositionDelta.x = (mousePosition.x - mousePositionLastTime.x);
        mousePositionDelta.y = (mousePosition.y - mousePositionLastTime.y);
        mouseScrollOffsetDelta.x = (mouseScrollOffset.x - mouseScrollOffsetLastTime.x);
        mouseScrollOffsetDelta.y = (mouseScrollOffset.y - mouseScrollOffsetLastTime.y);
        mouseDelta.x = mousePositionDelta.x;
        mouseDelta.y = mousePositionDelta.y;
        mouseDelta.z = mouseScrollOffsetDelta.x;
        mouseDelta.w = mouseScrollOffsetDelta.y;
        // TODO Maybe split this up, because this below was executed as the last part in the old update ("nextFrame") method
        mousePositionLastTime.x = mousePosition.x;
        mousePositionLastTime.y = mousePosition.y;
        mouseScrollOffsetLastTime.x = mouseScrollOffset.x;
        mouseScrollOffsetLastTime.y = mouseScrollOffset.y;
    }
    
    public Vector2dc getMousePositionDelta() {
        return mousePositionDelta;
    }
    
    public Vector2dc getMouseScrollOffsetDelta() {
        return mouseScrollOffsetDelta;
    }
    
    public Vector4dc getMouseDelta() {
        return mouseDelta;
    }
    
}
