package omnikryptec.shader.files.render;

import omnikryptec.graphics.SpriteBatch;
import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.UniformMatrix;
import omnikryptec.shader.base.UniformSampler;
import omnikryptec.util.AdvancedFile;

public class Shader2D extends Shader {

	private final UniformSampler sampler = new UniformSampler("sampler");
	private final UniformMatrix projview = new UniformMatrix("projview");
	
	public Shader2D() {
		super(new AdvancedFile(SHADER_LOCATION_RENDER, "2d_vert.glsl"),
				new AdvancedFile(SHADER_LOCATION_RENDER, "2d_frag.glsl"), "pos", "rgba", "uv");
		registerUniforms(sampler, projview);
		start();
		sampler.loadTexUnit(0);
	}
	
	@Override
	public void onDrawBatchStart(SpriteBatch batch) {
		projview.loadMatrix(batch.getCamera().getProjectionViewMatrix());
	}

}
