package de.omnikryptec.render.batch;

import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.VertexArray;
import de.omnikryptec.libapi.exposed.render.VertexBuffer;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout;
import de.omnikryptec.libapi.exposed.render.RenderAPI.BufferUsage;
import de.omnikryptec.resource.MeshData.Primitive;

public class RenderedVertexManager implements VertexManager {

    private final FloatCollector data;
    private final int floatsPerVertex;
    private Texture currentTexture;
    private VertexArray va;
    private VertexBuffer vb;

    public RenderedVertexManager(int vertexCount, VertexBufferLayout layout) {
        this.floatsPerVertex = layout.getCount();
        data = new FloatCollector(vertexCount * floatsPerVertex);
        vb = RenderAPI.get().createVertexBuffer();
        va = RenderAPI.get().createVertexArray();
        va.addVertexBuffer(vb, layout);
    }

    @Override
    public void addVertex(float[] floats, int offset, int length) {
        data.put(floats, offset, length);
    }

    @Override
    public void prepareNext(Texture baseTexture, int requiredFloats) {
        if (requiredFloats > data.size()) {
            throw new IndexOutOfBoundsException("Can't handle mesh, buffer too small");
        }
        if (requiredFloats > data.remaining() || !baseTexture.equals(currentTexture)) {
            currentTexture = baseTexture;
            forceFlush();
        }
    }

    @Override
    public void forceFlush() {
        if (currentTexture != null) {
            currentTexture.bindTexture(0);
        }
        int count = data.used();
        vb.storeData(data.flush(), BufferUsage.Stream, count);
        RenderAPI.get().render(va, Primitive.Triangle, count / floatsPerVertex);
    }

}
