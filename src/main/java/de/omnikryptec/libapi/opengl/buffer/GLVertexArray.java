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

package de.omnikryptec.libapi.opengl.buffer;

import java.util.List;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import de.omnikryptec.libapi.exposed.AutoDelete;
import de.omnikryptec.libapi.exposed.render.IndexBuffer;
import de.omnikryptec.libapi.exposed.render.VertexArray;
import de.omnikryptec.libapi.exposed.render.VertexBuffer;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout.VertexBufferElement;
import de.omnikryptec.libapi.opengl.OpenGLUtil;

public class GLVertexArray extends AutoDelete implements VertexArray {
    
    private final int pointer;
    private int vaaIndex = 0;
    
    private int vertexCount;
    
    private boolean indexBuffer;
    
    public GLVertexArray() {
        this.pointer = GL30.glGenVertexArrays();
    }
    
    @Override
    public void bindArray() {
        OpenGLUtil.bindVertexArray(this.pointer, false);
    }
    
    @Override
    public void unbindArray() {
        OpenGLUtil.bindVertexArray(0, true);
    }
    
    @Override
    protected void deleteRaw() {
        GL30.glDeleteVertexArrays(this.pointer);
    }
    
    public int arrayId() {
        return this.pointer;
    }
    
    @Override
    public void addVertexBuffer(final VertexBuffer buffer, final VertexBufferLayout layout) {
        final List<VertexBufferElement> elements = layout.getElements();
        int stride = 0;
        int offset = 0;
        int perVertexCount = 0;
        for (final VertexBufferElement element : elements) {
            stride += element.getCount() * OpenGLUtil.sizeof(element.getType());
            perVertexCount += element.getCount();
        }
        if (!hasIndexBuffer()) {
            this.vertexCount = buffer.size() / perVertexCount;
        }
        bindArray();
        buffer.bindBuffer();
        for (int i = 0; i < elements.size(); i++) {
            final VertexBufferElement element = elements.get(i);
            GL20.glEnableVertexAttribArray(i);
            GL20.glVertexAttribPointer(i, element.getCount(), OpenGLUtil.typeId(element.getType()), element.normalize(),
                    stride, offset);
            offset += element.getCount() * OpenGLUtil.sizeof(element.getType());
            this.vaaIndex++;
        }
        unbindArray();
    }
    
    @Override
    public void addVertexBuffer(final VertexBuffer buffer, final VertexBufferElement element) {
        if (!hasIndexBuffer()) {
            this.vertexCount = buffer.size() / element.getCount();
        }
        bindArray();
        buffer.bindBuffer();
        //pass the attribarrayindex as parameter in the future?
        GL20.glEnableVertexAttribArray(this.vaaIndex);
        GL20.glVertexAttribPointer(this.vaaIndex, element.getCount(), OpenGLUtil.typeId(element.getType()),
                element.normalize(), 0, 0);
        this.vaaIndex++;
        unbindArray();
    }
    
    @Override
    public void setIndexBuffer(final IndexBuffer buffer) {
        this.vertexCount = buffer.size();
        this.indexBuffer = true;
        bindArray();
        buffer.bindBuffer();
        unbindArray();
    }
    
    @Override
    public int vertexCount() {
        return this.vertexCount;
    }
    
    @Override
    public boolean hasIndexBuffer() {
        return this.indexBuffer;
    }
    
}
