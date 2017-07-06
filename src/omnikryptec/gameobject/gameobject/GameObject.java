package omnikryptec.gameobject.gameobject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.joml.Vector3f;

import omnikryptec.gameobject.component.Component;
import omnikryptec.main.Scene;
import omnikryptec.main.Scene.FrameState;
import omnikryptec.renderer.RenderChunk;
import omnikryptec.settings.GameSettings;
import omnikryptec.test.saving.DataMap;
import omnikryptec.test.saving.DataMapSerializable;
import omnikryptec.util.Instance;
import omnikryptec.util.SerializationUtil;
import omnikryptec.util.logger.Logger;
import omnikryptec.util.logger.LogLevel;

/**
 *
 * @author pcfreak9000 &amp; Panzer1119
 *
 */
public class GameObject implements DataMapSerializable, Positionable {


    private static class Sorter implements Comparator<Component> {

        @Override
        public int compare(Component o1, Component o2) {
            return ((o1.getLevel() < o2.getLevel()) ? -1 : (o1.getLevel() > o2.getLevel() ? 1 : 0));
        }

    }

    private static final Sorter SORTER = new Sorter();
    public static final ArrayList<GameObject> gameObjects = new ArrayList<>();

    private String name;
    private boolean isglobal = false;
    private Vector3f pos = new Vector3f();
    private GameObject parent = null;
    private boolean logicEnabled = true;
    private Vector3f rotation = new Vector3f();
    // @JsonView(GameObject.class) //To hide this while saving it
    private RenderChunk myChunk;
    private List<Component> componentsPreLogic = null;
    private List<Component> componentsPostLogic = null;

    private final Instant ULTIMATE_IDENTIFIER = Instant.now();
    private UpdateType uptype = UpdateType.DYNAMIC;

    /**
     * sets the updatetype of this GameObject. if its {@link UpdateType#SEMISTATIC} the logic will be executed but the chunkposition etc will not be changed.
     * if its {@link UpdateType#STATIC} the logic will never be executed.
     * @param t
     * @return this GameObject
     */
    public GameObject setUpdateType(UpdateType t) {
        uptype = t;
        return this;
    }

    /**
     * the updatetpye of this GameObject
     * @return
     */
    public UpdateType getUpdateType() {
        return uptype;
    }

    /**
     * creats an empty GameObject
     */
    public GameObject() {
        this("");
    }

    /**
     * creates a GameObject with no parent
     */
    public GameObject(String name) {
        this(name, null);
    }

    /**
     * creates a GameObject with a parent. e. g. used to bind a gun to the
     * player to move with him.
     *
     * @param parent the parent or null for no parent
     */
    public GameObject(String name, GameObject parent) {
        this.name = name;
        this.parent = parent;
        if (name != null && !name.isEmpty()) {
            gameObjects.add(this);
        }
    }
    
    /**
     * dont let the gameObjects list overflow
     */
    @Override
    protected void finalize() throws Throwable {
    	gameObjects.remove(this);
    }
    
    /**
     * if no parent is set this is the absolute position else its the postion
     * relative to the parent
     *
     * @param x
     * @param y
     * @param z
     * @return this GameObject
     */
    public final GameObject setRelativePos(float x, float y, float z) {
        this.pos.x = x;
        this.pos.y = y;
        this.pos.z = z;
        return this;
    }

    /**
     * the relative position if this gameobject has a parent else the absolute
     * position
     *
     * @return the relative position
     */
    public final Vector3f getRelativePos() {
        return new Vector3f(pos);
    }

    /**
     * increases the relative position with the given values.
     * @param x
     * @param y
     * @param z
     * @return this GameObject
     */
    public final GameObject increaseRelativePos(float x, float y, float z) {
        pos.x += x;
        pos.y += y;
        pos.z += z;
        return this;
    }

    /**
     * increases the relative rotation with the given values.
     * @param x
     * @param y
     * @param z
     * @return this GameObject
     */
    public final GameObject increaseRelativeRot(float x, float y, float z) {
        rotation.x += x;
        rotation.y += y;
        rotation.z += z;
        return this;
    }

    /**
     * the absolute position is always absolute. if the parent is not <code>null</code>, the relative position of this gameobject + the absolute position of the parent is returned.
     * 
     *
     * @return the absolute position
     */
    @Override
    public final Vector3f getAbsolutePos() {
        if (parent == null) {
            return new Vector3f(pos);
        }
        return parent.getAbsolutePos().add(pos, new Vector3f());
    }

    /**
     * the parent or null if this GameObject has no parent.
     *
     * @return the parent
     */
    public final GameObject getParent() {
        return parent;
    }

    /**
     * sets the parent for this gameobject or null for no parent
     *
     * @param go the parent
     */
    public final GameObject setParent(GameObject go) {
        this.parent = go;
        return this;
    }
    
    /**
     * executes the logic of this GameObject if neccessary. 
     * @see #setUpdateType(UpdateType)
     * @return this GameObject
     */
    public final GameObject doLogic(){
    	return doLogic(false);
    }
    
    private Scene cs;
    /**
     * executes the logic of this GameObject (if neccessary or forced)
     * @param force if true all logic of this GameObject is executed, if neccessary or not (ignores the {@link UpdateType}).
     * @return this GameObject
     */
    public final GameObject doLogic(boolean force) {
        if(getUpdateType()==UpdateType.STATIC&&!force){
        	return this;
        }
    	if(Logger.isDebugMode()&&(cs=Instance.getCurrentScene())!=null&&cs.getState()==FrameState.RENDERING){
        	Logger.log("Logic is not allowed while rendering!", LogLevel.WARNING);
        	return this;
        }
    	if(!logicEnabled){
    		return this;
    	}
    	if (componentsPreLogic != null) {
            for (Component c : componentsPreLogic) {
                c.execute(this);
            }
        }
        update();
        if (componentsPostLogic != null) {
            for (Component c : componentsPostLogic) {
                c.execute(this);
            }
        }
        if ((force || getUpdateType() == UpdateType.DYNAMIC) && !(this instanceof Camera)&&Instance.getGameSettings().usesRenderChunking()) {
            checkChunkPos(true);
        }
        return this;
    }

    /**
     * adds a Component to this GameObject.
     * @see Component
     * @param c
     * @return this GameObject
     */
    public final GameObject addComponent(Component c) {
        if (c.getLevel() < 0) {
            if (componentsPreLogic == null) {
                componentsPreLogic = new ArrayList<>();
            }
            componentsPreLogic.add(c);
            componentsPreLogic.sort(SORTER);
        } else {
            if (componentsPostLogic == null) {
                componentsPostLogic = new ArrayList<>();
            }
            componentsPostLogic.add(c);
            componentsPostLogic.sort(SORTER);
        }
        return this;
    }
    
    /**
     * removes a Component from this GameObject
     * @see {@link Component}
     * @param c
     * @return this GameObject
     */
    public final GameObject removeComponent(Component c) {
        if (c.getLevel() < 0) {
            if (componentsPreLogic != null) {
                componentsPreLogic.remove(c);
                if (componentsPreLogic.isEmpty()) {
                    componentsPreLogic = null;
                }
            }
        } else if (componentsPostLogic != null) {
            componentsPostLogic.remove(c);
            if (componentsPostLogic.isEmpty()) {
                componentsPostLogic = null;
            }
        }
        return this;
    }

    private List<Component> tmp;

    /**
     * all components of this GameObject
     * @return
     */
    public final Component[] getComponents() {
        if (componentsPreLogic == null && componentsPostLogic == null) {
            return new Component[]{};
        }
        if (tmp == null) {
            tmp = new ArrayList<>();
        }
        if (componentsPreLogic != null) {
            tmp.addAll(componentsPreLogic);
        }
        if (componentsPostLogic != null) {
            tmp.addAll(componentsPostLogic);
        }
        return tmp.toArray(new Component[1]);
    }

    /**
     * the first occurence of a Component from the Class <code>type</code> or <code>null</code> if none is found.
     * @param type
     * @return the component
     */
    @SuppressWarnings("unchecked")
    public final <T> T getComponent(Class<T> type) {
        if (componentsPreLogic != null) {
            for (Component c : componentsPreLogic) {
                if (c.getClass() == type) {
                    return (T) c;
                }
            }
        }
        if (componentsPostLogic != null) {
            for (Component c : componentsPostLogic) {
                if (c.getClass() == type) {
                    return (T) c;
                }
            }
        }
        return null;
    }

    /**
     * all occurences of a Component from the Class <code>type</code>
     * @param type
     * @return a ArrayList of Components or an Empty ArrayList
     */
    @SuppressWarnings("unchecked")
    public final <T> ArrayList<T> getComponents(Class<T> type) {
        final ArrayList<T> cp = new ArrayList<>();
        if (componentsPreLogic != null) {
            for (Component c : componentsPreLogic) {
                if (c.getClass() == type) {
                    cp.add((T) c);
                }
            }
        }
        if (componentsPostLogic != null) {
            for (Component c : componentsPostLogic) {
                if (c.getClass() == type) {
                    cp.add((T) c);
                }
            }
        }
        return cp;
    }

    /**
     * override this to do some logic. only gets executed for {@link UpdateType#SEMISTATIC} pr {@link UpdateType#DYNAMIC} or if the logic is forced.
     * @see #doLogic(boolean)
     */
    protected void update() {
    }

    /**
     * called then the GameObject is finally removed from the scene.
     * @see Scene#removeGameObject(GameObject, boolean) 
     * @see Scene#removeGameObject(GameObject)
     * @return
     */
    public final GameObject deleteOperation() {
        if (componentsPreLogic != null) {
            for (Component c : componentsPreLogic) {
                c.onDelete(this);
            }
        }
        if (componentsPostLogic != null) {
            for (Component c : componentsPostLogic) {
                c.onDelete(this);
            }
        }
        delete();
        gameObjects.remove(this);
        return this;
    }

    /**
     * override this to let your gameobject do its things when deleted the cam
     */
    protected void delete() {
    }

   /**
    * checks the chunkpos of this GameObject
    * @param error if true and if the Logger is in debugmode and if the chunk of this gameobject is null a warning will be printed.
    * @return this GameObject
    */
    protected final GameObject checkChunkPos(boolean error) {
        RenderChunk oldchunk = getMyChunk();
        if (oldchunk != null) {
            if (oldchunk.getChunkX() != getChunkX() || oldchunk.getChunkY() != getChunkY()
                    || oldchunk.getChunkZ() != getChunkZ()) {
                oldchunk.getScene().addGameObject(this);
                oldchunk.removeGameObject(this, false);
            }
        } else if (error&&Logger.isDebugMode()) {
            Logger.log("MyChunk is null: "+toString(), LogLevel.WARNING);
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
     * @see GameSettings#usesRenderChunking()
     * @return chunkx
     */
    public final long getChunkX() {
        return (long) Math.floor(getAbsolutePos().x / RenderChunk.getWidth());
    }

    /**
     * the chunky. used for rendering
     * @see GameSettings#usesRenderChunking()
     * @return chunky
     */
    public final long getChunkY() {
        return (long) Math.floor(getAbsolutePos().y / RenderChunk.getHeight());
    }

    /**
     * the chunkz. used for rendering
     * @see GameSettings#usesRenderChunking()
     * @return
     */
    public final long getChunkZ() {
        return (long) Math.floor(getAbsolutePos().z / RenderChunk.getDepth());
    }

    /**
     * if true the gameobject is active and will be processed.
     * if <code>false</code> the logic will never be executed automatically.
     * @param b
     */
    public final GameObject setLogicEnabled(boolean b) {
        this.logicEnabled = b;
        return this;
    }

    /**
     * is this gameobject active and should be processed?
     *
     * @return
     */
    public final boolean isLogicEnabled() {
        return logicEnabled;
    }

    /**
     * sets the rotation of this gameobject in radians around x,y,z axis.
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
     * @see #getAbsolutePos()
     * @return
     */
    public final Vector3f getAbsoluteRotation() {
        if (parent == null) {
            return new Vector3f(rotation);
        }
        return parent.getAbsoluteRotation().add(rotation, new Vector3f());
    }

    /**
     * wont copy physics!
     *
     * @param toCopy
     * @return
     */
    public static GameObject copy(GameObject toCopy) {
        GameObject go = new GameObject(toCopy.name);
        go.logicEnabled = toCopy.logicEnabled;
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
        if (toCopy == null) {
            return this;
        }
        name = toCopy.name;
        logicEnabled = toCopy.logicEnabled;
        parent = toCopy.parent;
        rotation = new Vector3f(toCopy.rotation);
        pos = new Vector3f(toCopy.pos);
        return this;
    }

    public final GameObject setMyChunk(RenderChunk myChunk) {
        this.myChunk = myChunk;
        return this;
    }

    /**
     * the {@link RenderChunk} this GameObject is in.
     * @return
     */
    public final RenderChunk getMyChunk() {
        return myChunk;
    }

    /**
     * @see #getRelativePos()
     * @return rel. pos
     */
    public final Vector3f getPos() {
        return pos;
    }

    /**
     * @see #getRelativeRotation()
     * @return rel. rot
     */
    public final Vector3f getRotation() {
        return rotation;
    }

    /**
     * @see #setRelativePos(float, float, float)
     * @param pos
     * @return this GameObject
     */
    public final GameObject setPos(Vector3f pos) {
        this.pos = pos;
        return this;
    }

    /**
     * if true this GameObject will always be processed regardless of the camera pos.
     * @param b
     * @return
     */
    public GameObject setGlobal(boolean b) {
        this.isglobal = b;
        checkChunkPos(false);
        return this;
    }

    /**
     * is this GameObject global?
     * @return
     */
    public boolean isGlobal() {
        return isglobal;
    }

    @Override
    public String toString() {
        return String.format("%s (%d): [Position: %s, Rotation: %s]", getClass().getSimpleName(), ULTIMATE_IDENTIFIER.toEpochMilli(), pos, rotation);
    }

    public GameObject setName(String name) {
        this.name = name;
        return this;
    }

    @SuppressWarnings("unchecked")
	public static final <T> T byName(Class<? extends T> c, String name, boolean onlySame) {
        for (GameObject gameObject : gameObjects) {
            if ((!onlySame && c.isAssignableFrom(gameObject.getClass()) || (onlySame && gameObject.getClass() == c)) && (gameObject.getName() == null ? name == null : gameObject.getName().equals(name))) {
                return (T) gameObject;
            }
        }
        return null;
    }

    public static final <T> T byName(Class<? extends T> c, String name) {
        return byName(c, name, true);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DataMap toDataMap(DataMap data) {
        data.put("name", name);
        data.put("isglobal", isglobal);
        data.put("active", logicEnabled);
        if (parent != null) {
            data.put("parent", parent.toDataMap(new DataMap("parent")));
        }
        data.put("position", SerializationUtil.vector3fToString(pos));
        data.put("rotation", SerializationUtil.vector3fToString(rotation));
        return data;
    }

    public static GameObject newInstanceFromDataMap(DataMap data) {
        if (data == null) {
            return null;
        }
        String name = data.getString("name");
        if (name == null || name.isEmpty()) {
            return null;
        }
        final GameObject gameObject = byName(GameObject.class, name, false);
        return (gameObject != null ? gameObject : new GameObject()).fromDataMap(data);
    }

    @Override
    public GameObject fromDataMap(DataMap data) {
        if (data == null) {
            return null;
        }
        setName(data.getString("name"));
        setGlobal(data.getBoolean("isglobal"));
        setLogicEnabled(data.getBoolean("isActive"));
        DataMap dataMap_temp = data.getDataMap("parent");
        if (parent == null) {
            Object parent_ = newInstanceFromDataMap(dataMap_temp);
            setParent((parent_ != null ? (GameObject) parent_ : null));
        } else {
            parent.fromDataMap(dataMap_temp); // FIXME Hmmm ist die Frage weil
            // die Parents und id und so
        }
        setPos(SerializationUtil.stringToVector3f(data.getString("position")));
        setRotation(SerializationUtil.stringToVector3f(data.getString("rotation")));
        return this;
    }

}
