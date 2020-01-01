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

package de.omnikryptec.old.postprocessing.stages;

import de.omnikryptec.graphics.display.Display;
import de.omnikryptec.old.postprocessing.main.FrameBufferObject;
import de.omnikryptec.old.postprocessing.main.PostProcessingStageShaded;
import de.omnikryptec.old.shader.base.Shader;
import de.omnikryptec.old.shader.files.postprocessing.FilterShader;
import de.omnikryptec.old.util.EnumCollection.DepthbufferType;
import org.joml.Vector4f;

import java.util.List;

public class FilterStage extends PostProcessingStageShaded {

    private FilterShader shader = new FilterShader();

    private Vector4f channels = new Vector4f();

    private int[] l_ind = { -1, 3 };

    public FilterStage(float r, float g, float b, float a) {
	setShader(shader);
	channels.set(r, g, b, a);
    }

    public FilterStage(Vector4f rgba) {
	this(rgba.x, rgba.y, rgba.z, rgba.w);
    }

    public FilterStage setListIndices(int before, int extra) {
	l_ind[0] = before;
	l_ind[1] = extra;
	return this;
    }

    @Override
    public void bindTexture(FrameBufferObject before, List<FrameBufferObject> beforelist, Shader using, int stage) {
	(l_ind[0] < 0 ? before : beforelist.get(l_ind[0])).bindToUnitOptimized(0);
	(l_ind[1] < 0 ? before : beforelist.get(l_ind[1])).bindToUnitOptimized(1);
	shader.channels.loadVec4(channels);
    }

    @Override
    protected FrameBufferObject createFbo() {
	return new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE);
    }

}
