package de.omnikryptec.libapi.exposed.render;

import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;
import de.omnikryptec.resource.MeshData;
import de.omnikryptec.resource.MeshData.VertexAttribute;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout.VertexBufferElement;

public class Mesh {
    
    private VertexArray vertexArray;
    private MeshData meshData;
    
    public Mesh(MeshData data) {
        this.meshData = data;
        this.vertexArray = RenderAPI.get().createVertexArray();
        if (meshData.hasVertexAttribute(VertexAttribute.Index)) {
            IndexBuffer ibo = RenderAPI.get().createIndexBuffer();
            ibo.storeData(this.meshData.getAttribute(VertexAttribute.Index), false);
            this.vertexArray.setIndexBuffer(ibo);
        }
        for (VertexAttribute va : VertexAttribute.values()) {
            if (va != VertexAttribute.Index && meshData.hasVertexAttribute(va)) {
                VertexBuffer vbo = RenderAPI.get().createVertexBuffer();
                vbo.storeData((float[]) data.getAttribute(va), false);
                this.vertexArray.addVertexBuffer(vbo,
                        new VertexBufferElement(Type.FLOAT, this.meshData.getAttributeSize(va), false));
            }
        }
    }
    
    public void bindMesh() {
        vertexArray.bindArray();
    }
    
    public void unbindMesh() {
        vertexArray.unbindArray();
    }
    
    public int vertexCount() {
        return vertexArray.vertexCount();
    }
    
    public boolean hasIndexBuffer() {
        return vertexArray.hasIndexBuffer();
    }
    
    public MeshData getMeshData() {
        return meshData;
    }
    
}
