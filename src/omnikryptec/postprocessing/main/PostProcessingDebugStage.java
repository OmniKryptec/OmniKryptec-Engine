package omnikryptec.postprocessing.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import omnikryptec.display.Display;
import omnikryptec.graphics.GraphicsUtil;
import omnikryptec.shader.files.postprocessing.DebugShader;
import omnikryptec.util.EnumCollection.BlendMode;
import omnikryptec.util.EnumCollection.DepthbufferType;

public class PostProcessingDebugStage extends PostProcessingStage {

	private List<Integer> disabled = new ArrayList<>();

	private DebugShader shader = new DebugShader();

	private boolean tmp;
	private int notr;

	public PostProcessingDebugStage() {
	}

	public PostProcessingDebugStage(Integer... is) {
		disabled.addAll(Arrays.asList(is));
	}

	public PostProcessingDebugStage disableIndex(int i) {
		disabled.add(i);
		return this;
	}

	public PostProcessingDebugStage enableIndex(int i) {
		disabled.remove((Integer) i);
		return this;
	}

	private int last = -1;
	private int side = 0;

	@Override
	public void render(FrameBufferObject before, List<FrameBufferObject> beforelist, int stage) {
		notr = 0;
		shader.start();
		if (last != beforelist.size() + 1 - disabled.size()) {
			side = calcQuadratic(last = beforelist.size() + 1 - disabled.size());
		}
		getFbo().bindFrameBuffer();
		GraphicsUtil.blendMode(BlendMode.ALPHA);
		GraphicsUtil.clear(0, 0, 0, 0);
		if (tmp = !disabled.contains(0)) {
			beforelist.get(0).bindDepthTexture(0);
			shader.info.loadVec3(side, 0, 0);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 6);
		}
		for (int i = 0; i < beforelist.size(); i++) {
			if (disabled.contains(i + 1)) {
				notr++;
				continue;
			}
			beforelist.get(i).bindToUnitOptimized(0);
			shader.info.loadVec3(side, i + (tmp ? 1 : 0) - notr, (i + (tmp ? 1 : 0) - notr) % side);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 6);
		}
		getFbo().unbindFrameBuffer();
	}

	private int calcQuadratic(int amount) {
		double sqrt = Math.sqrt(amount);
		return (int) Math.ceil(sqrt);
	}

	@Override
	protected FrameBufferObject createFbo() {
		return new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE);
	}

}