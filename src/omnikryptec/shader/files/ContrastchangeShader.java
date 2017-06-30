package omnikryptec.shader.files;

import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.UniformFloat;
import omnikryptec.shader.base.UniformSampler;

public class ContrastchangeShader extends Shader {

	public static final UniformFloat change = new UniformFloat("change");
	public static final UniformSampler scene = new UniformSampler("img");

	public ContrastchangeShader() {
		super(Shader.class.getResourceAsStream(DEFAULT_PP_VERTEX_SHADER_LOC),
				Shader.class.getResourceAsStream(oc_shader_loc + "contrastshanger_shader_frag.glsl"),
				DEFAULT_PP_VERTEX_SHADER_POS_ATTR, change, scene);
		start();
		scene.loadTexUnit(0);
	}

}
