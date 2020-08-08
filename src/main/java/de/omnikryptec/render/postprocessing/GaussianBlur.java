package de.omnikryptec.render.postprocessing;

import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.FBTarget.FBAttachmentFormat;
import de.omnikryptec.libapi.exposed.render.shader.UniformBool;
import de.omnikryptec.libapi.exposed.render.shader.UniformFloat;
import de.omnikryptec.libapi.exposed.render.shader.UniformSampler;
import de.omnikryptec.render.renderer.View;
import de.omnikryptec.util.updater.Time;

public class GaussianBlur extends AbstractPostProcessor {
    
    public static enum BlurType {
        Horizontal, Vertical;
    }
    
    public static PostprocessingBundle createGaussianBlurBundle(float scale) {
        PostprocessingBundle bundle = new PostprocessingBundle();
        bundle.add(new GaussianBlur(BlurType.Horizontal, scale));
        bundle.add(new GaussianBlur(BlurType.Vertical, scale));
        return bundle;
    }
    
    private boolean horizontal;
    private UniformFloat sizeu;
    private float scale = 1;
    
    public GaussianBlur(BlurType blur, float scale) {
        this.horizontal = blur == BlurType.Horizontal;
        this.scale = scale;
        this.buffer.assignTargetB(0, new FBTarget(FBAttachmentFormat.RGBA16, 0));
        this.shader.create("pp-gaussian-blur");
        UniformBool hor = shader.getUniform("hor");
        sizeu = shader.getUniform("size");
        UniformSampler sampler = shader.getUniform("tex");
        shader.bindShader();
        hor.loadBoolean(horizontal);
        sampler.setSampler(0);
    }
    
    @Override
    public Texture postprocess(Time time, View view, Texture sceneRaw) {
        buffer = buffer.resizeAndDeleteOrThis((int) (sceneRaw.getWidth() * scale),
                (int) (sceneRaw.getHeight() * scale));
        buffer.bindFrameBuffer();
        buffer.clearComplete();
        shader.bindShader();
        sizeu.loadFloat(horizontal ? buffer.getWidth() : buffer.getHeight());
        sceneRaw.bindTexture(0);
        PPMesh.renderPPMesh();
        buffer.unbindFrameBuffer();
        return buffer.getTexture(0);
    }
    
}
