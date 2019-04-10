package de.omnikryptec.render;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import de.omnikryptec.core.update.ILayer;
import de.omnikryptec.core.update.IUpdatable;
import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.RenderAPI.SurfaceBufferType;
import de.omnikryptec.libapi.exposed.window.WindowEvent;
import de.omnikryptec.render.postprocessing.Postprocessor;
import de.omnikryptec.render.storage.IRenderedObjectManager;
import de.omnikryptec.render.storage.RenderedObjectManager;
import de.omnikryptec.util.settings.Defaultable;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;
import de.omnikryptec.util.data.Color;

public class RendererContext implements IUpdatable {
    private static final Comparator<Renderer> RENDERER_PRIORITY_COMPARATOR = (e1, e2) -> e2.priority() - e1.priority();
    
    public static interface EnvironmentKey {
    }
    
    public static enum GlobalEnvironmentKeys implements Defaultable, EnvironmentKey {
        ClearColor(new Color(0, 0, 0, 0));
        
        private final Object def;
        
        private GlobalEnvironmentKeys(Object def) {
            this.def = def;
        }
        
        @Override
        public <T> T getDefault() {
            return (T) def;
        }
    }
    //TODO Split stuff in 2 classes? RenderingData and RenderingFunctions or something?
    private Postprocessor postprocessor;
    private SceneRenderBufferManager frameBuffers;
    private RenderAPI renderApi;
    
    private Settings<EnvironmentKey> environmentSettings;
    
    private IProjection mainProjection;
    private IRenderedObjectManager objectManager;
    private List<Renderer> renderers;
    
    public RendererContext() {
        this(new RenderedObjectManager());
    }
    
    public RendererContext(IRenderedObjectManager mgr) {
        this(mgr, new Settings<>(new HashMap<>()));
    }
    
    public RendererContext(IRenderedObjectManager renderedObjManager, Settings<EnvironmentKey> environmentSettings) {
        this.environmentSettings = environmentSettings;
        this.objectManager = renderedObjManager;
        this.renderers = new ArrayList<>();
        this.renderApi = RenderAPI.get();
        //TODO take in as arguments or something?
        this.frameBuffers = new SceneRenderBufferManager(this.renderApi, 0);
        
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
    
    public Settings<EnvironmentKey> getEnvironmentSettings() {
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
        renderApi.setClearColor(getEnvironmentSettings().get(GlobalEnvironmentKeys.ClearColor));
        renderApi.clear(SurfaceBufferType.Color, SurfaceBufferType.Depth);
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
        frameBuffers.resize(ev.width, ev.height);
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
        this.frameBuffers.beginRender();
        preRender(time, mainProjection);
        render(time, mainProjection);
        postRender(time, mainProjection);
        this.frameBuffers.endRender();
        if (this.postprocessor != null) {
            this.postprocessor.postprocess(time, frameBuffers);
        } else if (frameBuffers.is()) {
            //TODO make better framebuffer resolve
            frameBuffers.get(0).resolveToFrameBuffer(getRenderAPI().getWindow().getDefaultFrameBuffer(),
                    frameBuffers.get(0).targets()[0].attachmentIndex);
        }
    }
    
}
