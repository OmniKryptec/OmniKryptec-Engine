package omnikryptec.gameobject;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import omnikryptec.display.DisplayManager;
import omnikryptec.test.saving.DataMap;
import omnikryptec.test.saving.DataMapSerializable;
import omnikryptec.util.Maths;
import omnikryptec.util.RenderUtil;
import omnikryptec.util.SerializationUtil;

public class Transform implements DataMapSerializable, Positionable {

    protected Transform parent;
    protected Vector3f position;
    protected Quaternionf rotation;
    protected Vector3f scale;

    private Matrix4f transformation;

    protected boolean disableRecalculation = false;

    public Transform() {
        this(new Vector3f(0));
    }

    public Transform(Vector3f pos) {
        this(pos, new Quaternionf(0, 0, 0));
    }

    public Transform(Vector3f pos, Quaternionf rot) {
        this(pos, rot, new Vector3f(1));
    }

    public Transform(Vector3f pos, Quaternionf rot, Vector3f scale) {
        this(null, pos, rot, scale);
    }

    public Transform(Transform parent, Vector3f pos, Quaternionf rot, Vector3f scale) {
        this.parent = parent;
        this.position = pos;
        this.rotation = rot;
        this.scale = scale;
        transformation = new Matrix4f();
        recalculateTransformation();
    }

    public Transform(Transform parent) {
        this();
        setParent(parent);
    }

    public Transform getNewCopy() {
        return new Transform(parent, getPositionNew(), getRotationNew(), getScaleNew());
    }

    public Transform setParent(Transform transform) {
        lastframe = -1;
        this.parent = transform;
        return this;
    }

    public Transform getParent() {
        return parent;
    }

    public Transform setDisableRecalculation(boolean b) {
        this.disableRecalculation = b;
        return this;
    }

    public boolean isRecalculationDisabled() {
        return disableRecalculation;
    }

    public Transform setX(float x) {
        lastframe = -1;
        this.position.x = x;
        return this;
    }

    public Transform setY(float y) {
        lastframe = -1;
        this.position.y = y;
        return this;
    }

    public Transform setZ(float z) {
        lastframe = -1;
        this.position.z = z;
        return this;
    }

    public Transform increasePosition(float x, float y, float z) {
        lastframe = -1;
        this.position.x += x;
        this.position.y += y;
        this.position.z += z;
        return this;
    }

    public Transform increaseRotation(float x, float y, float z, float w) {
        lastframe = -1;
        this.rotation.x += x;
        this.rotation.y += y;
        this.rotation.z += z;
        this.rotation.w += w;
        return this;
    }

    public Transform increaseRotation(float xa, float ya, float za) {
        lastframe = -1;
        this.rotation.rotate(xa, ya, za);
        return this;
    }

    public Transform increaseScale(float x, float y, float z) {
        lastframe = -1;
        this.scale.x += x;
        this.scale.y += y;
        this.scale.z += z;
        return this;
    }

    public Transform setPosition(float x, float y, float z) {
        lastframe = -1;
        this.position.set(x, y, z);
        return this;
    }

    public Transform setRotation(float x, float y, float z, float w) {
        lastframe = -1;
        this.rotation.set(x, y, z, w);
        return this;
    }

    public Transform setScale(float x, float y, float z) {
        lastframe = -1;
        this.scale.set(x, y, z);
        return this;
    }

    public Transform setScale(float d) {
        return setScale(d, d, d);
    }

    public Transform setPosition(Vector3f pos) {
        lastframe = -1;
        this.position = pos;
        return this;
    }

    public Transform setRotation(Quaternionf q) {
        lastframe = -1;
        this.rotation = q;
        return this;
    }

    public Transform setScale(Vector3f scale) {
        lastframe = -1;
        this.scale = scale;
        return this;
    }

    public Vector3f getPositionNew() {
        return new Vector3f(position);
    }

    public Quaternionf getRotationNew() {
        return new Quaternionf(rotation);
    }

    public Vector3f getScaleNew() {
        return new Vector3f(scale);
    }

    public Vector3f getPositionSimple() {
        return position;
    }

    public Quaternionf getRotationSimple() {
        return rotation;
    }

    public Vector3f getScaleSimple() {
        return scale;
    }

    @Override
    public Vector3f getPosition() {
        return getPosition(false);
    }

    public Vector3f getPosition(boolean simple) {
        if (parent == null) {
            return simple ? position : new Vector3f(position);
        }
        return parent.getPosition(false).add(position);
    }

    public Quaternionf getRotation() {
        return getRotation(false);
    }

    public Quaternionf getRotation(boolean simple) {
        if (parent == null) {
            return simple ? rotation : new Quaternionf(rotation);
        }
        return parent.getRotation(false).add(rotation);
    }

    public Vector3f getScale() {
        return getScale(false);
    }

    public Vector3f getScale(boolean simple) {
        if (parent == null) {
            return simple ? scale : new Vector3f(scale);
        }
        return parent.getScale(false).add(scale);
    }

    public Vector3f getEulerAngelsXYZ() {
        return getEulerAngelsXYZ(false);
    }

    private Vector3f tmp = new Vector3f();

    public Vector3f getEulerAngelsXYZ(boolean simple) {
        return getEulerAngelsXYZ(simple, simple ? tmp.set(0) : new Vector3f());
    }

    public Vector3f getEulerAngelsXYZ(boolean simple, Vector3f dest) {
        return getRotation(simple).getEulerAnglesXYZ(dest);
    }

    public Matrix4f getTransformation() {
        return this.getTransformation(true);
    }

    public Matrix4f getTransformation(boolean checkupdate) {
        return this.getTransformation(UpdateType.DYNAMIC, 1, checkupdate);
    }

    public Matrix4f getTransformation(UpdateType type) {
        return this.getTransformation(type, 1);
    }

    public Matrix4f getTransformation(UpdateType type, int freq) {
        return this.getTransformation(type, freq, true);
    }

    public Matrix4f getTransformation(UpdateType updatetype, int freq, boolean checkupdate) {
        if (checkupdate && !disableRecalculation && RenderUtil.needsUpdate(lastframe, freq, updatetype) && (!Maths.fastEquals3f(lastpos, getPosition()) || !Maths.fastEquals4f(lastrot, getRotation()) || !Maths.fastEquals3f(lastscale, getScale()))) {
            return recalculateTransformation();
        }
        return transformation;
    }

    public Matrix4f recalculateTransformation() {
        if (transformation == null) {
            transformation = new Matrix4f();
        }
        if (disableRecalculation) {
            return transformation;
        }
        lastframe = DisplayManager.instance().getFramecount();
        transformation.identity();
        transformation.rotate(lastrot.set(getRotation(true)));
        transformation.translate(lastpos.set(getPosition(true)));
        transformation.scale(lastscale.set(getScale(true)));
        return transformation;
    }

    public Transform setDirty() {
        lastframe = -1;
        return this;
    }

    //TMP-Vars
    private Vector3f lastpos = new Vector3f(), lastscale = new Vector3f();
    private Quaternionf lastrot = new Quaternionf();
    private long lastframe = 0;

    @Override
    public String getName() {
        return "";
    }

    @Override
    public DataMap toDataMap(DataMap data) {
        if (parent != null) {
            data.put("parent", parent.toDataMap(new DataMap("parent")));
        }
        data.put("position", SerializationUtil.vector3fToString(position));
        data.put("rotation", SerializationUtil.quaternionfToString(rotation));
        data.put("scale", SerializationUtil.vector3fToString(scale));
        return data;
    }

    @Override
    public Transform fromDataMap(DataMap data) {
        if (data == null) {
            return null;
        }
        setScale(SerializationUtil.stringToVector3f(data.getString("scale")));
        setRotation(SerializationUtil.stringToQuaternionf(data.getString("rotation")));
        setPosition(SerializationUtil.stringToVector3f(data.getString("position")));
        DataMap dataMap_temp = data.getDataMap("parent");
        if (parent == null) {
            parent = newInstanceFromDataMap(dataMap_temp);
        } else {
            parent.fromDataMap(dataMap_temp);
        }
        return this;
    }

    public static Transform newInstanceFromDataMap(DataMap data) {
        if (data == null) {
            return null;
        }
        return new Transform().fromDataMap(data);
    }

}
