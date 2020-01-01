/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

import de.omnikryptec.core.update.IUpdatable;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.render.Camera;
import de.omnikryptec.util.math.MathUtil;
import de.omnikryptec.util.settings.KeySettings;
import de.omnikryptec.util.updater.Time;
import org.joml.Matrix4fc;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector2f;

public class InputManager implements IUpdatable {

    private final KeySettings keySettings;
    // Keyboard part
    private final KeyboardHandler keyboardHandler;
    // Mouse part
    private final MouseHandler mouseHandler;
    // Mouse delta part
    private final Vector2d mousePositionLastTime = new Vector2d(0.0, 0.0);
    private final Vector2d mouseScrollOffsetLastTime = new Vector2d(0.0, 0.0);
    private final Vector2d mousePositionDelta = new Vector2d(0.0, 0.0);
    private final Vector2d mouseScrollOffsetDelta = new Vector2d(0.0, 0.0);

    private boolean longButtonPressEnabled = false;

    public InputManager(final KeySettings keySettings) {
        this(keySettings, new KeyboardHandler(), new MouseHandler());
    }

    protected InputManager(final KeySettings keySettings, final KeyboardHandler keyboardHandler,
            final MouseHandler mouseHandler) {
        this.keySettings = keySettings;
        this.keyboardHandler = keyboardHandler;
        this.mouseHandler = mouseHandler;
    }

    public KeySettings getKeySettings() {
        return this.keySettings;
    }

    public KeyboardHandler getKeyboardHandler() {
        return this.keyboardHandler;
    }

    public MouseHandler getMouseHandler() {
        return this.mouseHandler;
    }

    protected boolean preUpdateIntern(final Time time) {
        boolean good = true;
        if (this.longButtonPressEnabled) {
            if (!this.keyboardHandler.preUpdate(time.current, this.keySettings)) {
                good = false;
            }
            if (!this.mouseHandler.preUpdate(time.current, this.keySettings)) {
                good = false;
            }
        }
        if (!JoystickHandler.preUpdateAll(time.current, this.keySettings)) {
            good = false;
        }
        return good;
    }

    protected boolean updateIntern(final Time time) {
        this.keyboardHandler.clearInputString();
        boolean good = true;
        if (this.longButtonPressEnabled) {
            if (!this.keyboardHandler.update(time.current, this.keySettings)) {
                good = false;
            }
            if (!this.mouseHandler.update(time.current, this.keySettings)) {
                good = false;
            }
        }
        if (!JoystickHandler.updateAll(time.current, this.keySettings)) {
            good = false;
        }
        return good;
    }

    protected boolean postUpdateIntern(final Time time) {
        boolean good = true;
        if (this.longButtonPressEnabled) {
            if (!this.keyboardHandler.postUpdate(time.current, this.keySettings)) {
                good = false;
            }
            if (!this.mouseHandler.postUpdate(time.current, this.keySettings)) {
                good = false;
            }
        }
        if (!JoystickHandler.postUpdateAll(time.current, this.keySettings)) {
            good = false;
        }
        return good;
    }

    public boolean close() {
        boolean good = true;
        if (!this.keyboardHandler.close()) {
            good = false;
        }
        if (!this.mouseHandler.close()) {
            good = false;
        }
        if (!JoystickHandler.closeAll()) {
            good = false;
        }
        return good;
    }

    @Override
    public void update(final Time time) {
        postUpdateIntern(time);

        updateIntern(time);
        updateMouseDeltas(getMousePosition(), getMouseScrollOffset());

        preUpdateIntern(time);

    }

    public boolean isLongButtonPressEnabled() {
        return this.longButtonPressEnabled;
    }

    public InputManager setLongButtonPressEnabled(final boolean longButtonPressEnabled) {
        this.longButtonPressEnabled = longButtonPressEnabled;
        return this;
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
        return this.mouseHandler.isButtonPressed(buttonCode); // || mouseHandler.isButtonRepeated(buttonCode); //TODO Panzer1119 Can a mouse buttons state be "REPEATED"?
    }

    public Vector2f getMousePositionRelative(final Vector2f target) {
        return MathUtil.relativeMousePosition(getMousePosition(),
                LibAPIManager.instance().getGLFW().getRenderAPI().getSurface().getViewportUnsafe(), target);
    }

    public Vector2f getMousePositionInWorld2D(final Camera camera, final Vector2f target) {
        return getMousePositionInWorld2D(camera.getProjectionInverse(), target);
    }

    public Vector2f getMousePositionInWorld2D(final Matrix4fc inverseViewProjection, Vector2f target) {
        target = getMousePositionRelative(target);
        MathUtil.screenToWorldspace2D(target, inverseViewProjection, target);
        return target;
    }

    public Vector2dc getMousePosition() {
        return this.mouseHandler.getPosition();
    }

    public Vector2dc getMouseScrollOffset() {
        return this.mouseHandler.getScrollOffset();
    }

    public boolean isMouseInsideViewport() {
        return isMouseInsideWindow() && LibAPIManager.instance().getGLFW().getRenderAPI().getSurface()
                .isInViewport(this.mouseHandler.getPosition());
    }

    public boolean isMouseInsideWindow() {
        return this.mouseHandler.isInsideWindow();
    }

    // Mouse delta part

    private void updateMouseDeltas(final Vector2dc mousePosition, final Vector2dc mouseScrollOffset) {
        this.mousePositionDelta.x = (mousePosition.x() - this.mousePositionLastTime.x);
        this.mousePositionDelta.y = (mousePosition.y() - this.mousePositionLastTime.y);
        this.mouseScrollOffsetDelta.x = (mouseScrollOffset.x() - this.mouseScrollOffsetLastTime.x);
        this.mouseScrollOffsetDelta.y = (mouseScrollOffset.y() - this.mouseScrollOffsetLastTime.y);
        //TODO Panzer1119 Maybe split this up, because this below was executed as the last part in the old update ("nextFrame") method, needs discussion?
        this.mousePositionLastTime.x = mousePosition.x();
        this.mousePositionLastTime.y = mousePosition.y();
        this.mouseScrollOffsetLastTime.x = mouseScrollOffset.x();
        this.mouseScrollOffsetLastTime.y = mouseScrollOffset.y();
    }

    public Vector2dc getMousePositionDelta() {
        return this.mousePositionDelta;
    }

    public Vector2dc getMouseScrollOffsetDelta() {
        return this.mouseScrollOffsetDelta;
    }

}
