package de.omnikryptec.render.renderer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.RenderState;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.window.WindowEvent;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.settings.Defaultable;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public class RendererContext {
    
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
    
    private static final Comparator<LocalRendererContext> LOCAL_CONTEXT_PRIORITY_COMPARATOR = (e1, e2) -> e2.priority()
            - e1.priority();
    private static final RenderState DEFAULT_SCREENWRITER_STATE = RenderState.of();
    
    
    private RenderAPI renderApi;
    private List<LocalRendererContext> subContexts;
    
    public RendererContext() {
        this.renderApi = LibAPIManager.instance().getGLFW().getRenderAPI();
        this.subContexts = new ArrayList<>();
        LibAPIManager.ENGINE_EVENTBUS.register(this);
    }
    
    public RenderAPI getRenderAPI() {
        return renderApi;
    }
    
    public LocalRendererContext createLocal() {
        return createLocal(null, 0);
    }
    
    public LocalRendererContext createLocal(Settings<EnvironmentKey> environmentSettings, int multisamples,
            FBTarget... targets) {
        LocalRendererContext context = new LocalRendererContext(this, environmentSettings, multisamples, targets);
        subContexts.add(context);
        notifyPriorityChanged();
        return context;
    }
    
    @EventSubscription
    public void event(WindowEvent.ScreenBufferResized ev) {
        for (LocalRendererContext c : subContexts) {
            c.screenBufferResizedEventDelegate(ev);
        }
    }
    
    public void renderComplete(Time time) {
        Texture[] screen = new Texture[subContexts.size()];
        for (int i = 0; i < subContexts.size(); i++) {
            if (subContexts.get(i).isEnabled()) {
                screen[i] = subContexts.get(i).renderCycle(time);
            }
        }
        renderApi.getCurrentFrameBuffer().clearAll();
        renderApi.applyRenderState(DEFAULT_SCREENWRITER_STATE);
        RendererUtil.renderDirect(screen);
    }
    
    void notifyPriorityChanged() {
        subContexts.sort(LOCAL_CONTEXT_PRIORITY_COMPARATOR);
    }
    
}
