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

package de.omnikryptec.libapi.exposed.render;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.RenderAPI.BufferUsage;
import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout.VertexBufferElement;
import de.omnikryptec.resource.MeshData;
import de.omnikryptec.resource.MeshData.Primitive;
import de.omnikryptec.resource.MeshData.VertexAttribute;

public class Mesh {

    private final VertexArray vertexArray;
    private final Primitive primitive;
    private final int elementCount;

    public Mesh(final MeshData meshData) {
        this.vertexArray = LibAPIManager.instance().getGLFW().getRenderAPI().createVertexArray();
        this.primitive = meshData.getPrimitiveType();
        this.elementCount = meshData.getElementCount();
        if (meshData.hasVertexAttribute(VertexAttribute.Index)) {
            final IndexBuffer ibo = LibAPIManager.instance().getGLFW().getRenderAPI().createIndexBuffer();
            int[] indices = meshData.getAttribute(VertexAttribute.Index);
            ibo.setDescription(BufferUsage.Static, indices.length);
            ibo.updateData(indices);
            this.vertexArray.setIndexBuffer(ibo);
        }
        //TODO pcfreak9000 make attribute positions configurable
        for (final VertexAttribute va : VertexAttribute.values()) {
            if (va != VertexAttribute.Index && meshData.hasVertexAttribute(va)) {
                final VertexBuffer vbo = LibAPIManager.instance().getGLFW().getRenderAPI().createVertexBuffer();
                float[] array = (float[]) meshData.getAttribute(va);
                vbo.setDescription(BufferUsage.Static, Type.FLOAT, array.length);
                vbo.updateData(array);
                this.vertexArray.addVertexBuffer(vbo,
                        new VertexBufferElement(Type.FLOAT, meshData.getAttributeSize(va), false));
            }
        }
    }

    public VertexArray getVertexArray() {
        return this.vertexArray;
    }

    public Primitive getPrimitive() {
        return this.primitive;
    }

    public int getElementCount() {
        return this.elementCount;
    }

}
