package de.omnikryptec.render.renderer2;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.render.renderer.RendererContext.EnvironmentKey;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public class InstanceManager {
    
    private static class PriorityProjection {
        IProjection projection;
        int priority;
    }
    
    private static class ProjData {
        FrameBuffer target;
        Settings<EnvironmentKey> settings;
    }
    
    private static final Comparator<PriorityProjection> PRIO_COMP = (p1, p2) -> p1.priority - p2.priority;
    private static final Comparator<Renderer> REND_COMP = (r1, r2) -> r1.priority() - r2.priority();
    
    private IProjection mainProjection;
    private List<Renderer> renderers;
    private List<PriorityProjection> additionalProjections;
    private Map<IProjection, ProjData> additionalProjData;
    private boolean isInAction;
    
    public void addAdditionalRenderPass(IProjection proj, int prio) {
        PriorityProjection p = new PriorityProjection();
        p.projection = proj;
        p.priority = prio;
        additionalProjections.add(p);
        additionalProjections.sort(PRIO_COMP);
        additionalProjData.put(proj, new ProjData());
    }
    
    public void removeAdditionalRenderPass(IProjection proj) {
        additionalProjections.removeIf((p) -> p.projection == proj);
        additionalProjData.remove(proj);
    }
    
    public void setAdditionalTarget(IProjection key, FrameBuffer target) {
        additionalProjData.get(key).target = target;
    }
    
    public void setAdditionalEnvSettings(IProjection key, Settings<EnvironmentKey> settings) {
        additionalProjData.get(key).settings = settings;
    }
    
    public void addRenderer(Renderer r) {
        renderers.add(r);
        renderers.sort(REND_COMP);
    }
    
    public void renderInstance(Time time, FrameBuffer mainTarget, Settings<EnvironmentKey> envSettings) {
        if (isInAction) {
            throw new IllegalStateException("Can't call renderInstance recursively");
        }
        RenderAPI api = LibAPIManager.instance().getGLFW().getRenderAPI();
        isInAction = true;
        try {
            prepare(api, mainProjection, time);
            for (PriorityProjection p : additionalProjections) {
                ProjData projData = additionalProjData.get(p.projection);
                render(api, p.projection, projData.target, Util.defaultIfNull(envSettings, projData.settings),
                        time);
            }
            render(api, mainProjection, mainTarget, envSettings, time);
        } finally {
            isInAction = false;
        }
    }
    
    public void renderView(IProjection projection, Time time) {
        ProjData pd = additionalProjData.get(projection);
        render(LibAPIManager.instance().getGLFW().getRenderAPI(), projection, pd.target, pd.settings, time);
    }
    
    private void render(RenderAPI api, IProjection p, FrameBuffer fbo,
            Settings<EnvironmentKey> envSettings, Time time) {
        for (Renderer r : renderers) {
            r.render(this, api, p, fbo, envSettings, time);
        }
    }
    
    private void prepare(RenderAPI api, IProjection mainProjection, Time time) {
        for (Renderer r : renderers) {
            r.prepare(this, api, mainProjection, time);
        }
    }
}
