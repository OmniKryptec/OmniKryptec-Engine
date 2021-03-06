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

package de.omnikryptec.libapi.opengl.buffer;

import org.lwjgl.opengl.GL15;

import de.omnikryptec.libapi.exposed.Deletable;
import de.omnikryptec.libapi.opengl.OpenGLUtil;

public abstract class GLBuffer implements Deletable {
    
    private final int pointer;
    private final int type;
    
    public GLBuffer(final int type) {
        this.type = type;
        this.pointer = GL15.glGenBuffers();
        registerThisAsAutodeletable();
    }
    
    @Override
    public void deleteRaw() {
        GL15.glDeleteBuffers(this.pointer);
    }
    
    public int bufferId() {
        return this.pointer;
    }
    
    public int bufferType() {
        return this.type;
    }
    
    public void bindBuffer() {
        OpenGLUtil.bindBuffer(this.type, this.pointer);
    }
    
    public void unbindBuffer() {
        OpenGLUtil.bindBuffer(this.type, this.pointer);
    }
    
}
