package omnikryptec.postprocessing;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import omnikryptec.renderer.RenderUtil;

public class PostProcessing {

	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };

	// public static void init(Loader loader) {
	// quad = loader.loadToVAO(POSITIONS, 2);
	// }

	private static List<PostProcessingStage> stages = new ArrayList<>();
	private static List<Fbo> beforelist = new ArrayList<>();
	private static Fbo before;

	public static void doPostProcessing(Fbo fbo) {
		before = fbo;
		beforelist.add(before);
		start();
		for (int i = 0; i < stages.size(); i++) {
			stages.get(i).render(before, beforelist);
			before = stages.get(i).getFbo();
			beforelist.add(before);
		}
		end();
		beforelist.clear();
		before.resolveToScreen();
	}

	public static void cleanUp() {
	}

	private static void start() {
		// GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		RenderUtil.enableDepthTest(false);
	}

	private static void end() {
		RenderUtil.enableDepthTest(true);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}

}
