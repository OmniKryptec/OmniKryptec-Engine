package de.omnikryptec.render.postprocessing;

import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FBTarget.FBAttachmentFormat;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.shader.UniformFloat;
import de.omnikryptec.libapi.exposed.render.shader.UniformSampler;
import de.omnikryptec.render.renderer.View;
import de.omnikryptec.util.updater.Time;

public class BrightnessAccent extends AbstractPostProcessor {
    
    private UniformFloat poweru;
    
    private float power = 1;
    
    public BrightnessAccent() {
        shaderProgram.create("pp-vert", "pp-brightness-accent");
        UniformSampler sam = shaderProgram.getUniform("scene");
        poweru = shaderProgram.getUniform("power");
        shaderProgram.bindShader();
        sam.setSampler(0);
        poweru.loadFloat(1);
        buffer.assignTargetB(0, new FBTarget(FBAttachmentFormat.RGBA16, 0));
    }
    
    public void setPower(float f) {
        this.power = f;
    }
    
    @Override
    public Texture postprocess(Time time, View view, Texture sceneRaw) {
        buffer = buffer.resizeAndDeleteOrThis(sceneRaw.getWidth(), sceneRaw.getHeight());
        buffer.bindFrameBuffer();
        buffer.clearComplete();
        shaderProgram.bindShader();
        poweru.loadFloat(power);
        sceneRaw.bindTexture(0);
        QuadMesh.renderScreenQuad();
        buffer.unbindFrameBuffer();
        return buffer.getTexture(0);
    }
}
