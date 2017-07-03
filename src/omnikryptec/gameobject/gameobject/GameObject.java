package omnikryptec.gameobject.gameobject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.joml.Vector3f;

import omnikryptec.gameobject.component.Component;
import omnikryptec.renderer.RenderChunk;
import omnikryptec.test.saving.DataMap;
import omnikryptec.test.saving.DataMapSerializable;
import omnikryptec.util.SerializationUtil;
import omnikryptec.util.logger.Logger;
import omnikryptec.util.logger.LogLevel;

/**
 *
 * @author pcfreak9000 &amp; Panzer1119
 *
 */
public class GameObject implements DataMapSerializable, Positionable {

    public static enum UpdateType {
        DYNAMIC, STATIC;
    }

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
    private boolean active = true;
    private Vector3f rotation = new Vector3f();
    // @JsonView(GameObject.class) //To hide this while saving it
    private RenderChunk myChunk;
    private List<Component> componentsPreLogic = null;
    private List<Component> componentsPostLogic = null;

    private final Instant ULTIMATE_IDENTIFIER = Instant.now();
    private UpdateType uptype = UpdateType.DYNAMIC;

    public GameObject setUpdateType(UpdateType t) {
        uptype = t;
        return this;
    }

    public UpdateType getUpdateType() {
        return uptype;
    }

    public GameObject() {
        this("");
    }

    /**
     * creates a gameobject with no parent
     */
    public GameObject(String name) {
        this(name, null);
    }

    /**
     * creates a gameobject with a parent. e. g. used to bind a gun to the
     * player
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

    public final GameObject increaseRelativePos(float x, float y, float z) {
        pos.x += x;
        pos.y += y;
        pos.z += z;
        return this;
    }

    public final GameObject increaseRelativeRot(float x, float y, float z) {
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
        if (parent == null) {
            return pos;
        }
        return parent.getAbsolutePos().add(pos, new Vector3f());
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
     * @param go the parent
     */
    public final GameObject setParent(GameObject go) {
        this.parent = go;
        return this;
    }

    public final GameObject doLogic0() {
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
        if (getUpdateType() == UpdateType.DYNAMIC && !(this instanceof Camera)) {
            checkChunkPos();
        }
        return this;
    }

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
     * override this to let your gameobject do its logic then its in sight of
     * the cam
     */
    protected void update() {
    }

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

    protected final GameObject checkChunkPos() {
        RenderChunk oldchunk = getMyChunk();
        if (oldchunk != null) {
            if (oldchunk.getChunkX() != getChunkX() || oldchunk.getChunkY() != getChunkY()
                    || oldchunk.getChunkZ() != getChunkZ()) {
                oldchunk.getScene().addGameObject(this);
                oldchunk.removeGameObject(this, false);
            }
        } else if (Logger.isDebugMode()) {
            Logger.log("MyChunk is null (Should not happen -.-)", LogLevel.WARNING);
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

    public final long getChunkZ() {
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
        if (parent == null) {
            return rotation;
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
        if (toCopy == null) {
            return this;
        }
        name = toCopy.name;
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

    public GameObject setGlobal(boolean b) {
        this.isglobal = b;
        return this;
    }

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
        data.put("active", active);
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
        setActive(data.getBoolean("isActive"));
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
