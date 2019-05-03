package de.omnikryptec.render.batch;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import de.omnikryptec.libapi.exposed.render.Texture;

public abstract class AbstractBatch {
    
    private final ModuleBatchingManager mgr;
    private final VertexManager vertexManager;
    private boolean rendering;
    
    public AbstractBatch(VertexManager vertexManager) {
        this.vertexManager = vertexManager;
        this.mgr = createManager();
        this.vertexManager.init(mgr);
    }
    
    protected abstract ModuleBatchingManager createManager();
    
    protected final void issueVertices(Texture baseTexture) {
        checkRendering();
        mgr.issueVertices(baseTexture, vertexManager);
    }
    
    protected final void issuePreComputed(Texture baseTexture, float[] floats, int start, int length) {
        checkRendering();
        mgr.issuePreComputed(baseTexture, vertexManager, floats, start, length);
    }
    
    @OverridingMethodsMustInvokeSuper
    public void begin() {
        this.rendering = true;
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
