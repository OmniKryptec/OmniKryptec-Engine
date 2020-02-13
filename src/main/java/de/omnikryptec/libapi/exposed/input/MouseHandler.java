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

import java.util.concurrent.atomic.AtomicBoolean;

import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.util.math.MathUtil;
import de.omnikryptec.util.settings.KeySettings;
import de.omnikryptec.util.settings.keys.KeysAndButtons;

public class MouseHandler implements InputHandler {
    
    private final byte[] buttons = new byte[KeysAndButtons.MOUSE_BUTTON_AMOUNT];
    private final Vector2d position = new Vector2d(0.0, 0.0);
    private Vector2f positionRelative;
    private final Vector2d scrollOffset = new Vector2d(0.0, 0.0);
    private final AtomicBoolean insideWindow = new AtomicBoolean(false);
    // Temporary variables
    private final byte[] buttonsLastTime = new byte[this.buttons.length];
    
    public MouseHandler() {
        LibAPIManager.ENGINE_EVENTBUS.register(this);
    }
    
    @Override
    public boolean init() {
        return true;
    }
    
    @Override
    public boolean deinit() {
        return true;
    }
    
    @EventSubscription
    public void onButtonInput(final InputEvent.MouseButtonEvent ev) {
        this.buttons[ev.button] = (byte) ev.action;
    }
    
    @EventSubscription
    public void onPosChangeEvent(final InputEvent.MousePositionEvent ev) {
        this.position.x = ev.xPos;
        this.position.y = ev.yPos;
        this.positionRelative = MathUtil.relativeMousePosition(this.position,
                LibAPIManager.instance().getGLFW().getRenderAPI().getSurface().getViewportUnsafe(),
                this.positionRelative);
    }
    
    @EventSubscription
    public void onScrollEvent(final InputEvent.MouseScrollEvent ev) {
        this.scrollOffset.x = ev.xChange;
        this.scrollOffset.y = ev.yChange;
    }
    
    @EventSubscription
    public void onCursorEnterEvent(final InputEvent.CursorInWindowEvent ev) {
        this.insideWindow.set(ev.entered);
    }
    
    @Override
    public boolean preUpdate(final double currentTime, final KeySettings keySettings) {
        /*
         * synchronized (buttons) { buttonsLastTime = Arrays.copyOf(buttons,
         * buttons.length); }
         */
        return true;
    }
    
    @Override
    public boolean update(final double currentTime, final KeySettings keySettings) {
        synchronized (this.buttons) {
            for (int i = 0; i < this.buttons.length; i++) {
                if (this.buttonsLastTime[i] != this.buttons[i]) {
                    keySettings.updateKeys(currentTime, i, false, this.buttons[i]);
                    this.buttonsLastTime[i] = this.buttons[i];
                }
            }
        }
        return true;
    }
    
    @Override
    public boolean postUpdate(final double currentTime, final KeySettings keySettings) {
        //buttonsLastTime = null; // Is this good for performance or not? // Makes no sense
        return true;
    }
    
    @Override
    public boolean close() {
        return true;
    }
    
    public byte getButtonState(final int buttonCode) {
        synchronized (this.buttons) {
            return this.buttons[buttonCode];
        }
    }
    
    public boolean isButtonUnknown(final int buttonCode) {
        synchronized (this.buttons) {
            return this.buttons[buttonCode] == KeySettings.KEY_UNKNOWN;
        }
    }
    
    public boolean isButtonNothing(final int buttonCode) {
        synchronized (this.buttons) {
            return this.buttons[buttonCode] == KeySettings.KEY_NOTHING;
        }
    }
    
    public boolean isButtonReleased(final int buttonCode) {
        synchronized (this.buttons) {
            return this.buttons[buttonCode] == KeySettings.KEY_RELEASED;
        }
    }
    
    public boolean isButtonPressed(final int buttonCode) {
        synchronized (this.buttons) {
            return this.buttons[buttonCode] == KeySettings.KEY_PRESSED;
        }
    }
    
    public boolean isButtonRepeated(final int buttonCode) {
        synchronized (this.buttons) {
            return this.buttons[buttonCode] == KeySettings.KEY_REPEATED;
        }
    }
    
    public Vector2dc getPosition() {
        return this.position;
    }
    
    public Vector2dc getScrollOffset() {
        return this.scrollOffset;
    }
    
    public Vector2fc getPositionRelative() {
        return this.positionRelative;
    }
    
    public boolean isInsideWindow() {
        return this.insideWindow.get();
    }
    
    public int size() {
        return this.buttons.length;
    }
    
}
