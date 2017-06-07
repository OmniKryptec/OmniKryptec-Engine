package omnikryptec.shader_files;

import omnikryptec.shader.Shader;
import omnikryptec.shader.UniformBoolean;
import omnikryptec.shader.UniformFloat;
import omnikryptec.shader.UniformSampler;

public class GaussianBlurShader extends Shader {

	public static final UniformFloat size = new UniformFloat("size");
	public static final UniformBoolean isHor = new UniformBoolean("hor");
	public static final UniformSampler sampler = new UniformSampler("tex");

	public GaussianBlurShader(String vertshader) {
		super(Shader.class.getResourceAsStream(Shader.oc_shader_loc + vertshader),
				Shader.class.getResourceAsStream(oc_shader_loc + "gaussian_blur_frag.glsl"),
				Shader.DEFAULT_PP_VERTEX_SHADER_POS_ATTR, size, isHor, sampler);
		start();
		sampler.loadTexUnit(0);
	}

}
