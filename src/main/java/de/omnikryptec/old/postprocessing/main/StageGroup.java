/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL30;

import de.omnikryptec.graphics.display.Display;
import de.omnikryptec.old.util.EnumCollection.DepthbufferType;

public class StageGroup extends PostProcessingStage {

    private List<PostProcessingStage> stages = new ArrayList<>();

    @Override
    public void render(FrameBufferObject before, List<FrameBufferObject> beforelist, int stage) {
	for (int i = 0; i < stages.size(); i++) {
	    stages.get(i).renderAndResolveDepth(before, beforelist, stage);
	    before = stages.get(i).getFbo();
	}
	before.resolveToFbo(getFbo(), GL30.GL_COLOR_ATTACHMENT0);

    }

    @Override
    public FrameBufferObject createFbo() {
	return new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE);
    }

    @Override
    protected void onResize() {
	for (int i = 0; i < stages.size(); i++) {
	    stages.get(i).resize();
	}
    }

}
