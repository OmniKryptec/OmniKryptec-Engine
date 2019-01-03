/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.old.postprocessing.main;

import de.omnikryptec.old.graphics.GraphicsUtil;
import de.omnikryptec.old.main.OmniKryptecEngine;
import de.omnikryptec.old.resource.model.Model;
import de.omnikryptec.old.util.Instance;
import de.omnikryptec.old.util.ModelUtil;
import de.omnikryptec.old.util.profiler.Profilable;
import de.omnikryptec.old.util.profiler.ProfileContainer;
import de.omnikryptec.old.util.profiler.Profiler;
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
