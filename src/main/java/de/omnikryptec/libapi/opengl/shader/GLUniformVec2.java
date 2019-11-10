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

package de.omnikryptec.libapi.opengl.shader;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL20;

public class GLUniformVec2 extends GLUniform {

    private float currentX;
    private float currentY;
    private boolean used = false;

    public GLUniformVec2(final String name) {
        super(name);
    }

    public void loadVec2(final Vector2f vector) {
        loadVec2(vector.x, vector.y);
    }

    public void loadVec2(final float[] array) {
        loadVec2(array[0], array[1]);
    }

    public void loadVec2(final float x, final float y) {
        if (existsInCompilation() && (!this.used || x != this.currentX || y != this.currentY)) {
            this.currentX = x;
            this.currentY = y;
            this.used = true;
            GL20.glUniform2f(super.getLocation(), x, y);
        }
    }

}
