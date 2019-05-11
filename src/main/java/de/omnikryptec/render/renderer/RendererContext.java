package de.omnikryptec.render.renderer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.omnikryptec.core.update.ILayer;
import de.omnikryptec.core.update.IUpdatable;
import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.RenderState;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.window.WindowEvent;
import de.omnikryptec.render.batch.SimpleBatch2D;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.settings.Defaultable;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public class RendererContext implements IUpdatable {
    
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
    private SimpleBatch2D batch;
    private List<LocalRendererContext> subContexts;
    
    public RendererContext() {
        this.renderApi = LibAPIManager.instance().getGLFW().getRenderAPI();
        this.batch = new SimpleBatch2D(12);
        this.subContexts = new ArrayList<>();
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
        Texture[] screen = new Texture[subContexts.size()];
        for (int i = 0; i < subContexts.size(); i++) {
            if (subContexts.get(i).isEnabled()) {
                screen[i] = subContexts.get(i).renderCycle(time);
            }
        }
        renderApi.getCurrentFrameBuffer().clearAll();
        renderApi.applyRenderState(DEFAULT_SCREENWRITER_STATE);
        batch.begin();
        for (int i = 0; i < screen.length; i++) {
            if (screen[i] != null) {
                batch.draw(screen[i], null, false, false);
            }
        }
        batch.end();
    }
    
    void notifyPriorityChanged() {
        subContexts.sort(LOCAL_CONTEXT_PRIORITY_COMPARATOR);
    }
    
}
