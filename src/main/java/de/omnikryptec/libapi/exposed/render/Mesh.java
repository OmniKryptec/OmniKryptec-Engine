package de.omnikryptec.libapi.exposed.render;

import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout.VertexBufferElement;
import de.omnikryptec.resource.MeshData;
import de.omnikryptec.resource.MeshData.VertexAttribute;

public class Mesh {

    private final VertexArray vertexArray;
    private final MeshData meshData;

    public Mesh(final MeshData data) {
        this.meshData = data;
        this.vertexArray = RenderAPI.get().createVertexArray();
        if (this.meshData.hasVertexAttribute(VertexAttribute.Index)) {
            final IndexBuffer ibo = RenderAPI.get().createIndexBuffer();
            ibo.storeData(this.meshData.getAttribute(VertexAttribute.Index), false);
            this.vertexArray.setIndexBuffer(ibo);
        }
        //TODO make attribute positions configurable
        for (final VertexAttribute va : VertexAttribute.values()) {
            if (va != VertexAttribute.Index && this.meshData.hasVertexAttribute(va)) {
                final VertexBuffer vbo = RenderAPI.get().createVertexBuffer();
                vbo.storeData((float[]) data.getAttribute(va), false);
                this.vertexArray.addVertexBuffer(vbo,
                        new VertexBufferElement(Type.FLOAT, this.meshData.getAttributeSize(va), false));
            }
        }
    }

    public void bindMesh() {
        this.vertexArray.bindArray();
    }

    public void unbindMesh() {
        this.vertexArray.unbindArray();
    }

    public int vertexCount() {
        return this.vertexArray.vertexCount();
    }

    public boolean hasIndexBuffer() {
        return this.vertexArray.hasIndexBuffer();
    }

    public MeshData getMeshData() {
        return this.meshData;
    }

}
