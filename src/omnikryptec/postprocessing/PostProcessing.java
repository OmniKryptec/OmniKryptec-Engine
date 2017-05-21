package omnikryptec.postprocessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import omnikryptec.util.RenderUtil;

public class PostProcessing {


	private static List<PostProcessingStage> stages = new ArrayList<>();
	private static List<FrameBufferObject> beforelist = new ArrayList<>();
	private static FrameBufferObject before;
	private static PostProcessingStage currentStage;
	
	public static void doPostProcessing(FrameBufferObject ...fbo) {
		before = fbo[fbo.length-1];
		beforelist.addAll(Arrays.asList(fbo));
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

	public static void cleanup() {
		for(int i=0; i<beforelist.size(); i++){
			beforelist.get(i).clear();
		}
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
