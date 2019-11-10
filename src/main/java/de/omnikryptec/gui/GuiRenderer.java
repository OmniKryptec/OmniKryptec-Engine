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
    private final Batch2D batch;

    public GuiRenderer() {
        this.batch = new SimpleBatch2D(1000);
    }

    protected void setGui(final GuiComponent componentRoot) {
        this.componentRoot = componentRoot;
    }

    @Override
    public void init(final LocalRendererContext context, final FrameBuffer target) {
    }

    @Override
    public void render(final Time time, final IProjection projection, final LocalRendererContext context) {
        if (this.componentRoot != null) {
            this.batch.begin();
            this.componentRoot.render(this.batch);
            this.batch.end();
        }
    }

    @Override
    public void deinit(final LocalRendererContext context) {
    }

}
