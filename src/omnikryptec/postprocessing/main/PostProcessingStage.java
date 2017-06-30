package omnikryptec.postprocessing.main;

import java.util.List;

import org.lwjgl.opengl.GL11;

import omnikryptec.util.RenderUtil;

public abstract class PostProcessingStage {

	public static final int INDEX_OPTION_USE_LAST_FBO = -1;

	private boolean enabled = true;
	private FrameBufferObject target;

	public PostProcessingStage() {
		target = createFbo();
	}

	public PostProcessingStage setEnabled(boolean b) {
		this.enabled = b;
		return this;
	}

	public boolean isEnabled() {
		return enabled;
	}

	protected void renderQuad(boolean clear) {
		target.bindFrameBuffer();
		if (clear) {
			RenderUtil.clear(0, 0, 0, 0);
		}
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		target.unbindFrameBuffer();
	}

	public FrameBufferObject getFbo() {
		return target;
	}

	public PostProcessingStage setDepthbuffer(FrameBufferObject fbo) {
		if (target != null) {
			fbo.resolveDepth(target);
		}
		return this;
	}

	public final void resize() {
		target = createFbo();
		onResize();
	}

	public final void renderAndResolveDepth(FrameBufferObject before, List<FrameBufferObject> beforelist, int stage) {
		render(before, beforelist, stage);
		setDepthbuffer(before);
	}

	/**
	 * 
	 * @param before
	 * @param beforelist
	 * @param stage
	 *            index of current PostProcessingStage (0-based)
	 */
	public abstract void render(FrameBufferObject before, List<FrameBufferObject> beforelist, int stage);

	protected abstract FrameBufferObject createFbo();

	protected boolean usesDefaultRenderObject() {
		return true;
	}

	protected void onResize() {
	}

}
