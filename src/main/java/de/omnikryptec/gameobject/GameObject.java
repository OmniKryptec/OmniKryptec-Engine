package de.omnikryptec.gameobject;

import de.omnikryptec.gameobject.component.Component;
import de.omnikryptec.main.OmniKryptecEngine;
import de.omnikryptec.main.Scene3D;
import de.omnikryptec.test.saving.DataMap;
import de.omnikryptec.test.saving.DataMapSerializable;
import de.omnikryptec.util.EnumCollection.UpdateType;
import de.omnikryptec.util.exceptions.OmniKryptecException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author pcfreak9000 &amp; Panzer1119
 *
 */
public abstract class GameObject implements DataMapSerializable {

    private static final Sorter SORTER = new Sorter();
    public static final ArrayList<GameObject> gameObjects = new ArrayList<>();

    private static class Sorter implements Comparator<Component<?>> {

        @Override
        public final int compare(Component<?> o1, Component<?> o2) {
            return ((o1.getPrio() < o2.getPrio()) ? -1 : (o1.getPrio() > o2.getPrio() ? 1 : 0));
        }

    }

    private String name;
    private boolean isglobal = false;
    private boolean logicEnabled = true;
    private List<Component<?>> componentsPreLogic = null;
    private List<Component<?>> componentsPostLogic = null;

    private final Instant ULTIMATE_IDENTIFIER = Instant.now();
    private UpdateType uptype = UpdateType.DYNAMIC;

    /**
     * sets the updatetype of this GameObject. if its
     * {@link UpdateType#SEMISTATIC} the logic will be executed but the
     * chunkposition etc will not be changed. if its {@link UpdateType#STATIC}
     * the logic will never be executed.
     *
     * @param t
     * @return this GameObject
     */
    public GameObject setUpdateType(UpdateType t) {
        uptype = t;
        return this;
    }

    /**
     * the updatetpye of this GameObject
     *
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
     * creates a GameObject
     *
     * @param name the parent or null for no parent
     */
    public GameObject(String name) {
        this.name = name;
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
     * executes the logic of this GameObject if neccessary.
     *
     * @see #setUpdateType(UpdateType)
     * @return this GameObject
     */
    public final GameObject doLogic() {
        return doLogic(false);
    }

    /**
     * executes the logic of this GameObject (if neccessary or forced)
     *
     * @param force if true all logic of this GameObject is executed, if
     * neccessary or not (ignores the {@link UpdateType}).
     * @return this GameObject
     */
    public final GameObject doLogic(boolean force) {
        if (getUpdateType() == UpdateType.STATIC && !force) {
            return this;
        }
        if (!logicEnabled) {
            return this;
        }
//        if (Logger.isDebugMode() && (cs = Instance.getCurrent3DScene()) != null && cs.getState() == FrameState.RENDERING) {
//            Logger.log("Logic is not allowed while rendering!", LogLevel.WARNING);
//            return this;
//        }
        if (componentsPreLogic != null) {
            for (Component<?> c : componentsPreLogic) {
                c.runOn(this);
            }
        }
        update();
        if (componentsPostLogic != null) {
            for (Component<?> c : componentsPostLogic) {
                c.runOn(this);
            }
        }
        return this;
    }

    /**
     * adds a Component to this GameObject.
     *
     * @see Component
     * @param c
     * @return this GameObject
     */
    public final GameObject addComponent(Component<?> c) {
        if (!c.supportsGameObject(this)) {
            if (OmniKryptecEngine.isCreated()) {
                OmniKryptecEngine.instance().errorOccured(new OmniKryptecException(getClass().getSimpleName() + " does not support the component " + c.getClass().getSimpleName()), "Error while adding component to a GameObject");
            } else {
                throw new OmniKryptecException(getClass().getSimpleName() + " does not support the component " + c.getClass().getSimpleName());
            }
        }
        if (c.getPrio() < 0) {
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
     *
     * @see Component
     * @param c
     * @return this GameObject
     */
    public final GameObject removeComponent(Component<?> c) {
        if (c.getPrio() < 0) {
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

    private List<Component<?>> tmp;

    /**
     * all components of this GameObject
     *
     * @return
     */
    public final Component<?>[] getComponents() {
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
     * the first occurence of a Component from the Class <code>type</code> or
     * <code>null</code> if none is found.
     *
     * @param type
     * @return the component
     */
    
    public final <T> T getComponent(Class<T> type) {
        if (componentsPreLogic != null) {
            for (Component<?> c : componentsPreLogic) {
                if (c.getClass() == type) {
                    return (T) c;
                }
            }
        }
        if (componentsPostLogic != null) {
            for (Component<?> c : componentsPostLogic) {
                if (c.getClass() == type) {
                    return (T) c;
                }
            }
        }
        return null;
    }

    /**
     * all occurences of a Component from the Class <code>type</code>
     *
     * @param type
     * @return a ArrayList of Components or an Empty ArrayList
     */
    
    public final <T> ArrayList<T> getComponents(Class<T> type) {
        final ArrayList<T> cp = new ArrayList<>();
        if (componentsPreLogic != null) {
            for (Component<?> c : componentsPreLogic) {
                if (c.getClass() == type) {
                    cp.add((T) c);
                }
            }
        }
        if (componentsPostLogic != null) {
            for (Component<?> c : componentsPostLogic) {
                if (c.getClass() == type) {
                    cp.add((T) c);
                }
            }
        }
        return cp;
    }

    /**
     * override this to do some logic. only gets executed for
     * {@link UpdateType#SEMISTATIC} pr {@link UpdateType#DYNAMIC} or if the
     * logic is forced.
     *
     * @see #doLogic(boolean)
     */
    protected void update() {
    }
    
    public void added(){
    }
    
    /**
     * called then the GameObject is finally removed from the scene.
     *
     * @see Scene3D#removeGameObject(GameObject, boolean)
     * @see Scene3D#removeGameObject(GameObject)
     * @return
     */
    public final GameObject deleteOperation() {
        if (componentsPreLogic != null) {
            for (Component<?> c : componentsPreLogic) {
                c.deleteOp(this);
            }
        }
        if (componentsPostLogic != null) {
            for (Component<?> c : componentsPostLogic) {
                c.deleteOp(this);
            }
        }
        delete();
        gameObjects.remove(this);
        return this;
    }
    
	public final GameObject addedOperation() {
		if (componentsPreLogic != null) {
            for (Component<?> c : componentsPreLogic) {
                c.addedOp(this);
            }
        }
        if (componentsPostLogic != null) {
            for (Component<?> c : componentsPostLogic) {
                c.addedOp(this);
            }
        }
        added();
        return this;
	}

    /**
     * override this to let your gameobject do its things when deleted the cam
     */
    protected void delete() {
    }

    /**
     * if true the gameobject is active and will be processed. if
     * <code>false</code> the logic will never be executed automatically.
     *
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

//    /**
//     * wont copy physics!
//     *
//     * @param toCopy
//     * @return
//     */
//    public static GameObject copy(GameObject toCopy) {
//        GameObject go = new GameObject(toCopy.name);
//        go.name = toCopy.name;
//        go.logicEnabled = toCopy.logicEnabled;
//        go.setParent(toCopy.getParent());
//        go.transform = toCopy.getTransform().getNewCopy();
//        return go;
//    }
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
        //setParent(toCopy.getParent());
        //  transform = toCopy.getTransform().getNewCopy();
        return this;
    }

    public abstract void checkChunkPos(boolean force);

    /**
     * if true this GameObject will always be processed regardless of the camera
     * pos.
     *
     * @param b
     * @return
     */
    public GameObject setGlobal(boolean b) {
        this.isglobal = b;
        checkChunkPos(true);
        return this;
    }

    /**
     * is this GameObject global?
     *
     * @return
     */
    public boolean isGlobal() {
        return isglobal;
    }

    @Override
    public String toString() {
        return String.format("%s (%d)", getClass().getName(), ULTIMATE_IDENTIFIER.toEpochMilli());
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

    //FIXME fixen
    @Override
    public DataMap toDataMap(DataMap data) {
        data.put("name", name);
        data.put("isglobal", isglobal);
        data.put("active", logicEnabled);
//        if (parent != null) {
//            data.put("parent", parent.toDataMap(new DataMap("parent")));
//        }
//        data.put("transform", transform.toDataMap(new DataMap("transform")));
        return data;
    }

    //FIXME fixen
    public static GameObject newInstanceFromDataMap(DataMap data) {
        if (data == null) {
            return null;
        }
        String name = data.getString("name");
        if (name == null || name.isEmpty()) {
            return null;
        }
        //     final GameObject gameObject = byName(GameObject.class, name, false);
        //  return (gameObject != null ? gameObject : new GameObject()).fromDataMap(data);
        return null;
    }

    @Override
    public GameObject fromDataMap(DataMap data) {
        if (data == null) {
            return null;
        }
        setName(data.getString("name"));
        setGlobal(data.getBoolean("isglobal"));
        setLogicEnabled(data.getBoolean("isActive"));
        //   DataMap dataMap_temp = data.getDataMap("parent");
//        if (parent == null) {
//            parent = newInstanceFromDataMap(dataMap_temp);
//        } else {
//            parent.fromDataMap(dataMap_temp); // FIXME Hmmm ist die Frage weil die Parents und id und so
//        }
//        dataMap_temp = data.getDataMap("transform");
//        if (transform == null) {
//            transform = Transform3D.newInstanceFromDataMap(dataMap_temp);
//        } else {
//            transform.fromDataMap(dataMap_temp);
//        }
        return this;
    }

}
