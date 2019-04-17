package de.omnikryptec.render.renderer;

import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;

//TODO this class seems ugly
public class SceneRenderBufferManager {
    
    private FrameBuffer multisampledScene;
    private FrameBuffer[] scene;
    
    private FBTarget[] targets;
    
    private boolean multisampled;
    
    public SceneRenderBufferManager(RenderAPI api, int multisamples, FBTarget... targets) {
        this.targets = targets;
        this.multisampled = multisamples > 0;
        
        if (targets.length == 0) {
            throw new IllegalArgumentException("requires at least one FBTarget");
        } else {
            int width = api.getSurface().getWidth();
            int height = api.getSurface().getHeight();
            multisampledScene = api.createFrameBuffer(width, height, multisamples, targets.length);
            multisampledScene.assignTargetsB(targets);
            if (targets.length > 1 || multisampled) {
                scene = new FrameBuffer[targets.length];
                for (int i = 0; i < scene.length; i++) {
                    scene[i] = api.createFrameBuffer(width, height, 0, 1);
                    scene[i].assignTargetB(0, targets[i]);
                }
            }
        }
    }
    
    public FrameBuffer get(int index) {
        return scene == null ? multisampledScene : scene[index];
    }
    
    public void beginRender() {
        multisampledScene.bindFrameBuffer();
    }
    
    public void endRender() {
        multisampledScene.unbindFrameBuffer();
        if (scene != null) {
            for (int i = 0; i < scene.length; i++) {
                multisampledScene.resolveToFrameBuffer(scene[i], targets[i].attachmentIndex);
            }
        }
    }
    
    public void resize(int width, int height) {
        multisampledScene = multisampledScene.resizedClone(width, height);
        if (scene != null) {
            for (int i = 0; i < scene.length; i++) {
                scene[i] = scene[i].resizedClone(width, height);
            }
        }
    }
    
}
