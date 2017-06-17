package omnikryptec.shader_files;

import omnikryptec.shader.Shader;
import omnikryptec.shader.UniformFloat;
import omnikryptec.shader.UniformMatrix;
import omnikryptec.shader.UniformSampler;
import omnikryptec.shader.UniformVec2;
import omnikryptec.shader.UniformVec3;
import omnikryptec.shader.UniformVec4;

public class FogShader extends Shader {

	public final UniformSampler depth = new UniformSampler("depth");
	public final UniformSampler texture = new UniformSampler("tex");
	public final UniformVec2 pixsize = new UniformVec2("pixelSize");
	public final UniformVec4 fog = new UniformVec4("fog");

	public final UniformFloat density = new UniformFloat("density");
	public final UniformFloat gradient = new UniformFloat("gradient");

	public final UniformMatrix invprojv = new UniformMatrix("invprojv");

	public final UniformVec3 campos = new UniformVec3("campos");

	public FogShader() {
		super(FogShader.class.getResourceAsStream(DEFAULT_PP_VERTEX_SHADER_LOC),
				FogShader.class.getResourceAsStream(oc_shader_loc + "fog_shader_frag.glsl"),
				Shader.DEFAULT_PP_VERTEX_SHADER_POS_ATTR);
		registerUniforms(depth, texture, pixsize, fog, density, gradient, invprojv, campos);
		start();
		texture.loadTexUnit(0);
		depth.loadTexUnit(1);
	}

}
