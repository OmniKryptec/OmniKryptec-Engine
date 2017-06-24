package omnikryptec.entity;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import omnikryptec.test.saving.DataMap;
import omnikryptec.util.Maths;
import omnikryptec.util.SerializationUtil;

public class Camera extends GameObject {

	private Matrix4f projection;
	private Matrix4f view;

	public Camera() {
		super();
	}

	public Camera(Matrix4f proj) {
		super();
		projection = proj;
	}

	public Matrix4f getProjectionMatrix() {
		return projection;
	}

	private Vector3f absrot, campos, negcampos, lastrot = new Vector3f(), lastpos = new Vector3f();

	public Matrix4f getViewMatrix() {
		if (view == null) {
			view = new Matrix4f();
		}
		absrot = getAbsoluteRotation();
		campos = getAbsolutePos();
		if (!Maths.fastEquals3f(campos, lastpos) || !Maths.fastEquals3f(lastrot, absrot)) {
			negcampos = new Vector3f(-campos.x, -campos.y, -campos.z);
			view.setIdentity();
			Matrix4f.rotate((float) Math.toRadians(absrot.x), Maths.X, view, view);
			Matrix4f.rotate((float) Math.toRadians(absrot.y), Maths.Y, view, view);
			Matrix4f.rotate((float) Math.toRadians(absrot.z), Maths.Z, view, view);
			Matrix4f.translate(negcampos, view, view);
			lastpos.set(campos);
			lastrot.set(absrot);
		}
		return view;
	}

	public Matrix4f getInverseProjView() {
		return Matrix4f.invert(getProjectionViewMatrix(), null);
	}

	public Matrix4f getProjectionViewMatrix() {
		return Matrix4f.mul(getProjectionMatrix(), getViewMatrix(), null);
	}

	public Camera setPerspectiveProjection(float fovdeg, float near, float far) {
		return setPerspectiveProjection(fovdeg, near, far, Display.getWidth(), Display.getHeight());
	}

	public Camera setPerspectiveProjection(float fovdeg, float near, float far, float width, float height) {
		projection = Maths.setPerspectiveProjection(fovdeg, far, near, width, height);
		return this;
	}

	public Camera setOrthographicProjection(float left, float right, float bottom, float top, float near, float far) {
		projection = Maths.setOrthographicProjection(left, right, bottom, top, near, far);
		return this;
	}

	public Camera setOrthographicProjection2D(float x, float y, float width, float height) {
		return setOrthographicProjection(x, x + width, y + height, y, 1, -1);
	}

	public Camera setOrthographicProjection2D(float x, float y, float width, float height, float near, float far) {
		return setOrthographicProjection(x, x + width, y, y + height, near, far);
	}

	public final Camera setValuesFrom(Camera toCopy) {
		if (toCopy == null) {
			return this;
		}
		setName(toCopy.getName());
		setActive(toCopy.isActive());
		setParent(toCopy.getParent());
		setRotation(toCopy.getRotation());
		setPos(toCopy.getRelativePos());
		absrot = toCopy.absrot;
		campos = toCopy.campos;
		lastpos = toCopy.lastpos;
		lastrot = toCopy.lastrot;
		negcampos = toCopy.negcampos;
		projection = toCopy.projection;
		view = toCopy.view;
		return this;
	}

	public static Camera copy(GameObject toCopy) {
		Camera camera = new Camera();
		if (toCopy == null) {
			return camera;
		}
		if (toCopy instanceof Camera) {
			Camera camera_toCopy = (Camera) toCopy;
			camera.absrot = camera_toCopy.absrot;
			camera.campos = camera_toCopy.campos;
			camera.lastpos = camera_toCopy.lastpos;
			camera.lastrot = camera_toCopy.lastrot;
			camera.negcampos = camera_toCopy.negcampos;
			camera.projection = camera_toCopy.projection;
			camera.view = camera_toCopy.view;
		}
		camera.setName(toCopy.getName());
		camera.setActive(toCopy.isActive());
		camera.setParent(toCopy.getParent());
		camera.setRotation(toCopy.getRotation());
		camera.setPos(toCopy.getRelativePos());
		return camera;
	}

	@Override
	public DataMap toDataMap(DataMap data) {
		DataMap data_temp = super.toDataMap(new DataMap("gameObject"));
		data.put(data_temp.getName(), data_temp);
		data.put("absrot", SerializationUtil.vector3fToString(absrot));
		data.put("campos", SerializationUtil.vector3fToString(campos));
		data.put("lastpos", SerializationUtil.vector3fToString(lastpos));
		data.put("lastrot", SerializationUtil.vector3fToString(lastrot));
		data.put("negcampos", SerializationUtil.vector3fToString(negcampos));
		data.put("projection", SerializationUtil.matrix4fToString(projection));
		data.put("view", SerializationUtil.matrix4fToString(view));
		return data;
	}

	public static Camera newInstanceFromDataMap(DataMap data) {
		if (data == null) {
			return null;
		}
		final Camera camera = byName(Camera.class, data.getString("name"));
		return (camera != null ? camera : new Camera()).fromDataMap(data);
	}

	@Override
	public Camera fromDataMap(DataMap data) {
		if (data == null) {
			return this;
		}
		DataMap dataMap_temp = data.getDataMap("gameObject");
		super.fromDataMap(dataMap_temp);
		absrot = SerializationUtil.stringToVector3f(data.getString("absrot"));
		campos = SerializationUtil.stringToVector3f(data.getString("campos"));
		lastpos = SerializationUtil.stringToVector3f(data.getString("lastpos"));
		lastrot = SerializationUtil.stringToVector3f(data.getString("lastrot"));
		negcampos = SerializationUtil.stringToVector3f(data.getString("negcampos"));
		projection = SerializationUtil.stringToMatrix4f(data.getString("projection"));
		view = SerializationUtil.stringToMatrix4f(data.getString("view"));
		return this;
	}

}
