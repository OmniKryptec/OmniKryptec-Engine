package omnikryptec.shader_files;

import omnikryptec.shader.Shader;
import omnikryptec.shader.UniformSampler;
import omnikryptec.shader.UniformVec3;

public class ColorSpaceShader extends Shader {

	public static final UniformSampler sampler = new UniformSampler("tex");
	public static final UniformVec3 value = new UniformVec3("levels");

	public ColorSpaceShader() {
		super(Shader.class.getResourceAsStream(Shader.DEFAULT_PP_VERTEX_SHADER_LOC),
				Shader.class.getResourceAsStream(Shader.oc_shader_loc + "color_space_shader_frag.glsl"),
				Shader.DEFAULT_PP_VERTEX_SHADER_POS_ATTR, value, sampler);
		start();
		sampler.loadTexUnit(0);
	}

}
