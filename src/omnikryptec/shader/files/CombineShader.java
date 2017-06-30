package omnikryptec.shader.files;

import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.UniformSampler;
import omnikryptec.shader.base.UniformVec2;

public class CombineShader extends Shader {

	public final UniformSampler sampler1 = new UniformSampler("tex1");
	public final UniformSampler sampler2 = new UniformSampler("tex2");
	public final UniformVec2 weights = new UniformVec2("weights");

	public CombineShader() {
		super(Shader.class.getResourceAsStream(DEFAULT_PP_VERTEX_SHADER_LOC),
				Shader.class.getResourceAsStream(oc_shader_loc + "combine_shader_frag.glsl"),
				Shader.DEFAULT_PP_VERTEX_SHADER_POS_ATTR);
		registerUniforms(sampler1, sampler2, weights);
		start();
		sampler1.loadTexUnit(0);
		sampler2.loadTexUnit(1);
	}

}
