package omnikryptec.storing;

import org.lwjgl.util.vector.Vector3f;

import omnikryptec.renderer.RenderChunk;

/**
 * 
 * @author pcfreak9000
 *
 */
public class GameObject {



	private Vector3f pos;
	private GameObject parent;
	private boolean active = true;
	private Vector3f rotation;
	private RenderChunk mychunk;

	/**
	 * creates a gameobject with no parent
	 */
	public GameObject() {
		this(null);
	}

	/**
	 * creates a gameobject with a parent. e. g. used to bind a gun to the
	 * player
	 * 
	 * @param parent
	 *            the parent or null for no parent
	 */
	public GameObject(GameObject parent) {
		this.parent = parent;
	}

	/**
	 * if no parent is set this is the absolute position else its the postion
	 * relative to the parent
	 * 
	 * @param pos
	 */
	public void setRelativePos(float x, float y, float z) {
		this.pos.x = x;
		this.pos.y = y;
		this.pos.z = z;
	}




	/**
	 * the relative xpostion if this gameobject has a parent else the absolute
	 * xposition
	 * 
	 * @return the relative position
	 */
	public Vector3f getRelativePos() {
		return pos;
	}


	/**
	 * the absolute xposition is always absolute
	 * 
	 * @return the absolute xposition
	 */
	public Vector3f getAbsolutePos() {
		if (parent == null) {
			return pos;
		}
		return Vector3f.add(parent.getAbsolutePos(), pos, null);
	}


	/**
	 * the parent or null for no parent of this gameobject
	 * 
	 * @return the parent
	 */
	public GameObject getParent() {
		return parent;
	}

	/**
	 * sets the parent for this gameobject
	 * 
	 * @param go
	 *            the parent
	 */
	public void setParent(GameObject go) {
		this.parent = go;
	}

	/**
	 * override this to let your gameobject do its logic then its in sight of
	 * the cam
	 */
	public void doLogic() {
	}




	/**
	 * 
	 * @return true if a parent is set
	 */
	public boolean hasParent() {
		return parent != null;
	}

	/**
	 * the chunkx. used for rendering
	 * 
	 * @return chunkx
	 */
	public long getChunkX() {
		return (long) Math.floor(getAbsolutePos().x / RenderChunk.getWidth());
	}

	/**
	 * the chunky. used for rendering
	 * 
	 * @return chunky
	 */
	public long getChunkY() {
		return (long) Math.floor(getAbsolutePos().y / RenderChunk.getHeight());
	}
	
	public long getChunkZ(){
		return (long) Math.floor(getAbsolutePos().z / RenderChunk.getDepth());
	}
	
	/**
	 * if true the gameobject is active and will be processed.
	 * 
	 * @param b
	 */
	public void setActive(boolean b) {
		this.active = b;
	}

	/**
	 * is this gameobject active and should be processed?
	 * 
	 * @return
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * sets the rotation of this gameobject in radians
	 * 
	 * @param f
	 */
	public void setRotation(Vector3f vec) {
		this.rotation = vec;
	}

	/**
	 * the relative rotation in radians of this object or if this gameobject has
	 * no parent, the absolute rotation
	 * 
	 * @return
	 */
	public Vector3f getRelativeRotation() {
		return rotation;
	}

	/**
	 * the absolute rotation of this GameObject in radians
	 * 
	 * @return
	 */
	public Vector3f getAbsoluteRotation() {
		if (parent == null) {
			return rotation;
		}
		return Vector3f.add(parent.getAbsoluteRotation(), rotation, null);
	}

	/**
	 * wont copy physics!
	 * 
	 * @param toCopy
	 * @return
	 */
	public static GameObject copy(GameObject toCopy) {
		GameObject go = new GameObject();
		go.active = toCopy.active;
		go.parent = toCopy.parent;
		go.rotation = new Vector3f(toCopy.rotation);
		go.pos = new Vector3f(toCopy.pos);
		return go;
	}

	/**
	 * wont copy physics!
	 * 
	 * @param toCopy
	 */
	public void setValuesFrom(GameObject toCopy) {
		active = toCopy.active;
		parent = toCopy.parent;
		rotation = new Vector3f(toCopy.rotation);
		pos = new Vector3f(toCopy.pos);
	}

	
	public void setMyChunk(RenderChunk c){
		this.mychunk = c;
	}
	
	public RenderChunk getMyChunk(){
		return mychunk;
	}

}

