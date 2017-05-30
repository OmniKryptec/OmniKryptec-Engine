package omnikryptec.shader_files;

import omnikryptec.shader.Shader;
import omnikryptec.shader.UniformSampler;

public class FogShader extends Shader {

	public static final UniformSampler depth = new UniformSampler("depth");
	public static final UniformSampler texture = new UniformSampler("tex");
	
	public FogShader() {
		super(FogShader.class.getResourceAsStream(DEFAULT_PP_VERTEX_SHADER_LOC), FogShader.class.getResourceAsStream(oc_shader_loc+"fog_shader_frag.glsl"), Shader.DEFAULT_PP_VERTEX_SHADER_POS_ATTR, depth, texture);
		start();
		texture.loadTexUnit(0);
		depth.loadTexUnit(1);
	}

}
