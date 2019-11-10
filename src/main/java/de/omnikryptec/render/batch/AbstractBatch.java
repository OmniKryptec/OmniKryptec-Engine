package de.omnikryptec.render.batch;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.batch.module.ModuleBatchingManager;
import de.omnikryptec.render.batch.vertexmanager.VertexManager;

public abstract class AbstractBatch {

    protected final ModuleBatchingManager modBatchManager;
    protected final VertexManager vertexManager;
    private boolean rendering;

    public AbstractBatch(final VertexManager vertexManager) {
        this.vertexManager = vertexManager;
        this.modBatchManager = createManager();
        this.vertexManager.init(this.modBatchManager);
    }

    protected abstract ModuleBatchingManager createManager();

    protected final void issueVertices(final Texture texture) {
        checkRendering();
        this.modBatchManager.issueVertices(texture, this.vertexManager);
    }

    protected final void issuePreComputed(final Texture texture, final float[] floats, final int start,
            final int length) {
        checkRendering();
        this.modBatchManager.issuePreComputed(texture, this.vertexManager, floats, start, length);
    }

    @OverridingMethodsMustInvokeSuper
    public void begin() {
        this.rendering = true;
        this.vertexManager.begin();
    }

    @OverridingMethodsMustInvokeSuper
    public void end() {
        flush();
        this.rendering = false;
    }

    @OverridingMethodsMustInvokeSuper
    public void flush() {
        checkRendering();
        this.vertexManager.forceFlush();
    }

    public void drawPolygon(final Texture texture, final float[] poly, final int start, final int len) {
        issuePreComputed(texture, poly, start, len);
    }

    private final void checkRendering() {
        if (!this.isRendering()) {
            throw new IllegalStateException("not rendering");
        }
    }

    public final boolean isRendering() {
        return this.rendering;
    }
}
