package omnikryptec.shader_files;

import omnikryptec.shader.Shader;
import omnikryptec.shader.UniformSampler;
import omnikryptec.shader.UniformVec4;

public class FilterShader extends Shader {

	public final UniformVec4 channels = new UniformVec4("channels");
	public final UniformSampler sampler = new UniformSampler("tex");
	public final UniformSampler extra = new UniformSampler("extra");

	public FilterShader() {
		super(Shader.class.getResourceAsStream(DEFAULT_PP_VERTEX_SHADER_LOC),
				Shader.class.getResourceAsStream(Shader.oc_shader_loc + "extrainfo_reader_frag.glsl"),
				Shader.DEFAULT_PP_VERTEX_SHADER_POS_ATTR);
		registerUniforms(sampler, extra, channels);
		start();
		sampler.loadTexUnit(0);
		extra.loadTexUnit(1);
	}

}
