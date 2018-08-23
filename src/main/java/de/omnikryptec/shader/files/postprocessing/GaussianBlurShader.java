package de.omnikryptec.shader.files.postprocessing;

import de.codemakers.io.file.AdvancedFile;
import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.UniformBoolean;
import omnikryptec.shader.base.UniformFloat;
import omnikryptec.shader.base.UniformSampler;

public class GaussianBlurShader extends Shader {

    public static final UniformFloat size = new UniformFloat("size");
    public static final UniformBoolean isHor = new UniformBoolean("hor");
    public static final UniformSampler sampler = new UniformSampler("tex");

    public GaussianBlurShader(String vertshader) {
        super(new AdvancedFile(true, SHADER_LOCATION_PP, vertshader),
                new AdvancedFile(true, SHADER_LOCATION_PP, "gaussian_blur_frag.glsl"),
                Shader.DEFAULT_PP_VERTEX_SHADER_POS_ATTR, size, isHor, sampler);
        start();
        sampler.loadTexUnit(0);
    }

}
