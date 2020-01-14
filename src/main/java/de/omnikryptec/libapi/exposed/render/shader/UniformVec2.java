/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.libapi.exposed.render.shader;

import org.joml.Vector2fc;

public interface UniformVec2 {
    void loadVec2(float x, float y);
    
    default void loadVec2(final Vector2fc vector) {
        loadVec2(vector.x(), vector.y());
    }
    
    default void loadVec2(final float[] array) {
        loadVec2(array[0], array[1]);
    }
}
