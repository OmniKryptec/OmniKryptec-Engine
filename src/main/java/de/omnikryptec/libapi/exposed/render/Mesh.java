package de.omnikryptec.libapi.exposed.render;

import de.omnikryptec.libapi.exposed.render.RenderAPI.BufferUsage;
import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout.VertexBufferElement;
import de.omnikryptec.resource.MeshData;
import de.omnikryptec.resource.MeshData.Primitive;
import de.omnikryptec.resource.MeshData.VertexAttribute;

public class Mesh implements Renderable {
    
    private final VertexArray vertexArray;
    private final Primitive primitive;
    private final int elementCount;
    
    public Mesh(final MeshData meshData) {
        this.vertexArray = RenderAPI.get().createVertexArray();
        this.primitive = meshData.getPrimitiveType();
        this.elementCount = meshData.getElementCount();
        if (meshData.hasVertexAttribute(VertexAttribute.Index)) {
            final IndexBuffer ibo = RenderAPI.get().createIndexBuffer();
            ibo.storeData(meshData.getAttribute(VertexAttribute.Index), BufferUsage.Static);
            this.vertexArray.setIndexBuffer(ibo);
        }
        //TODO make attribute positions configurable
        for (final VertexAttribute va : VertexAttribute.values()) {
            if (va != VertexAttribute.Index && meshData.hasVertexAttribute(va)) {
                final VertexBuffer vbo = RenderAPI.get().createVertexBuffer();
                vbo.storeData((float[]) meshData.getAttribute(va), BufferUsage.Static);
                this.vertexArray.addVertexBuffer(vbo,
                        new VertexBufferElement(Type.FLOAT, meshData.getAttributeSize(va), false));
            }
        }
    }
    
    @Override
    public void bindRenderable() {
        this.vertexArray.bindArray();
    }
    
    @Override
    public void unbindRenderable() {
        this.vertexArray.unbindArray();
    }
    
    @Override
    public int elementCount() {
        return elementCount;
    }
    
    @Override
    public boolean hasIndexBuffer() {
        return this.vertexArray.hasIndexBuffer();
    }
    
    @Override
    public Primitive primitive() {
        return this.primitive;
    }
    
}
