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

import java.util.concurrent.atomic.AtomicReference;

import org.lwjgl.glfw.GLFW;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.window.InputEvent;
import de.omnikryptec.util.settings.KeySettings;
import de.omnikryptec.util.settings.keys.KeysAndButtons;

//monitor if synchronized causes bad performance in this class or if it is negliable 
public class KeyboardHandler implements InputHandler {
    
    private final byte[] keys = new byte[KeysAndButtons.OKE_KEY_LAST + 1]; //TODO Find out if this includes every key
    private final AtomicReference<String> inputString = new AtomicReference<>("");
    // Configurable variables
    private final boolean appendToString = false;
    // Temporary variables
    private byte[] keysLastTime = new byte[keys.length];
    
    public KeyboardHandler() {
        LibAPIManager.ENGINE_EVENTBUS.register(this);
    }
    
    @EventSubscription
    public void onKeyEvent(InputEvent.KeyEvent ev) {
        this.keys[ev.key] = (byte) ev.action;
    }
    
    @Override
    public boolean init() {
        return true;
    }
    
    public boolean deinit() {
        return true;
    }
    
    @Override
    public boolean preUpdate(double currentTime, KeySettings keySettings) {
        return true;
    }
    
    @Override
    public boolean update(double currentTime, KeySettings keySettings) {
        synchronized (keys) {
            for (int i = 0; i < keys.length; i++) {
                if (keysLastTime[i] != keys[i]) {
                    keySettings.updateKeys(currentTime, i, true, keys[i]);
                    keysLastTime[i] = keys[i];
                }
            }
        }
        return true;
    }
    
    @Override
    public boolean postUpdate(double currentTime, KeySettings keySettings) {
        return true;
    }
    
    @Override
    public boolean close() {
        return true;
    }
    
    public byte getKeyState(int keyCode) {
        synchronized (keys) {
            return keys[keyCode];
        }
    }
    
    public boolean isKeyUnknown(int keyCode) {
        synchronized (keys) {
            return keys[keyCode] == KeySettings.KEY_UNKNOWN;
        }
    }
    
    public boolean isKeyNothing(int keyCode) {
        synchronized (keys) {
            return keys[keyCode] == KeySettings.KEY_NOTHING;
        }
    }
    
    public boolean isKeyReleased(int keyCode) {
        synchronized (keys) {
            return keys[keyCode] == KeySettings.KEY_RELEASED;
        }
    }
    
    public boolean isKeyPressed(int keyCode) {
        synchronized (keys) {
            return keys[keyCode] == KeySettings.KEY_PRESSED;
        }
    }
    
    public boolean isKeyRepeated(int keyCode) {
        synchronized (keys) {
            return keys[keyCode] == KeySettings.KEY_REPEATED;
        }
    }
    
    public String getInputString() {
        synchronized (inputString) {
            return inputString.get();
        }
    }
    
    public void clearInputString() {
        synchronized (inputString) {
            inputString.set("");
        }
    }
    
    public String consumeInputString() {
        final String temp = getInputString();
        clearInputString();
        return temp;
    }
    
    public int size() {
        return this.keys.length;
    }
    
}
