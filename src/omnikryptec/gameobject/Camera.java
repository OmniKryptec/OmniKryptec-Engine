package omnikryptec.gameobject;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import omnikryptec.display.Display;
import omnikryptec.test.saving.DataMap;
import omnikryptec.util.Maths;
import omnikryptec.util.SerializationUtil;

public class Camera extends GameObject {

    private Matrix4fc projection;
    private Matrix4f view;
    
    public Camera() {
        super();
    }

    public Camera(Matrix4f proj) {
        super();
        projViewNeedsUpdate = true;
        invPVneedsUpdate = true;
        projection = proj.toImmutable();
    }

    public Matrix4fc getProjectionMatrix() {
        return projection;
    }

    private Vector3f campos, negcampos, lastpos = new Vector3f();
    private Quaternionf absrot, lastrot = new Quaternionf();

    public Matrix4f getViewMatrix() {
        if (view == null) {
            view = new Matrix4f();
        }
        absrot = getTransform().getRotation(true);
        campos = getTransform().getPosition(true);
        if (!Maths.fastEquals3f(campos, lastpos) || !Maths.fastEquals4f(lastrot, absrot)) {
            negcampos = new Vector3f(-campos.x, -campos.y, -campos.z);
            view.identity();
            view.rotate(absrot);
            view.translate(negcampos);
            lastpos.set(campos);
            lastrot.set(absrot);
            projViewNeedsUpdate = true;
            invPVneedsUpdate = true;
        }
        return view;
    }
    
    private Vector3f eul = new Vector3f(), tmp;
    public void reflect(float height){
    	tmp = getTransform().getPosition(true);
		tmp.y -= 2 * (tmp.y - height);
    	getTransform().rotation.w *= -1;
    	getTransform().rotation.z *= -1;
		getTransform().setDirty();
	}
    
    private Matrix4f invProjView = new Matrix4f();
    private boolean invPVneedsUpdate = true;
    public Matrix4f getInverseProjView() {
    	if(invPVneedsUpdate){
    		invPVneedsUpdate = false;
            return getProjectionViewMatrix().invert(invProjView);
    	}else{
    		return invProjView;
    	}
    }
    
    private boolean projViewNeedsUpdate=true;
    private Matrix4f projview = new Matrix4f();
    public Matrix4f getProjectionViewMatrix() {
        if(projViewNeedsUpdate){
        	projViewNeedsUpdate=false;
        	return getProjectionMatrix().mul(getViewMatrix(), projview); 
        }else{
        	return projview;
        }
    }

    public Camera setPerspectiveProjection(float fovdeg, float near, float far) {
        return setPerspectiveProjection(fovdeg, near, far, Display.getWidth(), Display.getHeight());
    }

    public Camera setPerspectiveProjection(float fovdeg, float near, float far, float width, float height) {
        projViewNeedsUpdate = true;
        invPVneedsUpdate = true;
    	projection = Maths.setPerspectiveProjection(fovdeg, far, near, width, height).toImmutable();
        return this;
    }

    public Camera setOrthographicProjection(float left, float right, float bottom, float top, float near, float far) {
        projViewNeedsUpdate = true;
        invPVneedsUpdate = true;
    	projection = Maths.setOrthographicProjection(left, right, bottom, top, near, far).toImmutable();
        return this;
    }

    public Camera setOrthographicProjection2D(float x, float y, float width, float height) {
        return setOrthographicProjection(x, x + width, y, y + height, -1, 1);
    }

    public Camera setOrthographicProjection2D(float x, float y, float width, float height, float near, float far) {
        return setOrthographicProjection(x, x + width, y, y + height, near, far);
    }

    public Camera setProjectionMatrix(Matrix4f proj) {
        projViewNeedsUpdate = true;
        invPVneedsUpdate = true;
    	this.projection = proj.toImmutable();
        return this;
    }

    public final Camera setValuesFrom(Camera toCopy) {
        if (toCopy == null) {
            return this;
        }
        super.setValuesFrom(toCopy);
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
        camera.setLogicEnabled(toCopy.isLogicEnabled());
        camera.setParent(toCopy.getParent());
        camera.setTransform(toCopy.getTransform().getNewCopy());
        return camera;
    }

    @Override
    public DataMap toDataMap(DataMap data) {
        DataMap data_temp = super.toDataMap(new DataMap("gameObject"));
        data.put(data_temp.getName(), data_temp);
        data.put("absrot", SerializationUtil.quaternionfToString(absrot));
        data.put("campos", SerializationUtil.vector3fToString(campos));
        data.put("lastpos", SerializationUtil.vector3fToString(lastpos));
        data.put("lastrot", SerializationUtil.quaternionfToString(lastrot));
        data.put("negcampos", SerializationUtil.vector3fToString(negcampos));
        data.put("projection", SerializationUtil.matrix4fToString(projection));
        data.put("view", SerializationUtil.matrix4fToString(view));
        return data;
    }

    public static Camera newInstanceFromDataMap(DataMap data) {
        if (data == null) {
            return null;
        }
        String name = data.getDataMap("gameObject").getString("name");
        if (name == null || name.isEmpty()) {
            return null;
        }
        final Camera camera = byName(Camera.class, name, false);
        return (camera != null ? camera : new Camera()).fromDataMap(data);
    }

    @Override
    public Camera fromDataMap(DataMap data) {
        if (data == null) {
            return this;
        }
        DataMap dataMap_temp = data.getDataMap("gameObject");
        super.fromDataMap(dataMap_temp);
        absrot = SerializationUtil.stringToQuaternionf(data.getString("absrot"));
        campos = SerializationUtil.stringToVector3f(data.getString("campos"));
        lastpos = SerializationUtil.stringToVector3f(data.getString("lastpos"));
        lastrot = SerializationUtil.stringToQuaternionf(data.getString("lastrot"));
        negcampos = SerializationUtil.stringToVector3f(data.getString("negcampos"));
        projection = SerializationUtil.stringToMatrix4f(data.getString("projection"));
        view = SerializationUtil.stringToMatrix4f(data.getString("view"));
        return this;
    }

}
