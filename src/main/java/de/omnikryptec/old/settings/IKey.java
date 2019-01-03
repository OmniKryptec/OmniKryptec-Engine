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

package de.omnikryptec.old.settings;

/**
 * Key Interface
 *
 * @author Panzer1119
 */
public interface IKey extends XMLable {

    /**
     * Returns the name
     *
     * @return String Name
     */
    public String getName();

    /**
     * Returns if this IKey is pressed
     *
     * @return <tt>true</tt> if it is pressed
     */
    public boolean isPressed();

    /**
     * Returns of this IKey is pressed for a specified time
     *
     * @param minTime Float Minimum pressing time
     * @param maxTime Float Maximum pressing time
     * @return <tt>true</tt> if it is pressed for the specified time
     */
    public boolean isLongPressed(double minTime, double maxTime);

}
