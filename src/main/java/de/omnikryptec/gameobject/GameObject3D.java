package de.omnikryptec.gameobject;

import de.omnikryptec.renderer.d3.RenderChunk3D;
import de.omnikryptec.util.EnumCollection.UpdateType;
import de.omnikryptec.util.Instance;
import de.omnikryptec.util.logger.LogLevel;
import de.omnikryptec.util.logger.Logger;
import org.joml.Matrix4f;

import java.util.ArrayList;

public class GameObject3D extends GameObject implements Transformable3D {

	private RenderChunk3D renderChunk;
	private Transform3D transform = new Transform3D();
	private GameObject3D parent = null;
	private ArrayList<GameObject3D> childs = null;

	public GameObject3D() {
		this(null, null);
	}

	public GameObject3D(String name) {
		this(name, null);
	}

	public GameObject3D(GameObject3D parent) {
		this(null, parent);
	}

	public GameObject3D(String name, GameObject3D parent) {
		if (name == null) {
			name = "";
		}
		addToParent(parent);
	}

	@Override
	public Transform3D getTransform() {
		return transform;
	}

	public Matrix4f getTransformation() {
		return transform.getTransformation(getUpdateType());
	}

	public GameObject setTransform(Transform3D t) {
		this.transform = t;
		return this;
	}

	/**
	 * the parent or null if this GameObject has no parent.
	 *
	 * @return the parent
	 */
	public final GameObject3D getParent() {
		return parent;
	}

	public final GameObject3D addChild(GameObject3D g) {
		if (g != null) {
			if (childs == null) {
				childs = new ArrayList<>();
			}
			childs.add(g);
			g.setParent(this);
		}
		return this;
	}

	public final GameObject3D removeChild(GameObject3D g) {
		if (g != null && childs != null) {
			childs.remove(g);
			g.setParent(null);
			if (childs.isEmpty()) {
				childs = null;
			}
		}
		return this;
	}

	public final GameObject3D removeFromParent() {
		if (parent != null) {
			parent.removeChild(this);
		}
		return this;
	}

	public final GameObject3D addToParent(GameObject3D parent) {
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
	private final GameObject3D setParent(GameObject3D go) {
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

	public ArrayList<GameObject3D> getChilds() {
		return childs;
	}

	/**
	 * checks the chunkpos of this GameObject
	 *
	 */
	@Override
	public final void checkChunkPos(boolean force) {
		if ((force || (( getUpdateType() == UpdateType.DYNAMIC) && Instance.getGameSettings().usesRenderChunking()))
				&& !(this instanceof Camera)) {
			RenderChunk3D oldchunk = getRenderChunk();
			if (oldchunk != null && (oldchunk.isglobal != isGlobal() || force
					|| (!oldchunk.isglobal && (oldchunk.getChunkX() != getTransform().getChunkX()
							|| oldchunk.getChunkY() != getTransform().getChunkY()
							|| oldchunk.getChunkZ() != getTransform().getChunkZ())))) {
				oldchunk.getScene().realign(this);
			} else if (Logger.isDebugMode() && !isGlobal()) {
				Logger.log("RenderChunk3D is null: " + toString(), LogLevel.WARNING);
			}
		}
	}

	public final GameObject3D setRenderChunk3D(RenderChunk3D myChunk) {
		this.renderChunk = myChunk;
		return this;
	}

	/**
	 * the {@link RenderChunk3D} this GameObject is in.
	 *
	 * @return
	 */
	public final RenderChunk3D getRenderChunk() {
		return renderChunk;
	}

}
