package de.omnikryptec.postprocessing.main;

import omnikryptec.graphics.GraphicsUtil;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.resource.model.Model;
import omnikryptec.util.Instance;
import omnikryptec.util.ModelUtil;
import omnikryptec.util.profiler.Profilable;
import omnikryptec.util.profiler.ProfileContainer;
import omnikryptec.util.profiler.Profiler;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostProcessing implements Profilable {

	private static List<PostProcessingStage> stages = new ArrayList<>();
	private static List<FrameBufferObject> beforelist = new ArrayList<>();
	private static FrameBufferObject before;
	private static PostProcessingStage currentStage;

	private boolean enabled = true;
	private int stagecountactive = 0;
	private FrameBufferObject target;

	public PostProcessing(FrameBufferObject target) {
		if (!OmniKryptecEngine.isCreated()) {
			throw new NullPointerException("DisplayManager is null");
		}
		this.target = target;
		Profiler.addProfilable(this);
	}

	private double tmptime = 0;
	private double rendertime = 0;

	public void doPostProcessing(FrameBufferObject... fbo) {
		this.doPostProcessing(null, fbo);
	}

	public void doPostProcessing(FrameBufferObject[] fbos, FrameBufferObject... fbo) {
		before = fbo[0];
		stagecountactive = 0;
		rendertime = 0;
		if (enabled) {
			tmptime = Instance.getDisplayManager().getCurrentTime();
			beforelist.addAll(Arrays.asList(fbo));
			if (fbos != null) {
				beforelist.addAll(Arrays.asList(fbos));
			}
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
		if (target == null) {
			before.resolveToScreen();
		} else {
			before.resolveToFbo(target, GL30.GL_COLOR_ATTACHMENT0, true);
		}
		beforelist.clear();
	}

	public double getRenderTimeMS() {
		return rendertime;
	}

	public PostProcessing setEnabled(boolean b) {
		enabled = b;
		return this;
	}

	public FrameBufferObject getDisplayedFBO() {
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
		GraphicsUtil.enableDepthTesting(false);
	}

	private void end() {
		GraphicsUtil.enableDepthTesting(true);
	}

	public int getActiveStageCount() {
		return stagecountactive;
	}

	public void resize() {
		for (PostProcessingStage stage : stages) {
			stage.resize();
		}
	}

	@Override
	public ProfileContainer[] getProfiles() {
		return new ProfileContainer[] { new ProfileContainer(Profiler.POSTPROCESSOR, getRenderTimeMS()) };
	}

}
