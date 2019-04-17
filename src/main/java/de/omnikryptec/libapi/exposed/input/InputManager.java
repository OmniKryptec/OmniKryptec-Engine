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

import de.omnikryptec.core.update.ILayer;
import de.omnikryptec.core.update.IUpdatable;
import de.omnikryptec.event.EventBus;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.settings.KeySettings;
import de.omnikryptec.util.updater.Time;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector4d;
import org.joml.Vector4dc;

public class InputManager implements IUpdatable {
    
    private KeySettings keySettings;
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
    
    public InputManager(KeySettings keySettings) {
        this(keySettings, new KeyboardHandler(), new MouseHandler());
    }
    
    protected InputManager(KeySettings keySettings, KeyboardHandler keyboardHandler, MouseHandler mouseHandler) {
        this.keySettings = keySettings;
        this.keyboardHandler = keyboardHandler;
        this.mouseHandler = mouseHandler;
    }
    
    public KeySettings getKeySettings() {
        return keySettings;
    }
    
    public KeyboardHandler getKeyboardHandler() {
        return keyboardHandler;
    }
    
    public MouseHandler getMouseHandler() {
        return mouseHandler;
    }
    
    protected boolean init(EventBus bus) {
        boolean good = true;
        if (!keyboardHandler.init(bus)) {
            good = false;
        }
        if (!mouseHandler.init(bus)) {
            good = false;
        }
        return good;
    }
    
    protected boolean deinit(EventBus bus) {
        boolean good = true;
        if (!keyboardHandler.deinit(bus)) {
            good = false;
        }
        if (!mouseHandler.deinit(bus)) {
            good = false;
        }
        return good;
    }
    
    protected boolean preUpdateIntern(Time time) {
        boolean good = true;
        if (longButtonPressEnabled) {
            if (!keyboardHandler.preUpdate(time.current, keySettings)) {
                good = false;
            }
            if (!mouseHandler.preUpdate(time.current, keySettings)) {
                good = false;
            }
        }
        if (!JoystickHandler.preUpdateAll(time.current, keySettings)) {
            good = false;
        }
        return good;
    }
    
    protected boolean updateIntern(Time time) {
        keyboardHandler.clearInputString();
        boolean good = true;
        if (longButtonPressEnabled) {
            if (!keyboardHandler.update(time.current, keySettings)) {
                good = false;
            }
            if (!mouseHandler.update(time.current, keySettings)) {
                good = false;
            }
        }
        if (!JoystickHandler.updateAll(time.current, keySettings)) {
            good = false;
        }
        return good;
    }
    
    protected boolean postUpdateIntern(Time time) {
        boolean good = true;
        if (longButtonPressEnabled) {
            if (!keyboardHandler.postUpdate(time.current, keySettings)) {
                good = false;
            }
            if (!mouseHandler.postUpdate(time.current, keySettings)) {
                good = false;
            }
        }
        if (!JoystickHandler.postUpdateAll(time.current, keySettings)) {
            good = false;
        }
        return good;
    }
    
    public boolean close() {
        boolean good = true;
        if (!keyboardHandler.close()) {
            good = false;
        }
        if (!mouseHandler.close()) {
            good = false;
        }
        if (!JoystickHandler.closeAll()) {
            good = false;
        }
        return good;
    }
    
    @Override
    public boolean passive() {
        return false;
    }
    
    @Override
    public void update(Time time) {
        preUpdateIntern(time);
        updateIntern(time);
        postUpdateIntern(time);
    }
    
    @Override
    public void init(ILayer layer) {
        init(layer.getEventBus());
    }
    
    @Override
    public void deinit(ILayer layer) {
        deinit(layer.getEventBus());
    }
    
    public boolean isLongButtonPressEnabled() {
        return longButtonPressEnabled;
    }
    
    public InputManager setLongButtonPressEnabled(boolean longButtonPressEnabled) {
        this.longButtonPressEnabled = longButtonPressEnabled;
        return this;
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
        //FIXME move to different position, why?
        Util.ensureNonNull(cursorType);
        //GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, cursorType.getState());
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
        //FIXME Maybe split this up, because this below was executed as the last part in the old update ("nextFrame") method, needs discussion?
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
