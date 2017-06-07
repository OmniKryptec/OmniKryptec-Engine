package omnikryptec.shader_files;

import omnikryptec.shader.Shader;
import omnikryptec.shader.UniformFloat;
import omnikryptec.shader.UniformSampler;
import omnikryptec.shader.UniformVec2;
import omnikryptec.shader.UniformVec4;

public class FogShader extends Shader {

	public static final UniformSampler depth = new UniformSampler("depth");
	public static final UniformSampler texture = new UniformSampler("tex");
	public static final UniformVec2 test = new UniformVec2("planes");
	public static final UniformVec4 fog = new UniformVec4("fog");

	public static final UniformFloat density = new UniformFloat("density");
	public static final UniformFloat gradient = new UniformFloat("gradient");

	public FogShader() {
		super(FogShader.class.getResourceAsStream(DEFAULT_PP_VERTEX_SHADER_LOC),
				FogShader.class.getResourceAsStream(oc_shader_loc + "fog_shader_frag.glsl"),
				Shader.DEFAULT_PP_VERTEX_SHADER_POS_ATTR, depth, texture, test, fog, density, gradient);
		start();
		texture.loadTexUnit(0);
		depth.loadTexUnit(1);
	}

}
