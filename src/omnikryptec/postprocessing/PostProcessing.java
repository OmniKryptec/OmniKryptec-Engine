package omnikryptec.postprocessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import omnikryptec.display.DisplayManager;
import omnikryptec.model.Model;
import omnikryptec.util.RenderUtil;

public class PostProcessing {


	private static List<PostProcessingStage> stages = new ArrayList<>();
	private static List<FrameBufferObject> beforelist = new ArrayList<>();
	private static FrameBufferObject before;
	private static PostProcessingStage currentStage;
	
	private static PostProcessing instance;
	
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
	
	public void doPostProcessing(FrameBufferObject ...fbo) {
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
		if(stages.size()>0){
			beforelist.clear();
			before.resolveToScreen();
		}else{
			beforelist.get(0).resolveToScreen();
			beforelist.clear();
		}
	}

	public static void cleanup() {
		for(int i=0; i<beforelist.size(); i++){
			beforelist.get(i).clear();
		}
	}
	
	public void addStage(PostProcessingStage stage){
		stages.add(stage);
	}
	
	public PostProcessingStage removeStage(PostProcessingStage stage){
		return stages.remove(stages.indexOf(stage));
	}
	
	private Model quad = Model.generateQuad();
	
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
