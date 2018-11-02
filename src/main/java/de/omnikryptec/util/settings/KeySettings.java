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

package de.omnikryptec.util.settings;

import de.omnikryptec.util.settings.keys.IKey;
import de.omnikryptec.util.settings.keys.KeyContainer;

public class KeySettings extends KeyContainer {
    
    public KeySettings() {
    }
    
    public boolean isPressed(String name) {
        final IKey key = getIKey(name);
        return key == null ? false : key.isPressed();
    }
    
    public boolean isLongPressed(String name, double minTime, double maxTime) {
        final IKey key = getIKey(name);
        return key == null ? false : key.isLongPressed(minTime, maxTime);
    }
    
}
