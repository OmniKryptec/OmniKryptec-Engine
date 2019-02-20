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

import de.omnikryptec.util.settings.KeySettings;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

//FIXME synchronized causes bad performance // Who says that?
public class KeyboardHandler implements InputHandler {
    
    // private final InputState[] keys = new InputState[65536]; //TODO Clean this
    // private final byte[] keys = new byte[65536]; //TODO Clean this
    private final byte[] keys = new byte[GLFW.GLFW_KEY_LAST + 1]; //TODO Test if this includes every key
    private final KeyboardHandler ME = this;
    private final long window;
    private final GLFWKeyCallback keyCallback;
    private final AtomicReference<String> inputString = new AtomicReference<>("");
    // Configurable variables
    private final boolean appendToString = false;
    // Temporary variables
    private byte[] keysLastTime = null;
    
    public KeyboardHandler(final long window) {
        this.window = window;
        this.keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(final long window, final int key, final int scancode, final int action, final int mods) {
                if (KeyboardHandler.this.ME.window != window) {
                    return;
                }
                synchronized (KeyboardHandler.this.keys) {
                    // final InputState inputState = InputState.ofState(action); //TODO Clean this
                    // keys[key] = inputState; //TODO Clean this
                    final byte actionByte = (byte) action;
                    KeyboardHandler.this.keys[key] = actionByte;
                    if (KeyboardHandler.this.appendToString && (actionByte == KeySettings.KEY_PRESSED || actionByte == KeySettings.KEY_REPEATED)) {
                        final String keyString = GLFW.glfwGetKeyName(key, scancode); // FIXME Is this deprecated?
                        if (keyString != null) {
                            KeyboardHandler.this.inputString.updateAndGet((inputString_) -> inputString_ + keyString);
                        }
                    }
                }
            }
        };
    }
    
    @Override
    public synchronized InputHandler init() {
        GLFW.glfwSetKeyCallback(this.window, this.keyCallback);
        return this;
    }
    
    @Override
    public synchronized InputHandler preUpdate(final double currentTime, final KeySettings keySettings) {
        this.keysLastTime = Arrays.copyOf(this.keys, this.keys.length);
        return this;
    }
    
    @Override
    public synchronized InputHandler update(final double currentTime, final KeySettings keySettings) {
        for (int i = 0; i < this.keys.length; i++) {
            if (this.keysLastTime[i] != this.keys[i]) {
                keySettings.updateKeys(currentTime, i, true);
            }
        }
        return this;
    }
    
    @Override
    public synchronized InputHandler postUpdate(final double currentTime, final KeySettings keySettings) {
        //this.keysLastTime = null; // Is this good for performance or not? // makes no sense
        return this;
    }
    
    @Override
    public synchronized InputHandler close() {
        if (this.keyCallback != null) {
            this.keyCallback.close();
        }
        return this;
    }
    
    /* //TODO Clean this
     * public synchronized InputState getKeyState(int keyCode) { return
     * keys[keyCode]; }
     */
    
    public synchronized byte getKeyState(final int keyCode) {
        return this.keys[keyCode];
    }
    
    public synchronized boolean isKeyUnknown(final int keyCode) {
        return this.keys[keyCode] == KeySettings.KEY_UNKNOWN;
    }
    
    public synchronized boolean isKeyNothing(final int keyCode) {
        return this.keys[keyCode] == KeySettings.KEY_NOTHING;
    }
    
    public synchronized boolean isKeyReleased(final int keyCode) {
        return this.keys[keyCode] == KeySettings.KEY_RELEASED;
    }
    
    public synchronized boolean isKeyPressed(final int keyCode) {
        return this.keys[keyCode] == KeySettings.KEY_PRESSED;
    }
    
    public synchronized boolean isKeyRepeated(final int keyCode) {
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
    
    public long getWindow() {
        return this.window;
    }
    
}
