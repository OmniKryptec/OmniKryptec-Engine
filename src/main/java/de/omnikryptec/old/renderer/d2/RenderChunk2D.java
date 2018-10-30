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

package de.omnikryptec.old.renderer.d2;

import java.util.ArrayList;

import de.omnikryptec.old.gameobject.GameObject2D;
import de.omnikryptec.old.gameobject.Light2D;
import de.omnikryptec.old.gameobject.Sprite;
import de.omnikryptec.old.main.AbstractScene2D;
import de.omnikryptec.old.main.GameObjectContainer;
import de.omnikryptec.old.main.OmniKryptecEngine;
import de.omnikryptec.old.settings.GameSettings;

public class RenderChunk2D implements GameObjectContainer<GameObject2D> {

    private static int WIDTH = OmniKryptecEngine.instance().getDisplayManager().getSettings()
	    .getInteger(GameSettings.CHUNK_WIDTH_2D);
    private static int HEIGHT = OmniKryptecEngine.instance().getDisplayManager().getSettings()
	    .getInteger(GameSettings.CHUNK_HEIGHT_2D);

    public static int getWidth() {
	return WIDTH;
    }

    public static int getHeight() {
	return HEIGHT;
    }

    private AbstractScene2D scene;
    private long x, y;
    public final boolean isglobal;
    private final ArrayList<Sprite> chunkSprites = new ArrayList<>();
    private final ArrayList<GameObject2D> chunkOther = new ArrayList<>();
    private final ArrayList<Light2D> chunkLights = new ArrayList<>();

    // currently unused
    // private RenderChunk2D() {
    // this(0, 0, null);
    // }

    public RenderChunk2D(long x, long y, AbstractScene2D scene) {
	this(x, y, scene, false);
    }

    public RenderChunk2D(long x, long y, AbstractScene2D scene, boolean global) {
	this.x = x;
	this.y = y;
	this.scene = scene;
	this.isglobal = global;
    }

    @Override
    public void addGameObject(GameObject2D go, boolean added) {
	if (go != null) {
	    if (go instanceof Sprite) {
		if (go instanceof Light2D) {
		    chunkLights.add((Light2D) go);
		} else {
		    chunkSprites.add((Sprite) go);
		}
	    } else {
		chunkOther.add(go);
	    }
	    go.setRenderChunk2D(this);
	    if (added) {
		go.addedOperation();
	    }
	}
    }

    @Override
    public GameObject2D removeGameObject(GameObject2D go, boolean delete) {
	if (go != null) {
	    if (go instanceof Sprite) {
		if (go instanceof Light2D) {
		    chunkLights.remove(go);
		} else {
		    chunkSprites.remove(go);
		}
	    } else {
		chunkOther.remove(go);
	    }
	    if (delete) {
		go.deleteOperation();
	    }
	    go.setRenderChunk2D(null);
	}
	return go;
    }

    public AbstractScene2D getScene() {
	return scene;
    }

    public long getChunkX() {
	return x;
    }

    public long getChunkY() {
	return y;
    }

    public void logic() {
	for (int i = 0; i < chunkOther.size(); i++) {
	    chunkOther.get(i).doLogic();
	}
	for (int i = 0; i < chunkLights.size(); i++) {
	    chunkLights.get(i).doLogic();
	}
	for (int i = 0; i < chunkSprites.size(); i++) {
	    chunkSprites.get(i).doLogic();
	}
	for (int i = 0; i < chunkOther.size() && !chunkOther.isEmpty(); i++) {
	    chunkOther.get(i).checkChunkPos(false);
	}
	for (int i = 0; i < chunkLights.size() && !chunkLights.isEmpty(); i++) {
	    chunkLights.get(i).checkChunkPos(false);
	}
	for (int i = 0; i < chunkSprites.size() && !chunkSprites.isEmpty(); i++) {
	    chunkSprites.get(i).checkChunkPos(false);
	}
    }

    public ArrayList<Light2D> __getLights() {
	return chunkLights;
    }

    public ArrayList<Sprite> __getSprites() {
	return chunkSprites;
    }

    @Override
    public int size() {
	return chunkLights.size() + chunkOther.size() + chunkSprites.size();
    }
}
