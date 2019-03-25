package de.omnikryptec.render;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import de.omnikryptec.core.update.ILayer;
import de.omnikryptec.core.update.IUpdatable;
import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.window.WindowEvent;
import de.omnikryptec.render.storage.IRenderedObjectManager;
import de.omnikryptec.render.storage.RenderedObjectManager;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public class RendererContext implements IUpdatable {
    private static final Comparator<Renderer> RENDERER_PRIORITY_COMPARATOR = (e1, e2) -> e2.priority() - e1.priority();
    
    private Settings<Object> environmentSettings;
    private IProjection mainProjection;
    private IRenderedObjectManager objectManager;
    private RenderAPI renderApi;
    private List<Renderer> renderers;
    
    public RendererContext() {
        this(new RenderedObjectManager());
    }
    
    public RendererContext(IRenderedObjectManager mgr) {
        this(mgr, new Settings<>(new HashMap<>()));
    }
    
    public RendererContext(IRenderedObjectManager renderedObjManager, Settings<Object> environmentSettings) {
        this.environmentSettings = environmentSettings;
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
    
    public Settings<Object> getEnvironmentSettings() {
        return environmentSettings;
    }
    
    public void setMainProjection(IProjection projection) {
        this.mainProjection = projection;
    }
    
    public void addRenderer(Renderer renderer) {
        renderers.add(renderer);
        renderers.sort(RENDERER_PRIORITY_COMPARATOR);
        renderer.init(this);
        renderer.createAndResizeFBO(this, getRenderAPI().getWindow().getDefaultFrameBuffer());
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
    
    @EventSubscription
    public void event(WindowEvent.ScreenBufferResized ev) {
        for (Renderer r : renderers) {
            r.createAndResizeFBO(this, ev.window.getDefaultFrameBuffer());
        }
    }
    
    @Override
    public void init(ILayer layer) {
        layer.getEventBus().register(this);
    }
    
    @Override
    public void deinit(ILayer layer) {
        layer.getEventBus().unregister(this);
    }
    
    @Override
    public void update(Time time) {
        preRender(time, mainProjection);
        render(time, mainProjection);
        postRender(time, mainProjection);
    }
    
}
