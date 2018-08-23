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

package de.omnikryptec.main;

import de.omnikryptec.gameobject.Camera;
import de.omnikryptec.gameobject.GameObject2D;
import de.omnikryptec.renderer.d2.DefaultRenderer2D;
import de.omnikryptec.renderer.d2.RenderChunk2D;
import de.omnikryptec.renderer.d2.Renderer2D;
import de.omnikryptec.util.Instance;
import de.omnikryptec.util.logger.Logger;

import java.util.HashMap;

public class Scene2D extends AbstractScene2D {

	private HashMap<ChunkCoord2D, RenderChunk2D> scene = new HashMap<>();
	private RenderChunk2D global = new RenderChunk2D(0, 0, this, true);
	private Renderer2D renderer;

	public Scene2D() {
		this("", null);
	}

	public Scene2D(String name) {
		this(name, null);
	}

	public Scene2D(String name, Camera cam) {
		super(name, cam);
		setRenderer(new DefaultRenderer2D());
		init();
	}

	public Scene2D(String name, Camera cam, Renderer2D renderer) {
		this(name, cam);
		setRenderer(renderer);
		init();
	}

	private void init() {
		setAmbientColor(1, 1, 1);
	}

	public void setRenderer(Renderer2D renderer) {
		this.renderer = renderer;
	}

	@Override
	protected void logic() {
		if (Instance.getGameSettings().usesRenderChunking()) {
			long cx = getCamera().getTransform().getChunkX2D();
			long cy = getCamera().getTransform().getChunkY2D();
			RenderChunk2D tmpc;
			for (long x = -cox; x <= cox; x++) {
				for (long y = -coy; y <= coy; y++) {
					if ((tmpc = scene.get(/* xyToString(x + cx, y + cy) */new ChunkCoord2D(x + cx, y + cy))) != null) {
						tmpc.logic();
					}
				}
			}
		}
		global.logic();
		update();
	}

	protected void update() {
	}

	@Override
	protected long render() {
		if (getCamera() == null) {
			return 0;
		}
		return renderer.render(this, global, getCamera().getTransform().getChunkX2D(),
				getCamera().getTransform().getChunkY2D(), cox, coy, scene);
	}

	@Override
	public final void addGameObject_(GameObject2D go, boolean added) {
		if (go != null) {
			if (go.isGlobal() || !Instance.getGameSettings().usesRenderChunking()) {
				global.addGameObject(go, added);
			} else {
				ChunkCoord2D tmp = new ChunkCoord2D(go.getTransform().getChunkX(), go.getTransform().getChunkY());
				// tmp = xyToString(go.getTransform().getChunkX(),
				// go.getTransform().getChunkY());
				if (!scene.containsKey(tmp)) {
					scene.put(tmp,
							new RenderChunk2D(go.getTransform().getChunkX(), go.getTransform().getChunkY(), this));
				}
				scene.get(tmp).addGameObject(go, added);
			}
		}
	}

	@Override
	public final GameObject2D removeGameObject_(GameObject2D go, boolean delete) {
		if (go != null) {
			if (go.getRenderChunk() != null) {
				if (!Instance.getGameSettings().usesRenderChunking()) {
					global.removeGameObject(go, delete);
				} else {
					RenderChunk2D tmpc = go.getRenderChunk();
					go.getRenderChunk().removeGameObject(go, delete);
					if (tmpc.isEmpty() && !tmpc.isglobal) {
						scene.remove(new ChunkCoord2D(tmpc.getChunkX(), tmpc.getChunkY()));
						// scene.remove(xyToString(tmpc.getChunkX(), tmpc.getChunkY()));
					}
				}
			} else {
				global.removeGameObject(go, delete);
				ChunkCoord2D tmp = new ChunkCoord2D(go.getTransform().getChunkX(), go.getTransform().getChunkY());
				// tmp = xyToString(go.getTransform().getChunkX(),
				// go.getTransform().getChunkY());
				scene.get(tmp).removeGameObject(go, delete);
				if (scene.get(tmp).isEmpty()) {
					scene.remove(tmp);
				}
				if (Logger.isDebugMode()) {
					System.err.println("RenderChunk2D is null: " + go);
				}
			}
		}
		return go;
	}

//	private static final String DELIMITER = ":";
//
//	public static String xyToString(long x, long y) {
//		return x + DELIMITER + y;
//	}

	@Override
	public int size() {
		// +1 weil global immer da ist
		return scene.size() + 1;
	}
}
