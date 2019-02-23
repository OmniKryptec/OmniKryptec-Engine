package de.omnikryptec.render;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderUtil;
import de.omnikryptec.libapi.exposed.window.WindowEvent;
import de.omnikryptec.render.frame.RenderCollection;
import de.omnikryptec.render.frame.RendererSet;
import de.omnikryptec.render.frame.Viewport;
import de.omnikryptec.render.postprocessing.Postprocessor;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class MasterRenderer {
    
    public static final IntegerKey MultisampleSetting = IntegerKey.next(0);
    
    private final List<RendererSet> rendererSets;
    private final List<Postprocessor> postprocessors;
    
    private Settings<?> renderSettings;
    
    private FrameBuffer sceneFBO;
    
    public MasterRenderer(Settings<?> renderSettings) {
        this.renderSettings = renderSettings;
        this.rendererSets = new ArrayList<>();
        this.postprocessors = new ArrayList<>();
        LibAPIManager.LIB_API_EVENT_BUS.register(this);
    }
    
    public void renderScene(final Time time, final RenderCollection scene, final List<Viewport> mainViewports) {
        for (final Viewport view : mainViewports) {
            if (view.requiresRefill()) {
                scene.fillViewport(view);
            }
        }
        RenderUtil.bindIfNonNull(this.sceneFBO);
        for (final RendererSet set : this.rendererSets) {
            set.prepareRenderers(time);
        }
        for (final Viewport view : mainViewports) {
            view.render(time, null);
        }
        for (final RendererSet set : this.rendererSets) {
            set.finishRenderers(time);
        }
        RenderUtil.unbindIfNonNull(this.sceneFBO);
        if (this.sceneFBO != null) {
            for (final Postprocessor ppro : this.postprocessors) {
                ppro.postprocess(time, this.sceneFBO);
            }
        }
    }
    
    public void addPostProcessor(final Postprocessor postprocessor) {
        this.postprocessors.add(postprocessor);
    }
    
    public void addRendererSet(final RendererSet rendererSet) {
        this.rendererSets.add(rendererSet);
        rendererSet.initRenderers(renderSettings);
    }
    
    @EventSubscription
    public void onWindowResized(final WindowEvent.WindowResized ev) {
    }
    
}
