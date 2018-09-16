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

package de.omnikryptec.postprocessing.stages;

import java.util.List;

import org.joml.Vector3f;

import de.omnikryptec.display.Display;
import de.omnikryptec.postprocessing.main.FrameBufferObject;
import de.omnikryptec.postprocessing.main.PostProcessingStageShaded;
import de.omnikryptec.shader.base.Shader;
import de.omnikryptec.shader.files.postprocessing.ColorSpaceShader;
import de.omnikryptec.util.EnumCollection.DepthbufferType;

public class ColorSpaceStage extends PostProcessingStageShaded {

	private static ColorSpaceShader shader = new ColorSpaceShader();
	private Vector3f level = new Vector3f();

	public ColorSpaceStage() {
		this(256);
	}

	public ColorSpaceStage(float rgb) {
		this(rgb, rgb, rgb);
	}

	public ColorSpaceStage(float r, float g, float b) {
		super(shader);
		this.level.x = r;
		this.level.y = g;
		this.level.z = b;
	}

	private int ind = -1;

	public ColorSpaceStage setListIndex(int i) {
		ind = i;
		return this;
	}

	@Override
	public void bindTexture(FrameBufferObject before, List<FrameBufferObject> beforelist, Shader using, int stage) {
		ColorSpaceShader.value.loadVec3(level);
		if (ind < 0) {
			before.bindToUnitOptimized(0);
		} else {
			beforelist.get(ind).bindToUnitOptimized(0);
		}
	}

	@Override
	public FrameBufferObject createFbo() {
		return new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE);
	}

	public ColorSpaceStage setLevels(float rgb) {
		return setLevels(rgb, rgb, rgb);
	}

	public ColorSpaceStage setLevels(float r, float g, float b) {
		level.x = r;
		level.y = g;
		level.z = b;
		return this;
	}

	public ColorSpaceStage setLevels(Vector3f l) {
		this.level = l;
		return this;
	}

	public Vector3f getLevels() {
		return level;
	}

}
