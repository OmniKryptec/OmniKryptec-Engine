package de.omnikryptec.postprocessing.main;

import omnikryptec.shader.base.Shader;

import java.util.List;

public abstract class PostProcessingStageShaded extends PostProcessingStage {

	private Shader shader;

	protected PostProcessingStageShaded() {
	}

	public PostProcessingStageShaded(Shader shader) {
		this.shader = shader;
	}

	protected final void setShader(Shader shader) {
		this.shader = shader;
	}
	
	protected final Shader getShader(){
		return shader;
	}
	
	
	@Override
	public void render(FrameBufferObject before, List<FrameBufferObject> beforelist, int stage) {
		shader.start();
		bindTexture(before, beforelist, shader, stage);
		renderQuad(true);
		afterRendering();
	}

	public abstract void bindTexture(FrameBufferObject before, List<FrameBufferObject> beforelist, Shader using,
			int stage);

	public void afterRendering() {
	}

}
