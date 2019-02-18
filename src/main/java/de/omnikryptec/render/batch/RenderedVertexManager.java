package de.omnikryptec.render.batch;

import java.util.Objects;

import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.RenderAPI.BufferUsage;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.VertexArray;
import de.omnikryptec.libapi.exposed.render.VertexBuffer;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout;
import de.omnikryptec.resource.MeshData.Primitive;

public class RenderedVertexManager implements VertexManager {

    private final FloatCollector data;
    private final int floatsPerVertex;
    private Texture currentTexture;
    private final VertexArray va;
    private final VertexBuffer vb;

    public RenderedVertexManager(final int vertexCount, final VertexBufferLayout layout) {
        this.floatsPerVertex = layout.getCount();
        this.data = new FloatCollector(vertexCount * this.floatsPerVertex);
        this.vb = RenderAPI.get().createVertexBuffer();
        this.va = RenderAPI.get().createVertexArray();
        this.va.addVertexBuffer(this.vb, layout);
    }

    @Override
    public void addVertex(final float[] floats, final int offset, final int length) {
        this.data.put(floats, offset, length);
    }

    @Override
    public void prepareNext(final Texture baseTexture, final int requiredFloats) {
        if (requiredFloats > this.data.size()) {
            throw new IndexOutOfBoundsException("Can't handle mesh, buffer too small");
        }
        if (requiredFloats > this.data.remaining() || !Objects.equals(baseTexture, this.currentTexture)) {
            this.currentTexture = baseTexture;
            forceFlush();
        }
    }

    @Override
    public void forceFlush() {
        if (this.currentTexture != null) {
            this.currentTexture.bindTexture(0);
        }
        final int count = this.data.used();
        this.vb.storeData(this.data.flush(), BufferUsage.Stream, count);
        RenderAPI.get().render(this.va, Primitive.Triangle, count / this.floatsPerVertex);
    }

    @Override
    public int floatsPerVertex() {
        return floatsPerVertex;
    }

}
