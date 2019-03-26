package de.omnikryptec.render;

import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;

public class SceneRenderBufferManager {
    
    private FrameBuffer multisampledScene;
    private FrameBuffer[] scene;
    
    private FBTarget[] targets;
    
    public SceneRenderBufferManager(RenderAPI api, int width, int height, int multisamples, FBTarget... targets) {
        this.targets = targets;
        multisampledScene = api.createFrameBuffer(width, height, multisamples, targets.length);
        multisampledScene.bindFrameBuffer();
        multisampledScene.assignTargets(targets);
        multisampledScene.unbindFrameBuffer();
        scene = new FrameBuffer[targets.length];
        for (int i = 0; i < scene.length; i++) {
            scene[i] = api.createFrameBuffer(width, height, 0, 1);
            scene[i].bindFrameBuffer();
            scene[i].assignTarget(0, targets[i]);
            scene[i].unbindFrameBuffer();
        }
    }
    
    public FrameBuffer get(int index) {
        return scene[index];
    }
    
    public void beginRender() {
        multisampledScene.bindFrameBuffer();
    }
    
    public void endRender() {
        multisampledScene.unbindFrameBuffer();
        for (int i = 0; i < scene.length; i++) {
            multisampledScene.resolveToFrameBuffer(scene[i], targets[i].attachmentIndex);
        }
    }
    
    public void resize(int width, int height) {
        multisampledScene = multisampledScene.resizedClone(width, height);
        for (int i = 0; i < scene.length; i++) {
            scene[i] = scene[i].resizedClone(width, height);
        }
    }
}
