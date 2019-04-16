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

package de.omnikryptec.libapi.opengl.texture;

import org.lwjgl.opengl.GL11;

import de.omnikryptec.libapi.exposed.Deletable;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.opengl.OpenGLUtil;

public abstract class GLTexture implements Texture, Deletable {

    public final int pointer;
    public final int type;

    public GLTexture(final int type) {
        this.pointer = GL11.glGenTextures();
        this.type = type;
        registerThisAsAutodeletable();
    }

    public int textureId() {
        return this.pointer;
    }

    public int textureType() {
        return this.type;
    }

    @Override
    public void bindTexture(final int unit) {
        OpenGLUtil.bindTexture(unit, this.type, this.pointer, false);
    }

    @Override
    public void deleteRaw() {
        GL11.glDeleteTextures(this.pointer);
    }

}
