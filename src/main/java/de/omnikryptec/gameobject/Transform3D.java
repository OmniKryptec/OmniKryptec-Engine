package de.omnikryptec.gameobject;

import omnikryptec.graphics.GraphicsUtil;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.renderer.d2.RenderChunk2D;
import omnikryptec.renderer.d3.RenderChunk3D;
import omnikryptec.settings.GameSettings;
import omnikryptec.test.saving.DataMap;
import omnikryptec.test.saving.DataMapSerializable;
import omnikryptec.util.EnumCollection.UpdateType;
import omnikryptec.util.Maths;
import omnikryptec.util.SerializationUtil;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform3D implements DataMapSerializable, Positionable3D {

	protected Transform3D parent;
	protected Vector3f position;
	protected Quaternionf rotation;
	protected Vector3f scale;

	private Matrix4f transformation;

	protected boolean manualMatrixRecalculation = false;

	public Transform3D() {
		this(new Vector3f(0));
	}

	public Transform3D(Vector3f pos) {
		this(pos, new Quaternionf(0, 0, 0));
	}

	public Transform3D(Vector3f pos, Quaternionf rot) {
		this(pos, rot, new Vector3f(1));
	}

	public Transform3D(Vector3f pos, Quaternionf rot, Vector3f scale) {
		this(null, pos, rot, scale);
	}

	public Transform3D(Transform3D parent, Vector3f pos, Quaternionf rot, Vector3f scale) {
		this.parent = parent;
		this.position = pos;
		this.rotation = rot;
		this.scale = scale;
		transformation = new Matrix4f();
		recalculateTransformation();
	}

	public Transform3D(Transform3D parent) {
		this();
		setParent(parent);
	}

	public Transform3D setDirty() {
		lastframe = -1;
		return this;
	}

	public Transform3D getNewCopy() {
		return new Transform3D(parent, getPositionNew(), getRotationNew(), getScaleNew());
	}

	public Transform3D setParent(Transform3D transform) {
		this.parent = transform;
		setDirty();
		return this;
	}

	public Transform3D getParent() {
		return parent;
	}

	public Transform3D setManualMatrixRecalculation(boolean b) {
		this.manualMatrixRecalculation = b;
		return this;
	}

	public boolean isManualMatrixRecalculation() {
		return manualMatrixRecalculation;
	}

	public Transform3D setX(float x) {
		this.position.x = x;
		return this;
	}

	public Transform3D setY(float y) {
		this.position.y = y;
		return this;
	}

	public Transform3D setZ(float z) {
		this.position.z = z;
		return this;
	}

	public Transform3D increasePosition(float x, float y, float z) {
		this.position.x += x;
		this.position.y += y;
		this.position.z += z;
		return this;
	}

	public Transform3D increaseRotation(float x, float y, float z, float w) {
		this.rotation.x += x;
		this.rotation.y += y;
		this.rotation.z += z;
		this.rotation.w += w;
		return this;
	}

	public Transform3D increaseRotation(float xa, float ya, float za) {
		this.rotation.rotate(xa, ya, za);
		return this;
	}

	public Transform3D increaseScale(float x, float y, float z) {
		this.scale.x += x;
		this.scale.y += y;
		this.scale.z += z;
		return this;
	}

	public Transform3D setPosition(float x, float y, float z) {
		this.position.set(x, y, z);
		return this;
	}

	public Transform3D setRotation(float x, float y, float z, float w) {
		this.rotation.set(x, y, z, w);
		return this;
	}

	public Transform3D setScale(float x, float y, float z) {
		this.scale.set(x, y, z);
		return this;
	}

	public Transform3D setScale(float d) {
		return setScale(d, d, d);
	}

	public Transform3D setPosition(Vector3f pos) {
		this.position = pos;
		return this;
	}

	public Transform3D setRotation(Quaternionf q) {
		this.rotation = q;
		return this;
	}

	public Transform3D setScale(Vector3f scale) {
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
		if (checkupdate && !manualMatrixRecalculation && GraphicsUtil.needsUpdate(lastframe, freq, updatetype)
				&& (!Maths.fastEquals3f(lastpos, getPosition()) || !Maths.fastEquals4f(lastrot, getRotation())
						|| !Maths.fastEquals3f(lastscale, getScale()))) {
			return recalculateTransformation();
		}
		return transformation;
	}

	/**
	 * will be disabled with manualMatrixRecalculation set to true; if so only
	 * returns the transformation matrix
	 * 
	 * @return
	 */
	public Matrix4f recalculateTransformation() {
		if (transformation == null) {
			transformation = new Matrix4f();
		}
		if (manualMatrixRecalculation) {
			return transformation;
		}
		lastframe = OmniKryptecEngine.instance().getDisplayManager().getFramecount();
		transformation.identity();
		transformation.rotate(lastrot.set(getRotation(true)));
		transformation.translate(lastpos.set(getPosition(true)));
		transformation.scale(lastscale.set(getScale(true)));
		return transformation;
	}

	public Matrix4f getMatrix() {
		return transformation;
	}

	// TMP-Vars
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
	public Transform3D fromDataMap(DataMap data) {
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

	/**
	 * the chunkx. used for rendering
	 *
	 * @see GameSettings#usesRenderChunking()
	 * @return chunkx
	 */
	public final long getChunkX() {
		return Maths.fastFloor(getPosition(true).x / RenderChunk3D.getWidth());
	}

	/**
	 * the chunky. used for rendering
	 *
	 * @see GameSettings#usesRenderChunking()
	 * @return chunky
	 */
	public final long getChunkY() {
		return Maths.fastFloor(getPosition(true).y / RenderChunk3D.getHeight());
	}

	/**
	 * the chunkz. used for rendering
	 *
	 * @see GameSettings#usesRenderChunking()
	 * @return
	 */
	public final long getChunkZ() {
		return Maths.fastFloor(getPosition(true).z / RenderChunk3D.getDepth());
	}

	public final long getChunkX2D() {
		return Maths.fastFloor(getPosition(true).x / RenderChunk2D.getWidth());
	}

	public final long getChunkY2D() {
		return Maths.fastFloor(getPosition(true).y / RenderChunk2D.getHeight());
	}

	public static Transform3D newInstanceFromDataMap(DataMap data) {
		if (data == null) {
			return null;
		}
		return new Transform3D().fromDataMap(data);
	}

	@Override
	public String toString() {
		return "Transform2D [Pos.: " + position.toString() + " Rot.: " + rotation + " Scale: " + scale + " Parent: "
				+ parent + "]";
	}

}
