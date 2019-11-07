package de.omnikryptec.gui;

import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.render.batch.Batch2D;
import de.omnikryptec.render.batch.SimpleBatch2D;
import de.omnikryptec.render.renderer.LocalRendererContext;
import de.omnikryptec.render.renderer.Renderer;
import de.omnikryptec.util.updater.Time;

public class GuiRenderer implements Renderer {
    
    private GuiComponent componentRoot;
    private Batch2D batch;
    
    public GuiRenderer() {
        this.batch = new SimpleBatch2D(1000);
    }
    
    protected void setGui(GuiComponent componentRoot) {
        this.componentRoot = componentRoot;
    }
    
    @Override
    public void init(LocalRendererContext context, FrameBuffer target) {
    }
    
    @Override
    public void render(Time time, IProjection projection, LocalRendererContext context) {
        if (componentRoot != null) {
            batch.begin();
            componentRoot.render(batch);
            batch.end();
        }
    }
    
    @Override
    public void deinit(LocalRendererContext context) {
    }
    
}
