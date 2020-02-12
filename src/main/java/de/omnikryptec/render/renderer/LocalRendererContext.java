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
import de.omnikryptec.render.postprocessing.SceneRenderBufferManager;
import de.omnikryptec.render.renderer2.ViewManager.EnvironmentKey;
import de.omnikryptec.render.renderer2.ViewManager.GlobalEnvironmentKeys;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

@Deprecated
public class LocalRendererContext {
    
    private static final Comparator<OofRenderer> RENDERER_PRIORITY_COMPARATOR = (e1, e2) -> e2.priority() - e1.priority();
    
    private final RendererContext context;
    
    private Postprocessor postprocessor;
    private SceneRenderBufferManager frameBuffers;
    
    private IProjection mainProjection;
    private IRenderedObjectManager objectManager;
    private final List<OofRenderer> renderers;
    
    private final Settings<EnvironmentKey> environmentSettings;
    
    private int prio;
    private boolean enabled;
    
    LocalRendererContext(final RendererContext context, final Settings<EnvironmentKey> environmentSettings,
            final int multisamples, final FBTarget... targets) {
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
        this.enabled = true;
    }
    
    public void setPriority(final int i) {
        this.prio = i;
        this.context.notifyPriorityChanged();
    }
    
    public int getPriority() {
        return this.prio;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void setEnabled(final boolean b) {
        this.enabled = b;
    }
    
    //using this while having renderers added breaks things, so use this only after initialization
    public void setIRenderedObjectManager(final IRenderedObjectManager mgr) {
        this.objectManager = mgr;
    }
    
    public IRenderedObjectManager getIRenderedObjectManager() {
        return this.objectManager;
    }
    
    public IProjection getMainProjection() {
        return this.mainProjection;
    }
    
    public Settings<EnvironmentKey> getEnvironmentSettings() {
        return this.environmentSettings;
    }
    
    public void setMainProjection(final IProjection projection) {
        this.mainProjection = projection;
    }
    
    public void addRenderer(final OofRenderer renderer) {
        this.renderers.add(renderer);
        this.renderers.sort(RENDERER_PRIORITY_COMPARATOR);
        renderer.init(this, this.context.getRenderAPI().getSurface());
    }
    
    public void removeRenderer(final OofRenderer renderer) {
        renderer.deinit(this);
        this.renderers.remove(renderer);
    }
    
    public RenderAPI getRenderAPI() {
        return this.context.getRenderAPI();
    }
    
    RendererContext getContext() {
        return this.context;
    }
    
    public void preRender(final Time time, final IProjection projection) {
        this.context.getRenderAPI().getCurrentFrameBuffer().clear(
                getEnvironmentSettings().get(GlobalEnvironmentKeys.ClearColor), SurfaceBufferType.Color,
                SurfaceBufferType.Depth);
        for (final OofRenderer r : this.renderers) {
            r.preRender(time, projection, this);
        }
    }
    
    public void render(final Time time, final IProjection projection) {
        for (final OofRenderer r : this.renderers) {
            r.render(time, projection, this);
        }
    }
    
    public void postRender(final Time time, final IProjection projection) {
        for (final OofRenderer r : this.renderers) {
            r.postRender(time, projection, this);
        }
    }
    
    public Texture renderCycle(final Time time) {
        this.frameBuffers.beginRender();
        preRender(time, this.mainProjection);
        render(time, this.mainProjection);
        postRender(time, this.mainProjection);
        this.frameBuffers.endRender();
        if (this.postprocessor != null) {
            return this.postprocessor.postprocess(time, this.frameBuffers);
        } else {
            return this.frameBuffers.get(0).getTexture(0);
        }
    }
    
    void screenBufferResizedEventDelegate(final WindowEvent.ScreenBufferResized ev) {
        this.frameBuffers.resize(ev.width, ev.height);
        for (final OofRenderer r : this.renderers) {
            r.resizeFBOs(this, ev.surface);
        }
    }
}
