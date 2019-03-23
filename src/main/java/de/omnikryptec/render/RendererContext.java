package de.omnikryptec.render;

import de.omnikryptec.core.update.IUpdatable;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.render.storage.IRenderedObjectManager;
import de.omnikryptec.render.storage.RenderedObjectManager;
import de.omnikryptec.render.storage.RendererManager;
import de.omnikryptec.util.updater.Time;

public class RendererContext implements IUpdatable {
    
    private RendererManager rendererManager;
    private IProjection mainProjection;
    private IRenderedObjectManager objectManager;
    private RenderAPI renderApi;
    
    public RendererContext() {
        this(new RenderedObjectManager());
    }
    
    public RendererContext(IRenderedObjectManager renderedObjManager) {
        this.objectManager = renderedObjManager;
        this.rendererManager = new RendererManager();
    }
    
    public RenderAPI getRenderAPI() {
        return renderApi;
    }
    
    public IRenderedObjectManager getIRenderedObjectManager() {
        return objectManager;
    }
    
    public IProjection getMainProjection() {
        return mainProjection;
    }
    
    public void setMainProjection(IProjection projection) {
        this.mainProjection = projection;
    }
    
    public RendererManager getRendererManager() {
        return rendererManager;
    }
    
    @Override
    public void update(Time time) {
        rendererManager.preRender(time, mainProjection, this);
        rendererManager.render(time, mainProjection, this);
        rendererManager.postRender(time, mainProjection, this);
    }
    
    @Override
    public boolean passive() {
        return false;
    }
    
}
