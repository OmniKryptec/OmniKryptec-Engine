package omnikryptec.postprocessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import omnikryptec.display.DisplayManager;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.model.Model;
import omnikryptec.postprocessing.FrameBufferObject.DepthbufferType;
import omnikryptec.util.ModelUtil;
import omnikryptec.util.RenderUtil;

public class PostProcessing {


	private static List<PostProcessingStage> stages = new ArrayList<>();
	private static List<FrameBufferObject> beforelist = new ArrayList<>();
	private static FrameBufferObject before;
	private static PostProcessingStage currentStage;
	
	private static PostProcessing instance;
	
	private FrameBufferObject tmp;
	
	public static PostProcessing instance(){
		if(instance==null){
			new PostProcessing(DisplayManager.instance());
		}
		return instance;
	}
	
	private PostProcessing(DisplayManager manager){
		if(manager == null){
			throw new NullPointerException("DisplayManager is null");
		}
		instance = this;
	}
	
	public void doPostProcessing(FrameBufferObject[] fbos, FrameBufferObject ...fbo) {
		before = fbo[0];
		beforelist.addAll(Arrays.asList(fbo));
		beforelist.addAll(Arrays.asList(fbos));
		start();
		for (int i = 0; i < stages.size(); i++) {
			currentStage = stages.get(i);
			if(!currentStage.usesDefaultRenderObject()){
				end();
			}
			currentStage.render(before, beforelist, i);
			if(!currentStage.usesDefaultRenderObject()){
				start();
			}
			tmp = currentStage.getFbo();
			if(tmp.getDepthbufferType()==DepthbufferType.NONE){
				Logger.log("FBO of Stage "+i+" (zerobased) has no Depthbufferattachment. Some PostProcessingStages may not work anymore.", LogLevel.WARNING);
			}
			before.resolveDepth(tmp);
			before = tmp;
			beforelist.add(before);
		}
		end();
		before.resolveToScreen();
		beforelist.clear();
	}

	public static void cleanup() {
		for(int i=0; i<beforelist.size(); i++){
			beforelist.get(i).delete();
		}
	}
	
	public void addStage(PostProcessingStage stage){
		stages.add(stage);
	}
	
	public PostProcessingStage removeStage(PostProcessingStage stage){
		return stages.remove(stages.indexOf(stage));
	}
	
	private Model quad = ModelUtil.generateQuad();
	
	private void start() {
		quad.getVao().bind(0,1);
		RenderUtil.enableDepthTesting(false);
	}

	private void end() {
		RenderUtil.enableDepthTesting(true);
		quad.getVao().unbind(0,1);
	}

	public void resize() {
		for(PostProcessingStage stage : stages){
			stage.resize();
		}
	}

}
