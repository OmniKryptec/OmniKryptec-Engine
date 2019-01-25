package de.omnikryptec.graphics.render;

import java.util.ArrayList;
import java.util.List;

import de.omnikryptec.util.settings.Settings;

public class MasterRenderer {

    private List<RendererSet> rendererSets;
    private List<Viewport> mainViewports;
    private List<Postprocessor> postprocessors;

    public MasterRenderer() {
        rendererSets = new ArrayList<>();
        mainViewports = new ArrayList<>();
        postprocessors = new ArrayList<>();
    }

    public void renderScene(RenderCollection scene, Settings<?> renderSettings) {
        //filter objs in viewports
        for (Viewport view : mainViewports) {
            scene.fillViewport(view);
        }
        //pre tasks
        for (RendererSet set : rendererSets) {
            set.prepareRenderers(renderSettings);
        }
        //normal render
        for (Viewport view : mainViewports) {
            view.render(null/* TODO scene fbo */, renderSettings);
        }
        //post tasks
        for (RendererSet set : rendererSets) {
            set.finishRenderers(renderSettings);
        }
        //postprocessing
        for (Postprocessor ppro : postprocessors) {
            ppro.postprocess(null/* TODO scene fbo */, renderSettings);
        }
    }

    public void addSceneViewport(Viewport viewport) {
        this.mainViewports.add(viewport);
    }

    public void addPostProcessor(Postprocessor postprocessor) {
        this.postprocessors.add(postprocessor);
    }
    
    public void addRendererSet(RendererSet rendererSet) {
        this.rendererSets.add(rendererSet);
    }

}
