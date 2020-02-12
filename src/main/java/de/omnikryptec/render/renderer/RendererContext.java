/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
import de.omnikryptec.render.renderer2.ViewManager.EnvironmentKey;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public class RendererContext implements IUpdatable {

    
    private static final Comparator<LocalRendererContext> LOCAL_CONTEXT_PRIORITY_COMPARATOR = (e1,
            e2) -> e2.getPriority() - e1.getPriority();
    private static final RenderState DEFAULT_SCREENWRITER_STATE = RenderState.of(BlendMode.ALPHA);
    
    private final RenderAPI renderApi;
    private final List<LocalRendererContext> subContexts;
    private final List<LocalRendererContext> subContextsActive;
    
    public RendererContext() {
        this.renderApi = LibAPIManager.instance().getGLFW().getRenderAPI();
        this.subContexts = new ArrayList<>();
        this.subContextsActive = new ArrayList<>();
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
        return context;
    }
    
    public void addLocal(LocalRendererContext lrc) {
        if (lrc.getContext() != this) {
            throw new IllegalArgumentException("LocalRendererContext does not belong to this context");
        }
        this.subContextsActive.add(lrc);
        notifyPriorityChanged();
    }
    
    public void removeLocal(LocalRendererContext lrc) {
        this.subContextsActive.remove(lrc);
    }
    
    @EventSubscription
    public void event(final WindowEvent.ScreenBufferResized ev) {
        for (final LocalRendererContext c : this.subContexts) {
            c.screenBufferResizedEventDelegate(ev);
        }
    }
    
    public void renderComplete(final Time time) {
        final Texture[] screen = new Texture[this.subContextsActive.size()];
        for (int i = 0; i < this.subContextsActive.size(); i++) {
            if (this.subContextsActive.get(i).isEnabled()) {
                screen[this.subContextsActive.size() - i - 1] = this.subContextsActive.get(i).renderCycle(time);
            }
        }
        this.renderApi.getCurrentFrameBuffer().clearComplete();
        this.renderApi.applyRenderState(DEFAULT_SCREENWRITER_STATE);
        RendererUtil.renderDirect(screen);
    }
    
    @Deprecated
    @Override
    public void update(final Time time) {
        renderComplete(time);
    }
    
    void notifyPriorityChanged() {
        this.subContextsActive.sort(LOCAL_CONTEXT_PRIORITY_COMPARATOR);
    }
    
}
