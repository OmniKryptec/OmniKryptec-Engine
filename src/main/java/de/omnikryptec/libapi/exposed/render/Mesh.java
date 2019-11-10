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
            ibo.storeData(meshData.getAttribute(VertexAttribute.Index), BufferUsage.Static);
            this.vertexArray.setIndexBuffer(ibo);
        }
        //TODO make attribute positions configurable
        for (final VertexAttribute va : VertexAttribute.values()) {
            if (va != VertexAttribute.Index && meshData.hasVertexAttribute(va)) {
                final VertexBuffer vbo = LibAPIManager.instance().getGLFW().getRenderAPI().createVertexBuffer();
                vbo.storeData((float[]) meshData.getAttribute(va), BufferUsage.Static);
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
