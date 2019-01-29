package de.omnikryptec.graphics.render;

import java.util.ArrayList;
import java.util.List;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.window.WindowEvent;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public class MasterRenderer {

    private final List<RendererSet> rendererSets;
    private final List<Viewport> mainViewports;
    private final List<Postprocessor> postprocessors;

    private FrameBuffer sceneFBO;

    public MasterRenderer() {
        this.rendererSets = new ArrayList<>();
        this.mainViewports = new ArrayList<>();
        this.postprocessors = new ArrayList<>();
        LibAPIManager.LIBAPI_EVENTBUS.register(this);
    }

    public void renderScene(final Time time, final RenderCollection scene, final Settings<?> renderSettings) {
        //filter objs in viewports
        for (final Viewport view : this.mainViewports) {
            if (view.requiresRefill()) {
                scene.fillViewport(view);
            }
        }
        this.sceneFBO.bindFrameBuffer();
        //pre tasks
        for (final RendererSet set : this.rendererSets) {
            set.prepareRenderers(time, renderSettings);
        }
        //normal render
        for (final Viewport view : this.mainViewports) {
            view.render(time, null, renderSettings);
        }
        //post tasks
        for (final RendererSet set : this.rendererSets) {
            set.finishRenderers(time, renderSettings);
        }
        this.sceneFBO.unbindFrameBuffer();
        //postprocessing
        for (final Postprocessor ppro : this.postprocessors) {
            ppro.postprocess(time, null, renderSettings);
        }
    }

    public void addSceneViewport(final Viewport viewport) {
        this.mainViewports.add(viewport);
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
