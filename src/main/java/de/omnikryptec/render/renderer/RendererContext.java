package de.omnikryptec.render.renderer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.omnikryptec.core.update.IUpdatable;
import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.RenderState;
import de.omnikryptec.libapi.exposed.render.RenderState.BlendMode;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.window.WindowEvent;
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

        private GlobalEnvironmentKeys(final Object def) {
            this.def = def;
        }

        @Override
        public <T> T getDefault() {
            return (T) this.def;
        }
    }

    private static final Comparator<LocalRendererContext> LOCAL_CONTEXT_PRIORITY_COMPARATOR = (e1, e2) -> e2.priority()
            - e1.priority();
    private static final RenderState DEFAULT_SCREENWRITER_STATE = RenderState.of(BlendMode.ALPHA);

    private final RenderAPI renderApi;
    private final List<LocalRendererContext> subContexts;

    public RendererContext() {
        this.renderApi = LibAPIManager.instance().getGLFW().getRenderAPI();
        this.subContexts = new ArrayList<>();
        LibAPIManager.ENGINE_EVENTBUS.register(this);
    }

    public RenderAPI getRenderAPI() {
        return this.renderApi;
    }

    public LocalRendererContext createLocal() {
        return createLocal(null, 0);
    }

    public LocalRendererContext createLocal(final Settings<EnvironmentKey> environmentSettings, final int multisamples,
            final FBTarget... targets) {
        final LocalRendererContext context = new LocalRendererContext(this, environmentSettings, multisamples, targets);
        this.subContexts.add(context);
        notifyPriorityChanged();
        return context;
    }

    @EventSubscription
    public void event(final WindowEvent.ScreenBufferResized ev) {
        for (final LocalRendererContext c : this.subContexts) {
            c.screenBufferResizedEventDelegate(ev);
        }
    }

    public void renderComplete(final Time time) {
        final Texture[] screen = new Texture[this.subContexts.size()];
        for (int i = 0; i < this.subContexts.size(); i++) {
            if (this.subContexts.get(i).isEnabled()) {
                screen[i] = this.subContexts.get(i).renderCycle(time);
            }
        }
        this.renderApi.getCurrentFrameBuffer().clearAll();
        this.renderApi.applyRenderState(DEFAULT_SCREENWRITER_STATE);
        RendererUtil.renderDirect(screen);
    }

    @Deprecated
    @Override
    public void update(final Time time) {
        renderComplete(time);
    }

    void notifyPriorityChanged() {
        this.subContexts.sort(LOCAL_CONTEXT_PRIORITY_COMPARATOR);
    }

}
