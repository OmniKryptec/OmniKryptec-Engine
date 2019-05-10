package de.omnikryptec.render.renderer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.joml.Matrix4f;

import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FBTarget.FBAttachmentFormat;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.RenderAPI.SurfaceBufferType;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.window.WindowEvent;
import de.omnikryptec.render.Camera;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.render.objects.IRenderedObjectManager;
import de.omnikryptec.render.objects.RenderedObjectManager;
import de.omnikryptec.render.postprocessing.Postprocessor;
import de.omnikryptec.render.renderer.RendererContext.EnvironmentKey;
import de.omnikryptec.render.renderer.RendererContext.GlobalEnvironmentKeys;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

//TODO make Renderers or even LocalRendererContexts "global transformable"
//TODO also improve target information for renderers (null => surface, FrameBuffer => take its width and height and stay like that?)
public class LocalRendererContext {
    private static final Comparator<Renderer> RENDERER_PRIORITY_COMPARATOR = (e1, e2) -> e2.priority() - e1.priority();
    
    private RendererContext context;
    //TODO postprocessing
    private Postprocessor postprocessor;
    private SceneRenderBufferManager frameBuffers;
    
    private IProjection mainProjection;
    private IRenderedObjectManager objectManager;
    private List<Renderer> renderers;
    
    private Settings<EnvironmentKey> environmentSettings;
    
    private int prio;
    
    LocalRendererContext(RendererContext context, Settings<EnvironmentKey> environmentSettings, int multisamples,
            FBTarget... targets) {
        this.context = context;
        this.renderers = new ArrayList<>();
        if (targets == null || targets.length == 0) {
            this.frameBuffers = new SceneRenderBufferManager(getRenderAPI(), multisamples,
                    new FBTarget(FBAttachmentFormat.RGBA16, 0));
        } else {
            this.frameBuffers = new SceneRenderBufferManager(getRenderAPI(), multisamples, targets);
        }
        this.environmentSettings = environmentSettings == null ? new Settings<>() : environmentSettings;
        this.objectManager = new RenderedObjectManager();
        this.mainProjection = new Camera(new Matrix4f().ortho2D(0, 1, 0, 1));
    }
    
    public void setPriority(int i) {
        this.prio = i;
        this.context.notifyPriorityChanged();
    }
    
    public int priority() {
        return prio;
    }
    //using this while having renderers added breaks things, so use this only after initialization
    public void setIRenderedObjectManager(IRenderedObjectManager mgr) {
        this.objectManager = mgr;
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
        renderer.init(this, context.getRenderAPI().getSurface());
    }
    
    public void removeRenderer(Renderer renderer) {
        renderer.deinit(this);
        renderers.remove(renderer);
    }
    
    public RenderAPI getRenderAPI() {
        return context.getRenderAPI();
    }
    
    public void preRender(Time time, IProjection projection) {
        context.getRenderAPI().getCurrentFrameBuffer().clear(
                getEnvironmentSettings().get(GlobalEnvironmentKeys.ClearColor), SurfaceBufferType.Color,
                SurfaceBufferType.Depth);
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
    
    public Texture renderCycle(Time time) {
        this.frameBuffers.beginRender();
        preRender(time, mainProjection);
        render(time, mainProjection);
        postRender(time, mainProjection);
        this.frameBuffers.endRender();
        if (this.postprocessor != null) {
            return this.postprocessor.postprocess(time, frameBuffers);
        } else {
            return frameBuffers.get(0).getTexture(0);
        }
    }
    
    void screenBufferResizedEventDelegate(WindowEvent.ScreenBufferResized ev) {
        frameBuffers.resize(ev.width, ev.height);
        for (Renderer r : renderers) {
            r.resizeFBOs(this, ev.surface);
        }
    }
}
