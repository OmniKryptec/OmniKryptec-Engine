package de.omnikryptec.render.batch;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import de.omnikryptec.libapi.exposed.render.Texture;

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
    
    protected final void issueVertices(Texture baseTexture) {
        checkRendering();
        modBatchManager.issueVertices(baseTexture, vertexManager);
    }
    
    protected final void issuePreComputed(Texture baseTexture, float[] floats, int start, int length) {
        checkRendering();
        modBatchManager.issuePreComputed(baseTexture, vertexManager, floats, start, length);
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
    
    private final void checkRendering() {
        if (!this.isRendering()) {
            throw new IllegalStateException("not rendering");
        }
    }
    
    public final boolean isRendering() {
        return rendering;
    }
}
