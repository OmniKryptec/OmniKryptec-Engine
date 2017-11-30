package omnikryptec.gameobject;

import omnikryptec.renderer.d3.RenderChunk2D;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

public class GameObject2D extends GameObject implements Transformable2D {

	private RenderChunk2D renderChunk;
	private GameObject2D parent;
	private Transform2D transform = new Transform2D();

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
    	if(name==null) {
    		name="";
    	}
    	setParent(parent);
    }
	
	@Override
	protected void checkChunkPos() {
		RenderChunk2D oldchunk = getRenderChunk();
		if (oldchunk != null) {
			if (oldchunk.getChunkX() != getTransform().getChunkX() || oldchunk.getChunkY() != getTransform().getChunkY()
					|| oldchunk.isglobal) {
				oldchunk.getScene().addGameObject(this);
				oldchunk.removeGameObject(this, false);
			}
		} else if (Logger.isDebugMode() && !isGlobal()) {
			Logger.log("RenderChunk2D is null: " + toString(), LogLevel.WARNING);
		}
	}

	public final GameObject2D setRenderChunk3D(RenderChunk2D myChunk) {
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

	/**
	 * sets the parent for this gameobject or null for no parent
	 *
	 * @param go
	 *            the parent
	 */
	public final GameObject2D setParent(GameObject2D go) {
		this.parent = go;
		this.transform.setParent(go == null ? null : go.getTransform());
		return this;
	}

	public final boolean hasParent() {
		return parent!=null;
	}
	
	@Override
	public Transform2D getTransform() {
		return transform;
	}

}
