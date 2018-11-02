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

package de.omnikryptec.util.settings.keys;

public interface IKey {
    
    /**
     * Returns the name of the {@link de.omnikryptec.util.settings.keys.IKey}
     *
     * @return Name of the {@link de.omnikryptec.util.settings.keys.IKey}
     */
    String getName();
    
    /**
     * Returns if this {@link de.omnikryptec.util.settings.keys.IKey} is being pressed
     *
     * @return <tt>true</tt> if this {@link de.omnikryptec.util.settings.keys.IKey} is pressed
     */
    boolean isPressed();
    
    /**
     * Returns if this {@link de.omnikryptec.util.settings.keys.IKey} is being pressed for a specified time
     *
     * @param minTime Minimum pressing time
     * @param maxTime Maximum pressing time
     *
     * @return <tt>true</tt> if this {@link de.omnikryptec.util.settings.keys.IKey} is pressed for the specified time
     */
    boolean isLongPressed(double minTime, double maxTime);
    
}
