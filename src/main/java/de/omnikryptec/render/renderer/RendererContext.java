package de.omnikryptec.render.renderer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.omnikryptec.core.update.ILayer;
import de.omnikryptec.core.update.IUpdatable;
import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.window.WindowEvent;
import de.omnikryptec.render.batch.ShadedBatch2D;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.settings.Defaultable;
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
    
    private RenderAPI renderApi;
    private ShadedBatch2D batch;
    private List<LocalRendererContext> subContexts;
    
    public RendererContext() {
        this.renderApi = LibAPIManager.instance().getGLFW().getRenderAPI();
        this.batch = new ShadedBatch2D(12);
        this.subContexts = new ArrayList<>();
    }
    
    public RenderAPI getRenderAPI() {
        return renderApi;
    }
    
    public LocalRendererContext createLocal() {
        LocalRendererContext context = new LocalRendererContext(this);
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
        int width = renderApi.getSurface().getWidth();
        int height = renderApi.getSurface().getHeight();
        for (int i = 0; i < subContexts.size(); i++) {
            screen[i] = subContexts.get(i).renderCycle(time);
        }
        renderApi.getCurrentFrameBuffer().clearAll();
        //TODO renderstate for the screen batch
        batch.begin();
        for (Texture t : screen) {
            batch.draw(t, null, width, height, false, false);
        }
        batch.end();
    }
    
    void notifyPriorityChanged() {
        subContexts.sort(LOCAL_CONTEXT_PRIORITY_COMPARATOR);
    }
    
}
