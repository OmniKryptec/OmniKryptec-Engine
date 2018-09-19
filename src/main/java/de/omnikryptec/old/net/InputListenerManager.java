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

package de.omnikryptec.old.net;

import de.omnikryptec.old.util.logger.Logger;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

/**
 * InputListenerManager
 * @author Panzer1119
 */
public interface InputListenerManager {
    
    final ArrayList<InputListener> inputListeners = new ArrayList<>();
    
    public String getName();
    
    default InputListenerManager addInputListener(InputListener inputListener) {
        if(inputListener == null || inputListeners.contains(inputListener)) {
            return this;
        }
        inputListeners.add(inputListener);
        return this;
    }
    
    default InputListenerManager removeInputListener(InputListener inputListener) {
        if(inputListener == null || !inputListeners.contains(inputListener)) {
            return this;
        }
        inputListeners.remove(inputListener);
        return this;
    }
    
    default InputListenerManager fireInputEvent(InputEvent event, ExecutorService executor) {
        inputListeners.stream().forEach((inputListener) -> {
            Logger.logErr(getName() + ": FIRING EVENT ON: " + inputListener + " / " + inputListeners.size(), new Exception());
            if (executor != null) {
                executor.execute(() -> {
                    inputListener.inputReceived(event.copy());
                });
            } else {
                inputListener.inputReceived(event.copy());
            }
        });
        return this;
    }
    
}
