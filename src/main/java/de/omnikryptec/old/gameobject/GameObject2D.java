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

package de.omnikryptec.old.gameobject;

import de.omnikryptec.old.renderer.d2.RenderChunk2D;
import de.omnikryptec.old.util.EnumCollection.UpdateType;
import de.omnikryptec.old.util.Instance;
import de.omnikryptec.old.util.logger.LogLevel;
import de.omnikryptec.old.util.logger.Logger;

import java.util.ArrayList;

public class GameObject2D extends GameObject implements Transformable2D {

	private RenderChunk2D renderChunk;
	private GameObject2D parent;
	private Transform2D transform = new Transform2D();
	private ArrayList<GameObject2D> childs = null;

	public GameObject2D() {
		this(null, null);
	}

	public GameObject2D(String name) {
		this(name, null);
	}

	public GameObject2D(GameObject2D parent) {
		this(null, parent);
	}

	public GameObject2D(String name, GameObject2D parent) {
		if (name == null) {
			name = "";
		}
		addToParent(parent);
	}

	public final GameObject2D setRenderChunk2D(RenderChunk2D myChunk) {
		this.renderChunk = myChunk;
		return this;
	}

	/**
	 * the {@link RenderChunk2D} this GameObject is in.
	 *
	 * @return
	 */
	public final RenderChunk2D getRenderChunk() {
		return renderChunk;
	}

	/**
	 * the parent or null if this GameObject has no parent.
	 *
	 * @return the parent
	 */
	public final GameObject2D getParent() {
		return parent;
	}

	public final GameObject2D addChild(GameObject2D g) {
		if (g != null) {
			if (childs == null) {
				childs = new ArrayList<>();
			}
			childs.add(g);
			g.setParent(this);
		}
		return this;
	}

	public final GameObject2D removeChild(GameObject2D g) {
		if (g != null && childs != null) {
			childs.remove(g);
			g.setParent(null);
			if (childs.isEmpty()) {
				childs = null;
			}
		}
		return this;
	}

	public final GameObject2D removeFromParent() {
		if (parent != null) {
			parent.removeChild(this);
		}
		return this;
	}

	public final GameObject2D addToParent(GameObject2D parent) {
		if (parent != null) {
			parent.addChild(this);
		}
		return this;
	}

	/**
	 * sets the parent for this gameobject or null for no parent
	 *
	 * @param go
	 *            the parent
	 */
	private final GameObject2D setParent(GameObject2D go) {
		this.parent = go;
		this.transform.setParent(go == null ? null : go.getTransform());
		return this;
	}

	public final boolean hasParent() {
		return parent != null;
	}

	public final boolean hasChilds() {
		return childs != null && !childs.isEmpty();
	}

	public ArrayList<GameObject2D> getChilds() {
		return childs;
	}

	@Override
	public Transform2D getTransform() {
		return transform;
	}

	@Override
	public void checkChunkPos(boolean force) {
		if((force || getUpdateType() == UpdateType.DYNAMIC) && Instance.getGameSettings().usesRenderChunking()){
			RenderChunk2D oldchunk = getRenderChunk();
			if (oldchunk != null && (oldchunk.isglobal != isGlobal()
					|| (!oldchunk.isglobal && (oldchunk.getChunkX() != getTransform().getChunkX()
							|| oldchunk.getChunkY() != getTransform().getChunkY())))) {
				oldchunk.getScene().realign(this);
			} else if (Logger.isDebugMode() && oldchunk == null) {
				Logger.log("RenderChunk2D is null: " + toString(), LogLevel.WARNING);
			}
		}
	}



}
