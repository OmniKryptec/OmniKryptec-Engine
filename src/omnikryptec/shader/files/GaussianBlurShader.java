package omnikryptec.shader.files;

import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.UniformBoolean;
import omnikryptec.shader.base.UniformFloat;
import omnikryptec.shader.base.UniformSampler;

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
