package omnikryptec.shader.files.postprocessing;

import de.codemakers.io.file.AdvancedFile;
import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.UniformFloat;
import omnikryptec.shader.base.UniformMatrix;
import omnikryptec.shader.base.UniformSampler;
import omnikryptec.shader.base.UniformVec2;
import omnikryptec.shader.base.UniformVec3;
import omnikryptec.shader.base.UniformVec4;

public class FogShader extends Shader {

    public final UniformSampler depth = new UniformSampler("depth");
    public final UniformSampler texture = new UniformSampler("tex");
    public final UniformVec2 pixsize = new UniformVec2("pixelSize");
    public final UniformVec4 fog = new UniformVec4("fog");

    public final UniformFloat density = new UniformFloat("density");
    public final UniformFloat gradient = new UniformFloat("gradient");

    public final UniformMatrix invprojv = new UniformMatrix("invprojv");

    public final UniformVec3 campos = new UniformVec3("campos");

    public FogShader() {
        super(DEF_SHADER_LOC_PP_VS,
                new AdvancedFile(true, SHADER_LOCATION_PP, "fog_shader_frag.glsl"),
                Shader.DEFAULT_PP_VERTEX_SHADER_POS_ATTR);
        registerUniforms(depth, texture, pixsize, fog, density, gradient, invprojv, campos);
        start();
        texture.loadTexUnit(0);
        depth.loadTexUnit(1);
    }

}
