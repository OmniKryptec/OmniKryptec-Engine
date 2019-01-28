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

package de.omnikryptec.libapi.exposed.render;

import de.omnikryptec.libapi.exposed.render.VertexBufferLayout.VertexBufferElement;

public interface VertexArray {
    
    /**
     * Binds this {@link VertexArray}. A possible {@link IndexBuffer} gets bound,
     * too.
     *
     * @see #setIndexBuffer(IndexBuffer)
     */
    void bindArray();
    
    /**
     * Unbinds this array and associated parts
     *
     * @see #bindArray()
     */
    void unbindArray();
    
    /**
     * Adds a {@link VertexBuffer} with a certain layout to this
     * {@link VertexArray}. This method should only be used once.
     *
     * @param buffer the vertexbuffer
     * @param layout the layout
     */
    void addVertexBuffer(VertexBuffer buffer, VertexBufferLayout layout);
    
    /**
     * Adds a {@link VertexBuffer} with a certain configuration to this
     * {@link VertexArray}.
     *
     * @param buffer  the buffer
     * @param element the configuration
     */
    void addVertexBuffer(VertexBuffer buffer, VertexBufferElement element);
    
    /**
     * Sets the {@link IndexBuffer} of this {@link VertexArray}.
     *
     * @param buffer the indexbuffer
     */
    void setIndexBuffer(IndexBuffer buffer);
    
    /**
     *
     * @return true if an {@link IndexBuffer} has been set.
     * @see #setIndexBuffer(IndexBuffer)
     */
    boolean hasIndexBuffer();
    
    /**
     * The amount of vertices stored in this {@link VertexArray}.
     *
     * @return the vertex count of this {@link VertexArray}
     */
    //TODO better name/javadoc
    int vertexCount();
}
