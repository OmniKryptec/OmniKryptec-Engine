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

package de.omnikryptec.old.postprocessing.stages;

import java.util.List;

import org.joml.Vector4f;

import de.omnikryptec.graphics.display.Display;
import de.omnikryptec.old.gameobject.Camera;
import de.omnikryptec.old.main.OmniKryptecEngine;
import de.omnikryptec.old.postprocessing.main.FrameBufferObject;
import de.omnikryptec.old.postprocessing.main.PostProcessingStageShaded;
import de.omnikryptec.old.shader.base.Shader;
import de.omnikryptec.old.shader.files.postprocessing.FogShader;
import de.omnikryptec.old.util.EnumCollection.DepthbufferType;

public class FogStage extends PostProcessingStageShaded {

	private static FogShader shader = new FogShader();

	private Vector4f fog = new Vector4f(0.3f, 0.3f, 0.3f, 1);

	private float density = 0.007f;
	private float gradient = 1.5f;

	public FogStage() {
		super(shader);
	}

	public FogStage setFog(Vector4f v) {
		return setFog(v.x, v.y, v.z, v.w);
	}

	public FogStage setFog(float r, float g, float b, float a) {
		fog.set(r, g, b, a);
		return this;
	}

	public FogStage setDensity(float d) {
		density = d;
		return this;
	}

	public FogStage setGradient(float g) {
		gradient = g;
		return this;
	}

	private int[] l_ind = { -1, -1 };

	public FogStage setListIndices(int diff, int depth) {
		l_ind[0] = diff;
		l_ind[1] = depth;
		return this;
	}

	private Camera curcam;

	@Override
	public void bindTexture(FrameBufferObject before, List<FrameBufferObject> beforelist, Shader using, int stage) {
		shader.pixsize.loadVec2(1.0f / Display.getWidth(), 1.0f / Display.getHeight());
		shader.fog.loadVec4(fog);
		shader.density.loadFloat(density);
		shader.gradient.loadFloat(gradient);
		curcam = OmniKryptecEngine.instance().getCurrent3DScene().getCamera();
		shader.invprojv.loadMatrix(curcam.getInverseProjView());
		shader.campos.loadVec3(curcam.getTransform().getPosition(true));
		(l_ind[0] < 0 ? before : beforelist.get(l_ind[0])).bindToUnitOptimized(0);
		(l_ind[1] < 0 ? before : beforelist.get(l_ind[1])).getDepthTexture().bindToUnitOptimized(1);
	}

	@Override
	public FrameBufferObject createFbo() {
		return new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE);
	}

}
