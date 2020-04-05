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

import org.joml.Vector4fc;

import de.omnikryptec.util.data.Color;

public interface UniformVec4 extends Uniform {

    void loadVec4(float x, float y, float z, float w);

    default void loadVec4(final Vector4fc vector) {
        loadVec4(vector.x(), vector.y(), vector.z(), vector.w());
    }

    default void loadVec4(final float[] array) {
        loadVec4(array[0], array[1], array[2], array[3]);
    }

    default void loadColor(final Color color) {
        loadVec4(color.getR(), color.getG(), color.getB(), color.getA());
    }
}
