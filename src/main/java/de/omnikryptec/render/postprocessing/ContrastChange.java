package de.omnikryptec.render.postprocessing;

import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FBTarget.FBAttachmentFormat;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.shader.UniformFloat;
import de.omnikryptec.libapi.exposed.render.shader.UniformSampler;
import de.omnikryptec.render.renderer.View;
import de.omnikryptec.util.updater.Time;

public class ContrastChange extends AbstractPostProcessor {
    
    private UniformFloat change;
    private UniformSampler sampler;
    private float changef = 0;
    
    public ContrastChange() {
        shader.create("pp-vert", "pp-contrast-frag");
        change = shader.getUniform("change");
        sampler = shader.getUniform("img");
        shader.bindShader();
        sampler.setSampler(0);
        buffer.assignTargetB(0, new FBTarget(FBAttachmentFormat.RGBA16, 0));
    }
    
    @Override
    public Texture postprocess(Time time, View view, Texture sceneRaw) {
        buffer = buffer.resizeAndDeleteOrThis(sceneRaw.getWidth(), sceneRaw.getHeight());
        buffer.bindFrameBuffer();
        buffer.clearComplete();
        shader.bindShader();
        change.loadFloat(changef);
        sceneRaw.bindTexture(0);
        QuadMesh.renderScreenQuad();
        buffer.unbindFrameBuffer();
        return buffer.getTexture(0);
    }
    
    public void setContrastChange(float f) {
        this.changef = f;
    }
}
