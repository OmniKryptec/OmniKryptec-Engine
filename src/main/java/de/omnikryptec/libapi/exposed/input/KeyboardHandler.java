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

package de.omnikryptec.libapi.exposed.input;

import de.omnikryptec.util.settings.KeySettings;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class KeyboardHandler implements InputHandler {

    // private final InputState[] keys = new InputState[65536];
    // private final byte[] keys = new byte[65536];
    private final byte[] keys = new byte[GLFW.GLFW_KEY_LAST + 1]; // TODO Test if this includes every key
    private final KeyboardHandler ME = this;
    private final long window;
    private final GLFWKeyCallback keyCallback;
    private final AtomicReference<String> inputString = new AtomicReference<>("");
    // Configurable
    private boolean appendToString = false;
    // Temp
    private byte[] keysLastTime = null;

    public KeyboardHandler(long window) {
        this.window = window;
        this.keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (ME.window != window) {
                    return;
                }
                synchronized (keys) {
                    // final InputState inputState = InputState.ofState(action);
                    // keys[key] = inputState;
                    final byte actionB = (byte) action;
                    keys[key] = actionB;
                    if (appendToString && (actionB == KeySettings.KEY_PRESSED || actionB == KeySettings.KEY_REPEATED)) {
                        final String keyString = GLFW.glfwGetKeyName(key, scancode); // FIXME Is this deprecated?
                        if (keyString != null) {
                            inputString.updateAndGet((inputString_) -> inputString_ + keyString);
                        }
                    }
                }
            }
        };
    }

    @Override
    public synchronized InputHandler init() {
        GLFW.glfwSetKeyCallback(window, keyCallback);
        return this;
    }

    @Override
    public synchronized InputHandler preUpdate(double currentTime, KeySettings keySettings) {
        keysLastTime = Arrays.copyOf(keys, keys.length);
        return this;
    }

    @Override
    public synchronized InputHandler update(double currentTime, KeySettings keySettings) {
        for (int i = 0; i < keys.length; i++) {
            if (keysLastTime[i] != keys[i]) {
                keySettings.updateKeys(currentTime, i, true);
            }
        }
        return this;
    }

    @Override
    public synchronized InputHandler postUpdate(double currentTime, KeySettings keySettings) {
        keysLastTime = null; // TODO Is this good for performance or not?
        return this;
    }

    @Override
    public synchronized InputHandler close() {
        if (keyCallback != null) {
            keyCallback.close();
        }
        return this;
    }

    /*
     * public synchronized InputState getKeyState(int keyCode) { return
     * keys[keyCode]; }
     */

    public byte getKeyState(int keyCode) {
        return keys[keyCode];
    }

    public synchronized boolean isKeyUnknown(int keyCode) {
        return keys[keyCode] == KeySettings.KEY_UNKNOWN;
    }

    public synchronized boolean isKeyNothing(int keyCode) {
        return keys[keyCode] == KeySettings.KEY_NOTHING;
    }

    public synchronized boolean isKeyReleased(int keyCode) {
        return keys[keyCode] == KeySettings.KEY_RELEASED;
    }

    public synchronized boolean isKeyPressed(int keyCode) {
        return keys[keyCode] == KeySettings.KEY_PRESSED;
    }

    public synchronized boolean isKeyRepeated(int keyCode) {
        return keys[keyCode] == KeySettings.KEY_REPEATED;
    }

    public synchronized String getInputString() {
        return inputString.get();
    }

    public synchronized void clearInputString() {
        inputString.set("");
    }

    public synchronized String consumeInputString() {
        final String temp = getInputString();
        clearInputString();
        return temp;
    }

    public int size() {
        return keys.length;
    }

    public long getWindow() {
        return window;
    }

}
