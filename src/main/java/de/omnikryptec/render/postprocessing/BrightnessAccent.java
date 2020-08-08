package de.omnikryptec.render.postprocessing;

import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.FBTarget.FBAttachmentFormat;
import de.omnikryptec.libapi.exposed.render.shader.UniformSampler;
import de.omnikryptec.render.renderer.View;
import de.omnikryptec.util.updater.Time;

public class BrightnessAccent extends AbstractPostProcessor {
    
    public BrightnessAccent() {
        shader.create("pp-vert", "pp-brightness-accent");
        UniformSampler sam = shader.getUniform("scene");
        shader.bindShader();
        sam.setSampler(0);
        buffer.assignTargetB(0, new FBTarget(FBAttachmentFormat.RGBA16, 0));
    }
    
    @Override
    public Texture postprocess(Time time, View view, Texture sceneRaw) {
        buffer = buffer.resizeAndDeleteOrThis(sceneRaw.getWidth(), sceneRaw.getHeight());
        buffer.bindFrameBuffer();
        buffer.clearComplete();
        shader.bindShader();
        sceneRaw.bindTexture(0);
        PPMesh.renderPPMesh();
        buffer.unbindFrameBuffer();
        return buffer.getTexture(0);
    }
}
