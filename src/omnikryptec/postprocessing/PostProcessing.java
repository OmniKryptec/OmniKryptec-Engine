package omnikryptec.postprocessing;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import omnikryptec.renderer.RenderUtil;

public class PostProcessing {

	//private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };

	// public static void init(Loader loader) {
	// quad = loader.loadToVAO(POSITIONS, 2);
	// }

	private static List<PostProcessingStage> stages = new ArrayList<>();
	private static List<FrameBufferObject> beforelist = new ArrayList<>();
	private static FrameBufferObject before;
	private static PostProcessingStage currentStage;
	
	public static void doPostProcessing(FrameBufferObject fbo) {
		before = fbo;
		beforelist.add(before);
		start();
		for (int i = 0; i < stages.size(); i++) {
			currentStage = stages.get(i);
			if(!currentStage.usesDefaultRenderObject()){
				end();
			}
			currentStage.render(before, beforelist);
			if(!currentStage.usesDefaultRenderObject()){
				start();
			}
			before = currentStage.getFbo();
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
		RenderUtil.enableDepthTesting(false);
	}

	private static void end() {
		RenderUtil.enableDepthTesting(true);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}

}
