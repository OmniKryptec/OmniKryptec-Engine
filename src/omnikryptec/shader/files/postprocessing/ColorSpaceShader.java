package omnikryptec.shader.files.postprocessing;

import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.UniformSampler;
import omnikryptec.shader.base.UniformVec3;
import de.codemakers.io.file.AdvancedFile;

public class ColorSpaceShader extends Shader {

    public static final UniformSampler sampler = new UniformSampler("tex");
    public static final UniformVec3 value = new UniformVec3("levels");

    public ColorSpaceShader() {
        super(DEF_SHADER_LOC_PP_VS,
                new AdvancedFile(true, SHADER_LOCATION_PP, "color_space_shader_frag.glsl"),
                Shader.DEFAULT_PP_VERTEX_SHADER_POS_ATTR, value, sampler);
        start();
        sampler.loadTexUnit(0);
    }

}
