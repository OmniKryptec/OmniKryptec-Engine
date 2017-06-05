package omnikryptec.postprocessing;

import java.util.List;

import org.lwjgl.opengl.GL11;

import omnikryptec.shader.Shader;
import omnikryptec.util.RenderUtil;

public abstract class PostProcessingStep implements PostProcessingStage {

	private Shader shader;
	private FrameBufferObject target;

	protected PostProcessingStep(){
		this.target = getOnResize();
	}
	
	protected PostProcessingStep(Shader shader) {
		this.shader = shader;
		this.target = getOnResize();
	}
	
	protected void setShader(Shader shader){
		this.shader = shader;
	}
	
	@Override
	public void render(FrameBufferObject before, List<FrameBufferObject> beforelist, int stage) {
		shader.start();
		bindTexture(before, beforelist, shader, stage);
		rendermyQuad();
		afterRendering();
	}
	
	
	@Override
	public FrameBufferObject getFbo() {
		return target;
	}

	public abstract void bindTexture(FrameBufferObject before, List<FrameBufferObject> beforelist, Shader using, int stage);

	public abstract void afterRendering();

	private void rendermyQuad() {
		target.bindFrameBuffer();
		renderQuad(true);
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
