package de.omnikryptec.render.batch;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.batch.module.ModuleBatchingManager;

public abstract class AbstractBatch {
    
    protected final ModuleBatchingManager modBatchManager;
    protected final VertexManager vertexManager;
    private boolean rendering;
    
    public AbstractBatch(VertexManager vertexManager) {
        this.vertexManager = vertexManager;
        this.modBatchManager = createManager();
        this.vertexManager.init(modBatchManager);
    }
    
    protected abstract ModuleBatchingManager createManager();
    
    protected final void issueVertices(Texture texture) {
        checkRendering();
        modBatchManager.issueVertices(texture, vertexManager);
    }
    
    protected final void issuePreComputed(Texture texture, float[] floats, int start, int length) {
        checkRendering();
        modBatchManager.issuePreComputed(texture, vertexManager, floats, start, length);
    }
    
    @OverridingMethodsMustInvokeSuper
    public void begin() {
        this.rendering = true;
        vertexManager.begin();
    }
    
    @OverridingMethodsMustInvokeSuper
    public void end() {
        flush();
        this.rendering = false;
    }
    
    @OverridingMethodsMustInvokeSuper
    public void flush() {
        checkRendering();
        vertexManager.forceFlush();
    }
    
    public void drawPolygon(Texture texture, float[] poly, int start, int len) {
        issuePreComputed(texture, poly, start, len);
    }
    
    private final void checkRendering() {
        if (!this.isRendering()) {
            throw new IllegalStateException("not rendering");
        }
    }
    
    public final boolean isRendering() {
        return rendering;
    }
}
