package omnikryptec.postprocessing;

import java.util.List;

import org.lwjgl.opengl.GL11;

import omnikryptec.shader.Shader;
import omnikryptec.util.RenderUtil;

public abstract class PostProcessingStep implements PostProcessingStage {

	private Shader shader;
	private FrameBufferObject target;

	protected PostProcessingStep(Shader shader) {
		this.shader = shader;
		this.target = getOnResize();
	}

	@Override
	public void render(FrameBufferObject before, List<FrameBufferObject> beforelist, int stage) {
		shader.start();
		bindTexture(before, beforelist, shader, stage);
		renderQuad();
		afterRendering();
	}
	
	
	@Override
	public FrameBufferObject getFbo() {
		return target;
	}

	public abstract void bindTexture(FrameBufferObject before, List<FrameBufferObject> beforelist, Shader using, int stage);

	public abstract void afterRendering();

	private void renderQuad() {
		target.bindFrameBuffer();
		RenderUtil.clear(0, 0, 0, 1);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		target.unbindFrameBuffer();
	}

	public abstract FrameBufferObject getOnResize();
	
	@Override
	public void resize(){
		target = getOnResize();
	}
	
	public void cleanUp() {
		target.delete();
	}
}
