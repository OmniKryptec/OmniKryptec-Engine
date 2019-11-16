package de.omnikryptec.render.renderer;

import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;

//TODO this class seems ugly and does it even work? use FXAA (thats PP) instead of "classical" AA?
public class SceneRenderBufferManager {

    private FrameBuffer multisampledScene;
    private FrameBuffer[] scene;

    private final FBTarget[] targets;

    private final boolean multisampled;

    public SceneRenderBufferManager(final RenderAPI api, final int multisamples, final FBTarget... targets) {
        this.targets = targets;
        this.multisampled = multisamples > 0;

        if (targets.length == 0) {
            throw new IllegalArgumentException("requires at least one FBTarget");
        } else {
            final int width = api.getSurface().getWidth();
            final int height = api.getSurface().getHeight();
            this.multisampledScene = api.createFrameBuffer(width, height, multisamples, targets.length);
            this.multisampledScene.assignTargetsB(targets);
            if (targets.length > 1 || this.multisampled) {
                this.scene = new FrameBuffer[targets.length];
                for (int i = 0; i < this.scene.length; i++) {
                    this.scene[i] = api.createFrameBuffer(width, height, 0, 1);
                    this.scene[i].assignTargetB(0, targets[i]);
                }
            }
        }
    }

    public FrameBuffer get(final int index) {
        return this.scene == null ? this.multisampledScene : this.scene[index];
    }

    public void beginRender() {
        this.multisampledScene.bindFrameBuffer();
    }

    public void endRender() {
        this.multisampledScene.unbindFrameBuffer();
        if (this.scene != null) {
            for (int i = 0; i < this.scene.length; i++) {
                this.multisampledScene.resolveToFrameBuffer(this.scene[i], this.targets[i].attachmentIndex);
            }
        }
    }

    public void resize(final int width, final int height) {
        this.multisampledScene = this.multisampledScene.resizedClone(width, height);
        if (this.scene != null) {
            for (int i = 0; i < this.scene.length; i++) {
                this.scene[i] = this.scene[i].resizedClone(width, height);
            }
        }
    }

}
