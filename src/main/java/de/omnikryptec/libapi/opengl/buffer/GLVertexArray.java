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

import java.util.List;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

import de.omnikryptec.libapi.exposed.Deletable;
import de.omnikryptec.libapi.exposed.render.IndexBuffer;
import de.omnikryptec.libapi.exposed.render.VertexArray;
import de.omnikryptec.libapi.exposed.render.VertexBuffer;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout.VertexBufferElement;
import de.omnikryptec.libapi.opengl.OpenGLUtil;

public class GLVertexArray implements VertexArray, Deletable {
    
    private final int pointer;
    private int vaaIndex = 0;
    
    private boolean indexBuffer;
    private int attributeOffset = 0;//Ist dies klug?
    
    public GLVertexArray() {
        this.pointer = GL30.glGenVertexArrays();
        registerThisAsAutodeletable();
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
    public void deleteRaw() {
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
        for (final VertexBufferElement element : elements) {
            stride += element.getValuesPerVertex() * OpenGLUtil.sizeof(element.getType());
        }
        bindArray();
        buffer.bindBuffer();
        for (int i = 0; i < elements.size(); i++) {
            final VertexBufferElement element = elements.get(i);
            GL20.glEnableVertexAttribArray(i + attributeOffset);
            GL20.glVertexAttribPointer(i + attributeOffset, element.getValuesPerVertex(), OpenGLUtil.typeId(element.getType()),
                    element.normalize(), stride, offset);
            GL33.glVertexAttribDivisor(i + attributeOffset, element.getDivisor());//Probably should only allow one divisor per VBO? performance and so... but whatever
            offset += element.getValuesPerVertex() * OpenGLUtil.sizeof(element.getType());
            this.vaaIndex++;
        }
        attributeOffset += elements.size();
        unbindArray();
    }
    
    @Override
    public void addVertexBuffer(final VertexBuffer buffer, final VertexBufferElement element) {
        bindArray();
        buffer.bindBuffer();
        //pass the attribarrayindex as parameter in the future?
        GL20.glEnableVertexAttribArray(this.vaaIndex);
        GL20.glVertexAttribPointer(this.vaaIndex, element.getValuesPerVertex(), OpenGLUtil.typeId(element.getType()),
                element.normalize(), 0, 0);
        this.vaaIndex++;
        unbindArray();
    }
    
    @Override
    public void setIndexBuffer(final IndexBuffer buffer) {
        this.indexBuffer = true;
        bindArray();
        buffer.bindBuffer();
        unbindArray();
    }
    
    @Override
    public boolean hasIndexBuffer() {
        return this.indexBuffer;
    }
    
}
