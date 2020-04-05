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

package de.omnikryptec.render.batch.vertexmanager;

import java.nio.FloatBuffer;

import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;
import org.lwjgl.BufferUtils;

//Is this efficient?
public class FloatCollector {

    private final float[] floats;
    private final FloatBuffer fbuffer;

    private int position;

    public FloatCollector(final int size) {
        this.floats = new float[size];
        this.fbuffer = BufferUtils.createFloatBuffer(size);
    }

    public void put(final Vector2fc vec) {
        put(vec.x());
        put(vec.y());
    }

    public void put(final Vector3fc vec) {
        put(vec.x());
        put(vec.y());
        put(vec.z());
    }

    public void put(final Vector4fc vec) {
        put(vec.x());
        put(vec.y());
        put(vec.z());
        put(vec.w());
    }

    public void put(final float f) {
        this.floats[this.position++] = f;
    }

    public void put(final float[] newfloats) {
        put(newfloats, 0, newfloats.length);
    }

    public void put(final float[] newfloats, final int srcPos, final int length) {
        System.arraycopy(newfloats, srcPos, this.floats, this.position, length);
        this.position += length;
    }

    public FloatBuffer flush() {
        this.fbuffer.clear();
        this.fbuffer.put(this.floats, 0, used());
        clearArray();
        return this.fbuffer;
    }

    public void clearArray() {
        this.position = 0;
    }

    public float[] rawArray() {
        return this.floats;
    }

    public int size() {
        return this.floats.length;
    }

    public int remaining() {
        return this.floats.length - used();
    }

    public int used() {
        return this.position;
    }

}
