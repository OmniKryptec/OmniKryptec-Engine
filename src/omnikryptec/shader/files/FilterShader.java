package omnikryptec.shader.files;

import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.UniformSampler;
import omnikryptec.shader.base.UniformVec4;

public class FilterShader extends Shader {

	public final UniformVec4 channels = new UniformVec4("channels");
	public final UniformSampler sampler = new UniformSampler("tex");
	public final UniformSampler extra = new UniformSampler("extra");

	public FilterShader() {
		super("FilterShader", Shader.class.getResourceAsStream(DEFAULT_PP_VERTEX_SHADER_LOC),
				Shader.class.getResourceAsStream(Shader.oc_shader_loc + "extrainfo_reader_frag.glsl"),
				Shader.DEFAULT_PP_VERTEX_SHADER_POS_ATTR);
		registerUniforms(sampler, extra, channels);
		start();
		sampler.loadTexUnit(0);
		extra.loadTexUnit(1);
	}

}
