package de.omnikryptec.render;

import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.opengl.OpenGLUtil;
//TODO this class seems ugly
public class SceneRenderBufferManager {
    
    private FrameBuffer multisampledScene;
    private FrameBuffer[] scene;
    
    private FBTarget[] targets;
    
    private boolean multisampled;
    private boolean hasTargets;
    
    public SceneRenderBufferManager(RenderAPI api, int multisamples, FBTarget... targets) {
        this.targets = targets;
        this.multisampled = multisamples > 0;
        this.hasTargets = targets.length > 0;
        if (multisampled && !hasTargets) {
            throw new IllegalArgumentException("multisampling requires at least one FBTarget");
        } else if (hasTargets) {
            //TODO enable multisampling but better!
            OpenGLUtil.setMultisample(true);
            int width = api.getSurface().getWidth();
            int height = api.getSurface().getHeight();
            multisampledScene = api.createFrameBuffer(width, height, multisamples, targets.length);
            multisampledScene.bindFrameBuffer();
            multisampledScene.assignTargets(targets);
            multisampledScene.unbindFrameBuffer();
            if (targets.length > 1 || multisampled) {
                scene = new FrameBuffer[targets.length];
                for (int i = 0; i < scene.length; i++) {
                    scene[i] = api.createFrameBuffer(width, height, 0, 1);
                    scene[i].bindFrameBuffer();
                    scene[i].assignTarget(0, targets[i]);
                    scene[i].unbindFrameBuffer();
                }
            }
        }
    }
    
    public FrameBuffer get(int index) {
        return scene == null ? multisampledScene : scene[index];
    }
    
    public void beginRender() {
        if (hasTargets) {
            multisampledScene.bindFrameBuffer();
        }
    }
    
    public void endRender() {
        if (hasTargets) {
            multisampledScene.unbindFrameBuffer();
            if (scene != null) {
                for (int i = 0; i < scene.length; i++) {
                    multisampledScene.resolveToFrameBuffer(scene[i], targets[i].attachmentIndex);
                }
            }
        }
    }
    
    public void resize(int width, int height) {
        if (hasTargets) {
            multisampledScene = multisampledScene.resizedClone(width, height);
            if (scene != null) {
                for (int i = 0; i < scene.length; i++) {
                    scene[i] = scene[i].resizedClone(width, height);
                }
            }
        }
    }
    
    public boolean is() {
        return hasTargets;
    }
}
