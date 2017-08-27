package omnikryptec.shader.files.postprocessing;

import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.UniformSampler;
import omnikryptec.shader.base.UniformVec4;
import omnikryptec.util.AdvancedFile;

public class FilterShader extends Shader {

	public final UniformVec4 channels = new UniformVec4("channels");
	public final UniformSampler sampler = new UniformSampler("tex");
	public final UniformSampler extra = new UniformSampler("extra");

	public FilterShader() {
		super(DEF_SHADER_LOC_PP_VS,
				new AdvancedFile(SHADER_LOCATION_PP, "extrainfo_reader_frag.glsl"),
				Shader.DEFAULT_PP_VERTEX_SHADER_POS_ATTR);
		registerUniforms(sampler, extra, channels);
		start();
		sampler.loadTexUnit(0);
		extra.loadTexUnit(1);
	}

}
