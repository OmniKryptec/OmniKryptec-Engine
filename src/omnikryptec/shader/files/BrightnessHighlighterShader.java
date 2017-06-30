package omnikryptec.shader.files;

import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.UniformSampler;

public class BrightnessHighlighterShader extends Shader {

	public final UniformSampler scene = new UniformSampler("scene");

	public BrightnessHighlighterShader() {
		super(Shader.class.getResourceAsStream(DEFAULT_PP_VERTEX_SHADER_LOC),
				Shader.class.getResourceAsStream(oc_shader_loc + "bloom_shader_frag.glsl"),
				Shader.DEFAULT_PP_VERTEX_SHADER_POS_ATTR);
		registerUniforms(scene);
		start();
		scene.loadTexUnit(0);
	}

}