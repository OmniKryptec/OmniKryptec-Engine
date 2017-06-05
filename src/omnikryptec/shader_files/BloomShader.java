package omnikryptec.shader_files;

import omnikryptec.shader.Shader;
import omnikryptec.shader.UniformSampler;

public class BloomShader extends Shader {

	public final UniformSampler scene = new UniformSampler("scene");
	public final UniformSampler extra = new UniformSampler("extra");
	
	public BloomShader() {
		super(Shader.class.getResourceAsStream(DEFAULT_PP_VERTEX_SHADER_LOC), Shader.class.getResourceAsStream(oc_shader_loc+"bloom_shader_frag.glsl"), Shader.DEFAULT_PP_VERTEX_SHADER_POS_ATTR);
		registerUniforms(scene, extra);
		start();
		scene.loadTexUnit(0);
		extra.loadTexUnit(1);
	}

}
