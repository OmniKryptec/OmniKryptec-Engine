package de.omnikryptec.graphics.render;

import java.util.ArrayList;
import java.util.List;

import de.omnikryptec.util.settings.Settings;

public class MasterRenderer {
    
    private final List<RendererSet> rendererSets;
    private final List<Viewport> mainViewports;
    private final List<Postprocessor> postprocessors;
    
    public MasterRenderer() {
        this.rendererSets = new ArrayList<>();
        this.mainViewports = new ArrayList<>();
        this.postprocessors = new ArrayList<>();
    }
    
    public void renderScene(final RenderCollection scene, final Settings<?> renderSettings) {
        //filter objs in viewports
        for (final Viewport view : this.mainViewports) {
            scene.fillViewport(view);
        }
        //pre tasks
        for (final RendererSet set : this.rendererSets) {
            set.prepareRenderers(renderSettings);
        }
        //normal render
        for (final Viewport view : this.mainViewports) {
            view.render(null/* TODO scene fbo */, renderSettings);
        }
        //post tasks
        for (final RendererSet set : this.rendererSets) {
            set.finishRenderers(renderSettings);
        }
        //postprocessing
        for (final Postprocessor ppro : this.postprocessors) {
            ppro.postprocess(null/* TODO scene fbo */, renderSettings);
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
    
}
