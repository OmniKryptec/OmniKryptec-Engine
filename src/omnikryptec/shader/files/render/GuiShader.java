package omnikryptec.shader.files.render;

import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.UniformSampler;
import omnikryptec.util.AdvancedFile;

public class GuiShader extends Shader {

	private final UniformSampler sampler = new UniformSampler("sampler");

	public GuiShader() {
		super(new AdvancedFile("omnikryptec", "test", "test.vert"),
				new AdvancedFile("omnikryptec", "test", "test.frag"), "pos", "rgba", "uv");
		registerUniforms(sampler);
		start();
		sampler.loadTexUnit(0);
	}

}
