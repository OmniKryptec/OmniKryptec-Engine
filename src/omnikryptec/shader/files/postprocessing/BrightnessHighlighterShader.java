package omnikryptec.shader.files.postprocessing;

import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.UniformSampler;
import de.codemakers.io.file.AdvancedFile;

public class BrightnessHighlighterShader extends Shader {

    public final UniformSampler scene = new UniformSampler("scene");

    public BrightnessHighlighterShader() {
        super(DEF_SHADER_LOC_PP_VS,
                new AdvancedFile(true, SHADER_LOCATION_PP, "bloom_shader_frag.glsl"),
                Shader.DEFAULT_PP_VERTEX_SHADER_POS_ATTR);
        registerUniforms(scene);
        start();
        scene.loadTexUnit(0);
    }

}
