package omnikryptec.shader.files;

import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.UniformSampler;
import omnikryptec.shader.base.UniformVec3;

public class DebugShader extends Shader {

	public final UniformSampler sampler = new UniformSampler("sampler");
	public final UniformVec3 info = new UniformVec3("info");

	public DebugShader() {
		super("DebugShader", Shader.class.getResourceAsStream(oc_shader_loc + "debug_shader_vert.glsl"),
				Shader.class.getResourceAsStream(oc_shader_loc + "debug_shader_frag.glsl"),
				Shader.DEFAULT_PP_VERTEX_SHADER_POS_ATTR);
		registerUniforms(sampler, info);
		start();
		sampler.loadTexUnit(0);
	}

}
