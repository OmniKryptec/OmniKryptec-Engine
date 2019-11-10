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

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.util.settings.KeySettings;
import de.omnikryptec.util.settings.keys.KeysAndButtons;

//monitor if synchronized causes bad performance in this class or if it is negliable
public class KeyboardHandler implements InputHandler {

    private final byte[] keys = new byte[KeysAndButtons.OKE_KEY_LAST + 1]; //TODO Find out if this includes every key
    private final AtomicReference<String> inputString = new AtomicReference<>("");
    // Configurable variables
    private final boolean appendToString = false;
    // Temporary variables
    private final byte[] keysLastTime = new byte[this.keys.length];

    public KeyboardHandler() {
        LibAPIManager.ENGINE_EVENTBUS.register(this);
    }

    @EventSubscription
    public void onKeyEvent(final InputEvent.KeyEvent ev) {
        this.keys[ev.key] = (byte) ev.action;
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public boolean deinit() {
        return true;
    }

    @Override
    public boolean preUpdate(final double currentTime, final KeySettings keySettings) {
        return true;
    }

    @Override
    public boolean update(final double currentTime, final KeySettings keySettings) {
        synchronized (this.keys) {
            for (int i = 0; i < this.keys.length; i++) {
                if (this.keysLastTime[i] != this.keys[i]) {
                    keySettings.updateKeys(currentTime, i, true, this.keys[i]);
                    this.keysLastTime[i] = this.keys[i];
                }
            }
        }
        return true;
    }

    @Override
    public boolean postUpdate(final double currentTime, final KeySettings keySettings) {
        return true;
    }

    @Override
    public boolean close() {
        return true;
    }

    public byte getKeyState(final int keyCode) {
        synchronized (this.keys) {
            return this.keys[keyCode];
        }
    }

    public boolean isKeyUnknown(final int keyCode) {
        synchronized (this.keys) {
            return this.keys[keyCode] == KeySettings.KEY_UNKNOWN;
        }
    }

    public boolean isKeyNothing(final int keyCode) {
        synchronized (this.keys) {
            return this.keys[keyCode] == KeySettings.KEY_NOTHING;
        }
    }

    public boolean isKeyReleased(final int keyCode) {
        synchronized (this.keys) {
            return this.keys[keyCode] == KeySettings.KEY_RELEASED;
        }
    }

    public boolean isKeyPressed(final int keyCode) {
        synchronized (this.keys) {
            return this.keys[keyCode] == KeySettings.KEY_PRESSED;
        }
    }

    public boolean isKeyRepeated(final int keyCode) {
        synchronized (this.keys) {
            return this.keys[keyCode] == KeySettings.KEY_REPEATED;
        }
    }

    public String getInputString() {
        synchronized (this.inputString) {
            return this.inputString.get();
        }
    }

    public void clearInputString() {
        synchronized (this.inputString) {
            this.inputString.set("");
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
