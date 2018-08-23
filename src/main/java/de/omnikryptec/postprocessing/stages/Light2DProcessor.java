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

import de.omnikryptec.display.Display;
import de.omnikryptec.gameobject.Camera;
import de.omnikryptec.gameobject.Light2D;
import de.omnikryptec.graphics.GraphicsUtil;
import de.omnikryptec.graphics.SpriteBatch;
import de.omnikryptec.postprocessing.main.FrameBufferObject;
import de.omnikryptec.postprocessing.main.PostProcessingStage;
import de.omnikryptec.renderer.d2.DefaultRenderer2D;
import de.omnikryptec.util.EnumCollection.BlendMode;
import de.omnikryptec.util.EnumCollection.DepthbufferType;
import de.omnikryptec.util.Instance;
import org.lwjgl.opengl.GL30;

import java.util.List;

public class Light2DProcessor extends PostProcessingStage {

	private DefaultRenderer2D myrenderer;
	private SpriteBatch batch;

	public Light2DProcessor(DefaultRenderer2D renderer) {
		assert renderer != null;
		this.myrenderer = renderer;
		batch = new SpriteBatch(new Camera().setDefaultScreenSpaceProjection(), 1);
	}

	@Override
	public void render(FrameBufferObject before, List<FrameBufferObject> beforelist, int stage) {
		if (myrenderer.getPreparedLights() == null) {
			before.resolveToFbo(getFbo(), GL30.GL_COLOR_ATTACHMENT0);
		} else {
			getFbo().bindFrameBuffer();
			GraphicsUtil.clear(Instance.getCurrent2DScene().getAmbientColor());
			GraphicsUtil.blendMode(BlendMode.ADDITIVE);
			myrenderer.getSpriteBatch().begin();
			for (Light2D s : myrenderer.getPreparedLights()) {
				//System.out.println(s.getTexture());
				s.paint(myrenderer.getSpriteBatch());
			}
			myrenderer.getSpriteBatch().end();
			GraphicsUtil.blendMode(BlendMode.MULTIPLICATIVE);
			batch.begin();
			batch.draw(before, -1, -1, 2, 2);
			batch.end();
			GraphicsUtil.blendMode(BlendMode.ALPHA);
			getFbo().unbindFrameBuffer();
		}
	}

	@Override
	protected FrameBufferObject createFbo() {
		return new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.NONE);
	}

}