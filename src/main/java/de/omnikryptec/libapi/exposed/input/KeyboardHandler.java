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
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

//FIXME synchronized causes bad performance // Who says that? // synchronized means the calling thread have to check if there is a lock on that function also the synchronized is unneccessary here
public class KeyboardHandler implements InputHandler {
    
    private final byte[] keys = new byte[GLFW.GLFW_KEY_LAST + 1]; //TODO Test if this includes every key //TODO Maybe this is no longer necessary, because the KeySettings Keys having their own isPressed state, BUT this is necessary, because maybe you want to save ALL key states, so this should stay
    private final AtomicReference<String> inputString = new AtomicReference<>("");
    // Configurable variables
    private final boolean appendToString = false;
    // Temporary variables
    private byte[] keysLastTime = null;
    
    
    @EventSubscription
    public void onKeyEvent(InputEvent.KeyEvent ev) {
        this.keys[ev.key] = (byte) ev.action;
    }
    
    
    @Override
    public synchronized boolean init(EventBus bus) {
        bus.register(this);
        return true;
    }
    
    public boolean deinit(EventBus bus) {
        bus.unregister(this);
        return true;
    }
    
    @Override
    public synchronized boolean preUpdate(double currentTime, KeySettings keySettings) {
        this.keysLastTime = Arrays.copyOf(this.keys, this.keys.length);
        return true;
    }
    
    @Override
    public synchronized boolean update(double currentTime, KeySettings keySettings) {
        for (int i = 0; i < this.keys.length; i++) {
            if (this.keysLastTime[i] != this.keys[i]) {
                keySettings.updateKeys(currentTime, i, true, this.keys[i]);
            }
        }
        return true;
    }
    
    @Override
    public synchronized boolean postUpdate(double currentTime, KeySettings keySettings) {
        //this.keysLastTime = null; // Is this good for performance or not? // makes no sense
        return true;
    }
    
    @Override
    public synchronized boolean close() {
        return true;
    }
    
    public synchronized byte getKeyState(int keyCode) {
        return this.keys[keyCode];
    }
    
    public synchronized boolean isKeyUnknown(int keyCode) {
        return this.keys[keyCode] == KeySettings.KEY_UNKNOWN;
    }
    
    public synchronized boolean isKeyNothing(int keyCode) {
        return this.keys[keyCode] == KeySettings.KEY_NOTHING;
    }
    
    public synchronized boolean isKeyReleased(int keyCode) {
        return this.keys[keyCode] == KeySettings.KEY_RELEASED;
    }
    
    public synchronized boolean isKeyPressed(int keyCode) {
        return this.keys[keyCode] == KeySettings.KEY_PRESSED;
    }
    
    public synchronized boolean isKeyRepeated(int keyCode) {
        return this.keys[keyCode] == KeySettings.KEY_REPEATED;
    }
    
    public synchronized String getInputString() {
        return this.inputString.get();
    }
    
    public synchronized void clearInputString() {
        this.inputString.set("");
    }
    
    public synchronized String consumeInputString() {
        final String temp = getInputString();
        clearInputString();
        return temp;
    }
    
    public int size() {
        return this.keys.length;
    }
    
}
