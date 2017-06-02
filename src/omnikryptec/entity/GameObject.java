package omnikryptec.entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import omnikryptec.component.Component;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.renderer.RenderChunk;

/**
 * 
 * @author pcfreak9000 &amp; Panzer1119
 *
 */
public class GameObject {
	
    private static class Sorter implements Comparator<Component> {

            @Override
            public int compare(Component o1, Component o2) {
                return ((o1.getLevel() < o2.getLevel()) ? -1 : (o1.getLevel() > o2.getLevel() ? 1 : 0));
            }

    }
    private static final Sorter SORTER = new Sorter();
	
    private Vector3f pos = new Vector3f();
    private GameObject parent = null;
    private boolean active = true;
    private Vector3f rotation = new Vector3f();
    //@JsonView(GameObject.class) //To hide this while saving it
    private RenderChunk myChunk;
    private List<Component> componentsPreLogic = null;
    private List<Component> componentsPostLogic = null;
    
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
     * @param x
     * @param y
     * @param z
     */
    public final GameObject setRelativePos(float x, float y, float z) {
        this.pos.x = x;
        this.pos.y = y;
        this.pos.z = z;
        return this;
    }

    /**
     * the relative xpostion if this gameobject has a parent else the absolute
     * xposition
     * 
     * @return the relative position
     */
    public final Vector3f getRelativePos() {
        return pos;
    }
    
    public final GameObject increaseRelativePos(float x, float y, float z){
    	pos.x += x;
    	pos.y += y;
    	pos.z += z;
        return this;
    }

    public final GameObject increaseRelativeRot(float x, float y, float z){
    	rotation.x += x;
    	rotation.y += y;
    	rotation.z += z;
        return this;
    }

    /**
     * the absolute xposition is always absolute
     * 
     * @return the absolute xposition
     */
    public final Vector3f getAbsolutePos() {
        if(parent == null) {
            return pos;
        }
        return Vector3f.add(parent.getAbsolutePos(), pos, null);
    }
    
    /**
     * the parent or null for no parent of this gameobject
     * 
     * @return the parent
     */
    public final GameObject getParent() {
        return parent;
    }

    /**
     * sets the parent for this gameobject
     * 
     * @param go
     *            the parent
     */
    public final GameObject setParent(GameObject go) {
        this.parent = go;
        return this;
    }
    
    public final GameObject doLogic0() {
    	if(componentsPreLogic != null){
            for(Component c : componentsPreLogic){
                c.execute(this);
            }
    	}
    	doLogic();
    	if(componentsPostLogic != null){
            for(Component c : componentsPostLogic){
                c.execute(this);
            }
    	}
    	checkChunkPos();
        return this;
    }
    
    public final GameObject addComponent(Component c) {
    	if(c.getLevel() < 0) {
            if(componentsPreLogic == null) {
                componentsPreLogic = new ArrayList<>();
            }
            componentsPreLogic.add(c);
            componentsPreLogic.sort(SORTER);
    	} else {
            if(componentsPostLogic == null) {
                componentsPostLogic = new ArrayList<>();
            }
            componentsPostLogic.add(c);
            componentsPostLogic.sort(SORTER);
    	}
    	return this;
    }
    
    public final GameObject removeComponent(Component c) {
    	if(c.getLevel() < 0){
            if(componentsPreLogic != null){
                componentsPreLogic.remove(c);
                if(componentsPreLogic.isEmpty()) {
                    componentsPreLogic = null;
                }
            }
    	} else {
            if(componentsPostLogic != null){
                componentsPostLogic.remove(c);
                if(componentsPostLogic.isEmpty()) {
                    componentsPostLogic = null;
                }
            }
    	}
    	return this;
    }
    
    private List<Component> tmp;
    public final Component[] getComponents() {
    	if(componentsPreLogic == null && componentsPostLogic == null) {
            return new Component[] {};
    	}
    	if(tmp == null){
            tmp = new ArrayList<>();
    	}
    	if(componentsPreLogic != null){
            tmp.addAll(componentsPreLogic);
    	}
    	if(componentsPostLogic != null){
            tmp.addAll(componentsPostLogic);
    	}
    	return tmp.toArray(new Component[1]);
    }
    
    @SuppressWarnings("unchecked")
    public final <T> T getComponent(Class<T> type) {
        if(componentsPreLogic != null){
            for(Component c : componentsPreLogic) {
                if(c.getClass() == type) {
                    return (T) c;
                }
            }
        }
        if(componentsPostLogic != null){
            for(Component c : componentsPostLogic) {
                if(c.getClass() == type) {
                    return (T) c;
                }
            }
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public final <T> ArrayList<T> getComponents(Class<T> type) {
    	final ArrayList<T> cp = new ArrayList<>();
    	if(componentsPreLogic != null){
            for(Component c : componentsPreLogic) {
                if(c.getClass() == type) {
                    cp.add((T) c);
                }
            }
    	}
    	if(componentsPostLogic != null){
            for(Component c : componentsPostLogic) {
                if(c.getClass() == type) {
                    cp.add((T) c);
                }
            }
    	}
        return cp;
    }
    
    /**
     * override this to let your gameobject do its logic then its in sight of
     * the cam
     */
    protected void doLogic(){	
    }
    
    public final GameObject deleteOperation() {
    	if(componentsPreLogic != null){
            for(Component c : componentsPreLogic){
                c.onDelete(this);
            }
    	}
    	if(componentsPostLogic != null){
            for(Component c : componentsPostLogic){
                c.onDelete(this);
            }
    	}
    	delete();
        return this;
    }
    
    /**
     * override this to let your gameobject do its things when deleted
     * the cam
     */
    protected void delete() {
    }
    
    protected final GameObject checkChunkPos() {
    	RenderChunk oldchunk = getMyChunk();
    	if(oldchunk != null) {
            if(oldchunk.getChunkX() != getChunkX() || oldchunk.getChunkY() != getChunkY() || oldchunk.getChunkZ() != getChunkZ()) {
                oldchunk.getScene().addGameObject(this);
                oldchunk.removeGameObject(this, false);
            }
    	} else {
            if(Logger.isDebugMode()) {
                Logger.log("MyChunk is null (Should not happen -.-)", LogLevel.WARNING);
            }
    	}
        return this;
    }
    
    /**
     * 
     * @return true if a parent is set
     */
    public final boolean hasParent() {
        return parent != null;
    }

    /**
     * the chunkx. used for rendering
     * 
     * @return chunkx
     */
    public final long getChunkX() {
        return (long) Math.floor(getAbsolutePos().x / RenderChunk.getWidth());
    }

    /**
     * the chunky. used for rendering
     * 
     * @return chunky
     */
    public final long getChunkY() {
        return (long) Math.floor(getAbsolutePos().y / RenderChunk.getHeight());
    }

    public final long getChunkZ(){
        return (long) Math.floor(getAbsolutePos().z / RenderChunk.getDepth());
    }

    /**
     * if true the gameobject is active and will be processed.
     * 
     * @param b
     */
    public final GameObject setActive(boolean b) {
        this.active = b;
        return this;
    }

    /**
     * is this gameobject active and should be processed?
     * 
     * @return
     */
    public final boolean isActive() {
        return active;
    }

    /**
     * sets the rotation of this gameobject in radians
     * 
     * @param vec
     */
    public final GameObject setRotation(Vector3f vec) {
        this.rotation = vec;
        return this;
    }

    /**
     * the relative rotation in radians of this object or if this gameobject has
     * no parent, the absolute rotation
     * 
     * @return
     */
    public final Vector3f getRelativeRotation() {
        return rotation;
    }

    /**
     * the absolute rotation of this GameObject in radians
     * 
     * @return
     */
    public final Vector3f getAbsoluteRotation() {
        if(parent == null) {
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
    public static final GameObject copy(GameObject toCopy) {
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
    public final GameObject setValuesFrom(GameObject toCopy) {
        active = toCopy.active;
        parent = toCopy.parent;
        rotation = new Vector3f(toCopy.rotation);
        pos = new Vector3f(toCopy.pos);
        return this;
    }

    public final GameObject setMyChunk(RenderChunk myChunk) {
    	this.myChunk = myChunk;
        return this;
    }

    public final RenderChunk getMyChunk() {
    	return myChunk;
    }

    public final Vector3f getPos() {
        return pos;
    }

    public final Vector3f getRotation() {
        return rotation;
    }

    public final GameObject setPos(Vector3f pos) {
        this.pos = pos;
        return this;
    }
    
    @Override
    public String toString(){
    	return "GameObject [ Pos: "+pos.toString()+" Rot: "+rotation.toString()+" ]";
    }
    
}

