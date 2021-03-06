package de.omnikryptec.render3.postprocessing;

import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FBTarget.FBAttachmentFormat;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.shader.UniformSampler;
import de.omnikryptec.libapi.exposed.render.shader.UniformVec2;
import de.omnikryptec.render3.structure.View;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.updater.Time;

public class EffectMixer extends AbstractPostProcessor {
    
    private Postprocessor effect;
    
    private UniformVec2 weights;
    
    private float sceneWeight;
    private float effectWeight;
    
    public void setWeightSource(float w) {
        this.sceneWeight = w;
    }
    
    public void setWeightEffect(float w) {
        this.effectWeight = w;
    }
    
    public EffectMixer(Postprocessor effect) {
        this.effect = Util.ensureNonNull(effect);
        buffer.assignTargetB(0, new FBTarget(FBAttachmentFormat.RGBA16, 0));
        shaderProgram.create("pp-vert", "pp-effect-mixer");
        weights = shaderProgram.getUniform("weights");
        UniformSampler tex1 = shaderProgram.getUniform("tex1");
        UniformSampler tex2 = shaderProgram.getUniform("tex2");
        shaderProgram.bindShader();
        tex1.setSampler(0);
        tex2.setSampler(1);
    }
    
    @Override
    public Texture postprocess(Time time, View view, Texture sceneRaw) {
        Texture effectTexture = effect.postprocess(time, view, sceneRaw);
        buffer = buffer.resizeAndDeleteOrThis(sceneRaw.getWidth(), sceneRaw.getHeight());
        buffer.bindFrameBuffer();
        buffer.clearComplete();
        shaderProgram.bindShader();
        weights.loadVec2(sceneWeight, effectWeight);
        sceneRaw.bindTexture(0);
        effectTexture.bindTexture(1);
        QuadMesh.renderScreenQuad();
        buffer.unbindFrameBuffer();
        return buffer.getTexture(0);
    }
    
}
