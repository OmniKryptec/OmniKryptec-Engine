package de.omnikryptec.graphics.render;

import java.util.ArrayList;
import java.util.List;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.RenderUtil;
import de.omnikryptec.libapi.exposed.render.FBTarget.TextureFormat;
import de.omnikryptec.libapi.exposed.window.WindowEvent;
import de.omnikryptec.libapi.exposed.window.WindowEvent.WindowResized;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public class MasterRenderer {
    
    public static final IntegerKey MultisampleSetting = IntegerKey.next(0);
    
    private final List<RendererSet> rendererSets;
    private final List<Postprocessor> postprocessors;
    
    private FrameBuffer sceneFBO;
    
    public MasterRenderer() {
        this.rendererSets = new ArrayList<>();
        this.postprocessors = new ArrayList<>();
        LibAPIManager.LIBAPI_EVENTBUS.register(this);
    }
    
    public void renderScene(final Time time, final RenderCollection scene, List<Viewport> mainViewports,
            final Settings<?> renderSettings) {
        for (final Viewport view : mainViewports) {
            if (view.requiresRefill()) {
                scene.fillViewport(view);
            }
        }
        RenderUtil.bindIfNonNull(sceneFBO);
        for (final RendererSet set : this.rendererSets) {
            set.prepareRenderers(time, renderSettings);
        }
        for (final Viewport view : mainViewports) {
            view.render(time, null, renderSettings);
        }
        for (final RendererSet set : this.rendererSets) {
            set.finishRenderers(time, renderSettings);
        }
        RenderUtil.unbindIfNonNull(sceneFBO);
        if (sceneFBO != null) {
            for (final Postprocessor ppro : this.postprocessors) {
                ppro.postprocess(time, sceneFBO, renderSettings);
            }
        }
    }
    
    public void addPostProcessor(final Postprocessor postprocessor) {
        this.postprocessors.add(postprocessor);
    }
    
    public void addRendererSet(final RendererSet rendererSet) {
        this.rendererSets.add(rendererSet);
    }
    
    @EventSubscription
    public void onWindowResized(final WindowEvent.WindowResized ev) {
    }
    
}
