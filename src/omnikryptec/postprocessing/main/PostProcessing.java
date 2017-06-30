package omnikryptec.postprocessing.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import omnikryptec.display.DisplayManager;
import omnikryptec.model.Model;
import omnikryptec.util.Instance;
import omnikryptec.util.ModelUtil;
import omnikryptec.util.RenderUtil;
import omnikryptec.util.profiler.Profilable;
import omnikryptec.util.profiler.ProfileContainer;
import omnikryptec.util.profiler.Profiler;

public class PostProcessing implements Profilable{

	private static List<PostProcessingStage> stages = new ArrayList<>();
	private static List<FrameBufferObject> beforelist = new ArrayList<>();
	private static FrameBufferObject before;
	private static PostProcessingStage currentStage;

	private static PostProcessing instance;

	// private FrameBufferObject tmp;

	private boolean enabled = true;
	private int stagecountactive=0;
	
	public static PostProcessing instance() {
		if (instance == null) {
			new PostProcessing(DisplayManager.instance());
		}
		return instance;
	}

	private PostProcessing(DisplayManager manager) {
		if (manager == null) {
			throw new NullPointerException("DisplayManager is null");
		}
		Profiler.addProfilable(this, 2);
		instance = this;
	}
	
	private double tmptime = 0;
	private double rendertime=0;
	
	public void doPostProcessing(FrameBufferObject[] fbos, FrameBufferObject... fbo) {
		before = fbo[0];
		stagecountactive = 0;
		rendertime = 0;
		if (enabled) {
			tmptime = Instance.getDisplayManager().getCurrentTime();
			beforelist.addAll(Arrays.asList(fbo));
			beforelist.addAll(Arrays.asList(fbos));
			start();
			for (int i = 0; i < stages.size(); i++) {
				currentStage = stages.get(i);
				if (currentStage.isEnabled()) {
					if (!currentStage.usesDefaultRenderObject()) {
						end();
					}
					currentStage.renderAndResolveDepth(before, beforelist, i);
					if (!currentStage.usesDefaultRenderObject()) {
						start();
					}
					before = currentStage.getFbo();
					beforelist.add(before);
					stagecountactive++;
				}
			}
			end();
			rendertime = Instance.getDisplayManager().getCurrentTime() - tmptime;
		}
		before.resolveToScreen();
		beforelist.clear();
	}

	public double getRenderTimeMS(){
		return rendertime;
	}
	
	public PostProcessing setEnabled(boolean b) {
		enabled = b;
		return this;
	}
	
	public FrameBufferObject getDisplayedFBO(){
		return before;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public static void cleanup() {
		for (int i = 0; i < beforelist.size(); i++) {
			beforelist.get(i).delete();
		}
	}

	public void addStage(PostProcessingStage stage) {
		stages.add(stage);
	}

	public PostProcessingStage removeStage(PostProcessingStage stage) {
		return stages.remove(stages.indexOf(stage));
	}

	private Model quad = ModelUtil.generateQuad();

	private void start() {
		quad.getVao().bind(0, 1);
		RenderUtil.enableDepthTesting(false);
	}

	private void end() {
		RenderUtil.enableDepthTesting(true);
	}
	
	public int getActiveStageCount(){
		return stagecountactive;
	}
	
	public void resize() {
		for (PostProcessingStage stage : stages) {
			stage.resize();
		}
	}

	@Override
	public ProfileContainer[] getProfiles() {
		return new ProfileContainer[]{new ProfileContainer(Profiler.POSTPROCESSOR, getRenderTimeMS())};
	}

}
