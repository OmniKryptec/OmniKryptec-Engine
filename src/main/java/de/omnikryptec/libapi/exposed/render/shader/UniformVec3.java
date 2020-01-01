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

import org.joml.Vector3fc;

public interface UniformVec3 extends Uniform {
    void loadVec3(float x, float y, float z);
    
    default void loadVec3(final Vector3fc vector) {
        loadVec3(vector.x(), vector.y(), vector.z());
    }
    
    default void loadVec3(final float[] array) {
        loadVec3(array[0], array[1], array[2]);
    }
}
