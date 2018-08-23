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

package de.omnikryptec.renderer.d2;

import de.omnikryptec.event.eventV2.EventSubscription;
import de.omnikryptec.event.eventV2.engineevents.ResizeEvent;
import de.omnikryptec.gameobject.Light2D;
import de.omnikryptec.gameobject.Sprite;
import de.omnikryptec.graphics.GraphicsUtil;
import de.omnikryptec.graphics.SpriteBatch;
import de.omnikryptec.main.AbstractScene2D;
import de.omnikryptec.main.ChunkCoord2D;
import de.omnikryptec.main.OmniKryptecEngine;
import de.omnikryptec.settings.GameSettings;
import de.omnikryptec.util.Color;
import de.omnikryptec.util.EnumCollection.BlendMode;
import de.omnikryptec.util.FrustrumFilter;
import de.omnikryptec.util.Instance;

import java.util.ArrayList;
import java.util.HashMap;

public class DefaultRenderer2D implements Renderer2D {

	private SpriteBatch batch/*, finalBatch*/;

	public DefaultRenderer2D() {
		this(new SpriteBatch());
		Instance.engineBus().registerEventHandler(this);
	}

	public DefaultRenderer2D(SpriteBatch batch) {
		this.batch = batch;
//		this.finalBatch = new SpriteBatch(new Camera().setDefaultScreenSpaceProjection(), 1);
//		lights = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.NONE);
//		fbo = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.NONE);
	}

	private ArrayList<Light2D> lightlist;
	//private FrameBufferObject fbo;
	private boolean light;
	private long last = -1;
	private Color clearcolor2d = new Color(0, 0, 0, 0);
	private FrustrumFilter filter = new FrustrumFilter();

	@Override
	public long render(AbstractScene2D sc, RenderChunk2D global, long camChunkX, long camChunkY, int chunkOffsetX,
			int chunkOffsetY, HashMap<ChunkCoord2D, RenderChunk2D> scene) {
		batch.setCamera(sc.getCamera());
		filter.setCamera(sc.getCamera());
		ArrayList<Sprite> sprites = new ArrayList<>();
		if (GraphicsUtil.needsUpdate(last, GameSettings.CHECKCHANGEFRAMES)) {
			light = OmniKryptecEngine.instance().getGameSettings().getBoolean(GameSettings.LIGHT_2D);
			last = Instance.getFramecount();
		}
		sprites.addAll(global.__getSprites());
		if (light) {
			lightlist = new ArrayList<>();
			lightlist.addAll(global.__getLights());
		} else {
			lightlist = null;
		}
		ChunkCoord2D stringTmp;
		for (long x = -chunkOffsetX; x <= chunkOffsetX; x++) {
			for (long y = -chunkOffsetY; y <= chunkOffsetY; y++) {
				stringTmp = new ChunkCoord2D(camChunkX + x, camChunkY + y);
				//stringTmp = Scene2D.xyToString(camChunkX + x, camChunkY + y);
				if (scene.get(stringTmp) != null) {
					sprites.addAll(scene.get(stringTmp).__getSprites());
					if (light) {
						lightlist.addAll(scene.get(stringTmp).__getLights());
					}
				}
			}
		}
		sprites.sort(getLayerComparator());
		//fbo.bindFrameBuffer();
		GraphicsUtil.clear(clearcolor2d);
		batch.begin();
		GraphicsUtil.blendMode(BlendMode.ALPHA);
		for (Sprite s : sprites) {
			if (filter.intersects(s)) {
				s.paint(batch);
			}
		}
		batch.end();
		//fbo.unbindFrameBuffer();
		// if (light) {
		// lights.bindFrameBuffer();
		// GraphicsUtil.clear(sc.getAmbientColor());
		// GraphicsUtil.blendMode(BlendMode.ADDITIVE);
		// batch.begin();
		// for (Light2D s : lightlist) {
		// s.paint(batch);
		// }
		// batch.end();
		// lights.unbindFrameBuffer();
		// GraphicsUtil.blendMode(BlendMode.MULTIPLICATIVE);
		// finalBatch.begin();
		// finalBatch.draw(lights, -1, -1, 2, 2);
		// finalBatch.end();
		// }
		// GraphicsUtil.blendMode(BlendMode.ALPHA);
		//finalBatch.begin();
		//finalBatch.draw(fbo, -1, -1, 2, 2);
		//finalBatch.end();
		return batch.getVertexCount();
	}

	@EventSubscription
	public void onEvent(ResizeEvent ev) {
		//fbo.delete();
		//lights.delete();
		//lights = new FrameBufferObject(ev.getNewWidth(), ev.getNewHeight(), DepthbufferType.NONE);
		//fbo = new FrameBufferObject(ev.getNewWidth(), ev.getNewHeight(), DepthbufferType.NONE);
	}

	public Color clearColor() {
		return clearcolor2d;
	}

	public SpriteBatch getSpriteBatch() {
		return batch;
	}

	public ArrayList<Light2D> getPreparedLights() {
		return lightlist;
	}
}
