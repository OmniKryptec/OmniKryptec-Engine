package omnikryptec.shader.files.postprocessing;

import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.UniformFloat;
import omnikryptec.shader.base.UniformSampler;
import omnikryptec.util.AdvancedFile;

public class ContrastchangeShader extends Shader {

	public static final UniformFloat change = new UniformFloat("change");
	public static final UniformSampler scene = new UniformSampler("img");

	public ContrastchangeShader() {
		super(DEF_SHADER_LOC_PP_VS,
				new AdvancedFile(SHADER_LOCATION_PP, "contrastshanger_shader_frag.glsl"),
				DEFAULT_PP_VERTEX_SHADER_POS_ATTR, change, scene);
		start();
		scene.loadTexUnit(0);
	}

}
