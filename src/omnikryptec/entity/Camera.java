package omnikryptec.entity;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import omnikryptec.util.Maths;

public class Camera extends GameObject{
	

	
	private Matrix4f projection;
	private Matrix4f view;
	
	public Camera(){
		super();
	}
	
	public Camera(Matrix4f proj){
		super();
		projection = proj;
	}
	
	public Matrix4f getProjectionMatrix(){
		return projection;
	}
	
	
	private Vector3f absrot,campos,negcampos;
	public Matrix4f getViewMatrix() {
		if(view==null){
			view = new Matrix4f();
		}
		absrot = getAbsoluteRotation();
		campos = getAbsolutePos();
		negcampos = new Vector3f(-campos.x, -campos.y, -campos.z);
		view.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(absrot.x), Maths.X, view, view);
		Matrix4f.rotate((float) Math.toRadians(absrot.y), Maths.Y, view, view);
		Matrix4f.rotate((float) Math.toRadians(absrot.z), Maths.Z, view, view);
		Matrix4f.translate(negcampos, view, view);
		return view;
	}
	
	public Matrix4f getProjectionViewMatrix(){
		return Matrix4f.mul(getProjectionMatrix(), getViewMatrix(), null);
	}
	
	public Camera setPerspectiveProjection(float fovdeg, float far, float near) {
		return setPerspectiveProjection(fovdeg, far, near, Display.getWidth(), Display.getHeight());
	}

	public Camera setPerspectiveProjection(float fovdeg, float far, float near, float width, float height) {
		this.near = near;
		this.far = far;
		this.fov = fovdeg;
		projection = Maths.setPerspectiveProjection(fovdeg, far, near, width, height);
		return this;
	}

	private float near,far,fov;
	private Vector2f vect = new Vector2f();
	
	public float getFOVDeg(){
		return fov;
	}
	
	public Vector2f getPlanesForLR(){
		vect.x =  -far / (far-near);
		vect.y = -far * near / (far-near);
		return vect;
	}
	
	public Camera setOrthographicProjection(float left, float right, float bottom, float top, float near,
			float far) {
		this.near = near;
		this.far = far;
		this.fov = 180;
		projection = Maths.setOrthographicProjection(left, right, bottom, top, near, far);
		return this;
	}
	
	public Camera setOrthographicProjection2D(float x, float y, float width, float height) {
		return setOrthographicProjection( x, x + width, y + height, y, 1, -1);
	}

	public Camera setOrthographicProjection2D(float x, float y, float width, float height, float near, float far) {
		return setOrthographicProjection(x, x + width, y, y + height, near, far);
	}

	
}
