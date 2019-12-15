package de.omnikryptec.render.batch.vertexmanager;

import java.util.Objects;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.RenderAPI.BufferUsage;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.VertexArray;
import de.omnikryptec.libapi.exposed.render.VertexBuffer;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout;
import de.omnikryptec.render.batch.AbstractShaderSlot;
import de.omnikryptec.render.batch.module.ModuleBatchingManager;
import de.omnikryptec.resource.MeshData.Primitive;

public class RenderedVertexManager implements VertexManager {

    private final int vertexCount;

    private FloatCollector data;
    private int floatsPerVertex;
    private Texture currentTexture;
    private VertexArray va;
    private VertexBuffer vb;
    private final AbstractShaderSlot shader;

    public RenderedVertexManager(final int vertexCount, final AbstractShaderSlot shader) {
        this.vertexCount = vertexCount;
        this.shader = shader;
    }

    @Override
    public void addData(final float[] floats, final int offset, final int length) {
        this.data.put(floats, offset, length);
    }

    @Override
    public void prepareNext(final Texture texture, final int requiredFloats) {
        if (requiredFloats > this.data.size()) {
            throw new IndexOutOfBoundsException(
                    requiredFloats + " floats required, but buffer size is only " + this.data.size());
        }
        final Texture baseTexture = texture == null ? null : texture.getBaseTexture();
        if (requiredFloats > this.data.remaining() || !Objects.equals(baseTexture, this.currentTexture)) {
            //flush BEFORE setting new texture
            forceFlush();
            this.currentTexture = baseTexture;
        }
    }

    @Override
    public void forceFlush() {
        if (this.data.used() == 0) {
            return;
        }
        if (this.currentTexture != null) {
            this.currentTexture.bindTexture(0);
        }
        final int count = this.data.used();
        this.vb.storeData(this.data.flush(), BufferUsage.Dynamic, count);
        LibAPIManager.instance().getGLFW().getRenderAPI().render(this.va, Primitive.Triangle,
                count / this.floatsPerVertex);
    }

    @Override
    public void init(final ModuleBatchingManager mgr) {
        final VertexBufferLayout layout = mgr.createLayout();
        this.floatsPerVertex = layout.getCount();
        this.data = new FloatCollector(this.vertexCount * this.floatsPerVertex);
        this.vb = LibAPIManager.instance().getGLFW().getRenderAPI().createVertexBuffer();
        this.va = LibAPIManager.instance().getGLFW().getRenderAPI().createVertexArray();
        this.va.addVertexBuffer(this.vb, layout);
    }

    @Override
    public void begin() {
        this.shader.bindShaderRenderReady();
    }
}
