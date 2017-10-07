package omnikryptec.shader.files.render;

import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.UniformSampler;
import omnikryptec.util.AdvancedFile;

public class GuiShader extends Shader {

	private final UniformSampler sampler = new UniformSampler("sampler");

	public GuiShader() {
		super(new AdvancedFile(SHADER_LOCATION_RENDER, "gui_vert.glsl"),
				new AdvancedFile(SHADER_LOCATION_RENDER, "gui_frag.glsl"), "pos", "rgba", "uv");
		registerUniforms(sampler);
		start();
		sampler.loadTexUnit(0);
	}

}
