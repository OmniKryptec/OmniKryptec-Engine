package omnikryptec.gameobject;

import java.util.ArrayList;

import omnikryptec.renderer.d2.RenderChunk2D;
import omnikryptec.util.EnumCollection.UpdateType;
import omnikryptec.util.Instance;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

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
