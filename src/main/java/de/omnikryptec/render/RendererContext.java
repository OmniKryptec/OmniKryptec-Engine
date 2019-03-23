package de.omnikryptec.render;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.omnikryptec.core.update.IUpdatable;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.render.storage.IRenderedObjectManager;
import de.omnikryptec.render.storage.RenderedObjectManager;
import de.omnikryptec.util.updater.Time;

public class RendererContext implements IUpdatable {
    private static final Comparator<Renderer> RENDERER_PRIORITY_COMPARATOR = (e1, e2) -> e2.priority() - e1.priority();
    
    private IProjection mainProjection;
    private IRenderedObjectManager objectManager;
    private RenderAPI renderApi;
    private List<Renderer> renderers;
    
    public RendererContext() {
        this(new RenderedObjectManager());
    }
    
    public RendererContext(IRenderedObjectManager renderedObjManager) {
        this.objectManager = renderedObjManager;
        this.renderers = new ArrayList<>();
        this.renderApi = RenderAPI.get();
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
    
    public void addRenderer(Renderer renderer) {
        renderers.add(renderer);
        renderers.sort(RENDERER_PRIORITY_COMPARATOR);
        renderer.init(this);
    }
    
    public void removeRenderer(Renderer renderer) {
        renderer.deinit(this);
        renderers.remove(renderer);
    }
    
    public void preRender(Time time, IProjection projection) {
        for (Renderer r : renderers) {
            r.preRender(time, projection, this);
        }
    }
    
    public void render(Time time, IProjection projection) {
        for (Renderer r : renderers) {
            r.render(time, projection, this);
        }
    }
    
    public void postRender(Time time, IProjection projection) {
        for (Renderer r : renderers) {
            r.postRender(time, projection, this);
        }
    }
    
    @Override
    public void update(Time time) {
        preRender(time, mainProjection);
        render(time, mainProjection);
        postRender(time, mainProjection);
    }
    
    @Override
    public boolean passive() {
        return false;
    }
    
}
