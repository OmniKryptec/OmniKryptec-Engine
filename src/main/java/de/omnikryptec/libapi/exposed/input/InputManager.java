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

import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector4d;
import org.joml.Vector4dc;
import org.lwjgl.glfw.GLFW;

import de.omnikryptec.core.Updateable;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.settings.KeySettings;
import de.omnikryptec.util.updater.Time;

public class InputManager implements Updateable {
    
    /**
     * The currently set {@code InputManager}.
     * <p>
     * Note: this is the same as calling<br>
     * {@code LibAPIManager.instance().getInputManager()}
     * </p>
     * 
     * @return the current {@code InputManager}
     */
    public static InputManager get() {
        return LibAPIManager.instance().getInputManager();
    }
    
    private final long window;
    private KeySettings keySettings;
    //private final AtomicReference<Double> currentTime = new AtomicReference<>(0.0); //TODO Clean this
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
    
    public InputManager(final long window, KeySettings keySettings) {
        this(window, keySettings, new KeyboardHandler(window), new MouseHandler(window));
    }
    
    public InputManager(final long window, KeySettings keySettings, final KeyboardHandler keyboardHandler,
            final MouseHandler mouseHandler) {
        this.window = window;
        this.keySettings = keySettings;
        this.keyboardHandler = keyboardHandler;
        this.mouseHandler = mouseHandler;
    }
    
    public KeySettings getKeySettings() {
        return keySettings;
    }
    
    public KeyboardHandler getKeyboardHandler() {
        return this.keyboardHandler;
    }
    
    public MouseHandler getMouseHandler() {
        return this.mouseHandler;
    }
    
    public InputManager init() {
        this.keyboardHandler.init();
        this.mouseHandler.init();
        return this;
    }
    
    @Override
    public void preUpdate(final Time time) {
        // final double currentTime = LibAPIManager.active().getTime(); //TODO Clean this
        //currentTime.set(LibAPIManager.active().getTime()); //TODO Clean this
        //final double currentTime_ = currentTime.get(); //TODO Clean this
        //final KeySettings keySettings = null; //FIXME KeySettings missing!
        if (this.longButtonPressEnabled) {
            this.keyboardHandler.preUpdate(time.current, keySettings);
            this.mouseHandler.preUpdate(time.current, keySettings);
        }
        JoystickHandler.preUpdateAll(time.current, keySettings);
    }
    
    @Override
    public void update(final Time time) {
        // final double currentTime = LibAPIManager.active().getTime(); //TODO Clean this
        //final double currentTime_ = currentTime.get(); //TODO Clean this
        this.keyboardHandler.clearInputString();
        //final KeySettings keySettings = null; //FIXME KeySettings missing!
        if (this.longButtonPressEnabled) {
            this.keyboardHandler.update(time.current, keySettings);
            this.mouseHandler.update(time.current, keySettings);
        }
        JoystickHandler.updateAll(time.current, keySettings);
    }
    
    @Override
    public void postUpdate(final Time time) {
        // final double currentTime = LibAPIManager.active().getTime(); //TODO Clean this
        //final double currentTime_ = currentTime.get(); //TODO Clean this
        //final KeySettings keySettings = null; //FIXME KeySettings missing!
        if (this.longButtonPressEnabled) {
            this.keyboardHandler.postUpdate(time.current, keySettings);
            this.mouseHandler.postUpdate(time.current, keySettings);
        }
        JoystickHandler.postUpdateAll(time.current, keySettings);
    }
    
    public InputManager close() {
        this.keyboardHandler.close();
        this.mouseHandler.close();
        JoystickHandler.closeAll();
        return this;
    }
    
    public boolean isLongButtonPressEnabled() {
        return this.longButtonPressEnabled;
    }
    
    public InputManager setLongButtonPressEnabled(final boolean longButtonPressEnabled) {
        this.longButtonPressEnabled = longButtonPressEnabled;
        return this;
    }
    
    public long getWindow() {
        return this.window;
    }
    
    // Keyboard part
    
    public byte getKeyboardKeyState(final int keyCode) {
        if (keyCode < 0 || keyCode >= this.keyboardHandler.size()) {
            return KeySettings.KEY_UNKNOWN;
        }
        return this.keyboardHandler.getKeyState(keyCode);
    }
    
    public boolean isKeyboardKeyPressed(final int keyCode) {
        if (keyCode < 0 || keyCode >= this.keyboardHandler.size()) {
            return false;
        }
        return this.keyboardHandler.isKeyPressed(keyCode) || this.keyboardHandler.isKeyRepeated(keyCode);
    }
    
    // Mouse part
    
    public byte getMouseButtonState(final int buttonCode) {
        if (buttonCode < 0 || buttonCode >= this.mouseHandler.size()) {
            return KeySettings.KEY_UNKNOWN;
        }
        return this.mouseHandler.getButtonState(buttonCode);
    }
    
    public boolean isMouseButtonPressed(final int buttonCode) {
        if (buttonCode < 0 || buttonCode >= this.mouseHandler.size()) {
            return false;
        }
        return this.mouseHandler.isButtonPressed(buttonCode); // || mouseHandler.isButtonRepeated(buttonCode); //TODO Can a mouse buttons state be "REPEATED"?
    }
    
    public Vector2dc getMousePosition() {
        return this.mouseHandler.getPosition();
    }
    
    public Vector2dc getMouseScrollOffset() {
        return this.mouseHandler.getScrollOffset();
    }
    
    public boolean isMouseInsideWindow() {
        return this.mouseHandler.isInsideWindow();
    }
    
    public CursorType getCursorType() {
        return this.cursorType;
    }
    
    public InputManager setCursorType(final CursorType cursorType) {
        Util.ensureNonNull(cursorType);
        GLFW.glfwSetInputMode(this.window, GLFW.GLFW_CURSOR, cursorType.getState());
        this.cursorType = cursorType;
        return this;
    }
    
    // Mouse delta part
    
    private void updateMouseDeltas(final Vector2d mousePosition, final Vector2d mouseScrollOffset) {
        this.mousePositionDelta.x = (mousePosition.x - this.mousePositionLastTime.x);
        this.mousePositionDelta.y = (mousePosition.y - this.mousePositionLastTime.y);
        this.mouseScrollOffsetDelta.x = (mouseScrollOffset.x - this.mouseScrollOffsetLastTime.x);
        this.mouseScrollOffsetDelta.y = (mouseScrollOffset.y - this.mouseScrollOffsetLastTime.y);
        this.mouseDelta.x = this.mousePositionDelta.x;
        this.mouseDelta.y = this.mousePositionDelta.y;
        this.mouseDelta.z = this.mouseScrollOffsetDelta.x;
        this.mouseDelta.w = this.mouseScrollOffsetDelta.y;
        //TODO Maybe split this up, because this below was executed as the last part in the old update ("nextFrame") method
        this.mousePositionLastTime.x = mousePosition.x;
        this.mousePositionLastTime.y = mousePosition.y;
        this.mouseScrollOffsetLastTime.x = mouseScrollOffset.x;
        this.mouseScrollOffsetLastTime.y = mouseScrollOffset.y;
    }
    
    public Vector2dc getMousePositionDelta() {
        return this.mousePositionDelta;
    }
    
    public Vector2dc getMouseScrollOffsetDelta() {
        return this.mouseScrollOffsetDelta;
    }
    
    public Vector4dc getMouseDelta() {
        return this.mouseDelta;
    }
    
}
