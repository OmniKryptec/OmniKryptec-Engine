/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

import de.omnikryptec.graphics.display.Display;
import de.omnikryptec.old.shader.base.Shader;
import de.omnikryptec.old.util.EnumCollection.DepthbufferType;

import java.util.List;

public class PostProcessingStageRenderedSimple extends PostProcessingStageShaded {

    public PostProcessingStageRenderedSimple(Shader shader) {
	setShader(shader);
    }

    private int l_ind = -1;

    public PostProcessingStageRenderedSimple setListIndex(int i) {
	l_ind = i;
	return this;
    }

    @Override
    public void bindTexture(FrameBufferObject before, List<FrameBufferObject> beforelist, Shader using, int stage) {
	(l_ind < 0 ? before : beforelist.get(l_ind)).bindToUnit(0);
    }

    @Override
    protected FrameBufferObject createFbo() {
	return new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE);
    }

}
