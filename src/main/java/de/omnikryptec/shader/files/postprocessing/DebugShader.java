package de.omnikryptec.shader.files.postprocessing;

import de.codemakers.io.file.AdvancedFile;
import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.UniformSampler;
import omnikryptec.shader.base.UniformVec3;

public class DebugShader extends Shader {

	public final UniformSampler sampler = new UniformSampler("sampler");
	public final UniformVec3 info = new UniformVec3("info");

	public DebugShader() {
		super(new AdvancedFile(true, SHADER_LOCATION_PP, "debug_shader_vert.glsl"),
				new AdvancedFile(true, SHADER_LOCATION_PP, "debug_shader_frag.glsl"),
				Shader.DEFAULT_PP_VERTEX_SHADER_POS_ATTR);
		registerUniforms(sampler, info);
		start();
		sampler.loadTexUnit(0);
	}

}
