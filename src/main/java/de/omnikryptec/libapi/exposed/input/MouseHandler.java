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

import de.omnikryptec.event.EventBus;
import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.window.InputEvent;
import de.omnikryptec.libapi.exposed.window.SurfaceBuffer;
import de.omnikryptec.util.settings.KeySettings;

public class MouseHandler implements InputHandler {
    //TODO pcfreak - better viewport accesss?
    private final SurfaceBuffer surface = LibAPIManager.instance().getGLFW().getRenderAPI().getSurface();
    private final byte[] buttons = new byte[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private final Vector2d position = new Vector2d(0.0, 0.0);
    private final Vector2d scrollOffset = new Vector2d(0.0, 0.0);
    private final AtomicBoolean insideWindow = new AtomicBoolean(false);
    // Temporary variables
    private byte[] buttonsLastTime = new byte[buttons.length];
    
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
    
    @Override
    public boolean preUpdate(double currentTime, KeySettings keySettings) {
        /*
        synchronized (buttons) {
            buttonsLastTime = Arrays.copyOf(buttons, buttons.length);
        }
        */
        return true;
    }
    
    @Override
    public boolean update(double currentTime, KeySettings keySettings) {
        synchronized (buttons) {
            for (int i = 0; i < buttons.length; i++) {
                if (buttonsLastTime[i] != buttons[i]) {
                    keySettings.updateKeys(currentTime, i, false, buttons[i]);
                    buttonsLastTime[i] = buttons[i];
                }
            }
        }
        return true;
    }
    
    @Override
    public boolean postUpdate(double currentTime, KeySettings keySettings) {
        //buttonsLastTime = null; // Is this good for performance or not? // Makes no sense
        return true;
    }
    
    @Override
    public boolean close() {
        return true;
    }
    
    public byte getButtonState(int buttonCode) {
        synchronized (buttons) {
            return buttons[buttonCode];
        }
    }
    
    public boolean isButtonUnknown(int buttonCode) {
        synchronized (buttons) {
            return buttons[buttonCode] == KeySettings.KEY_UNKNOWN;
        }
    }
    
    public boolean isButtonNothing(int buttonCode) {
        synchronized (buttons) {
            return buttons[buttonCode] == KeySettings.KEY_NOTHING;
        }
    }
    
    public boolean isButtonReleased(int buttonCode) {
        synchronized (buttons) {
            return buttons[buttonCode] == KeySettings.KEY_RELEASED;
        }
    }
    
    public boolean isButtonPressed(int buttonCode) {
        synchronized (buttons) {
            return buttons[buttonCode] == KeySettings.KEY_PRESSED;
        }
    }
    
    public boolean isButtonRepeated(int buttonCode) {
        synchronized (buttons) {
            return buttons[buttonCode] == KeySettings.KEY_REPEATED;
        }
    }
    
    public Vector2dc getPosition() {
        return position;
    }
    
    public Vector2dc getScrollOffset() {
        return scrollOffset;
    }
    
    public boolean isInsideWindow() {
        return insideWindow.get();
    }
    
    public boolean isInsideViewport() {
        return isInsideWindow() && surface.isInViewport(position);
    }
    
    public int size() {
        return buttons.length;
    }
    
}
