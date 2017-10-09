package omnikryptec.shader.files.render;

import omnikryptec.graphics.DrawBatch;
import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.UniformMatrix;
import omnikryptec.shader.base.UniformSampler;
import omnikryptec.util.AdvancedFile;

public class GuiShader extends Shader {

	private final UniformSampler sampler = new UniformSampler("sampler");
	private final UniformMatrix projview = new UniformMatrix("projview");
	
	public GuiShader() {
		super(new AdvancedFile(SHADER_LOCATION_RENDER, "gui_vert.glsl"),
				new AdvancedFile(SHADER_LOCATION_RENDER, "gui_frag.glsl"), "pos", "rgba", "uv");
		registerUniforms(sampler, projview);
		start();
		sampler.loadTexUnit(0);
	}
	
	@Override
	public void onDrawBatchStart(DrawBatch batch) {
		projview.loadMatrix(batch.getCamera().getProjectionViewMatrix());
	}

}
