package omnikryptec.camera;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import omnikryptec.storing.GameObject;

public class Camera extends GameObject{
	
	
	private Matrix4f projection;
	private Matrix4f view;
	
	protected Camera(Matrix4f projmat){
		this.projection = projmat;
	}
	
	
	public Matrix4f getProjectionMatrix(){
		return projection;
	}
	
	public Matrix4f getViewMatrix(){
		view.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(getAbsoluteRotation().x), new Vector3f(1, 0, 0), view,
				view);
		Matrix4f.rotate((float) Math.toRadians(getAbsoluteRotation().y), new Vector3f(0, 1, 0), view, view);
		Matrix4f.rotate((float) Math.toRadians(getAbsoluteRotation().z), new Vector3f(0, 0, 1), view, view);
		Vector3f negativeCameraPos = new Vector3f(-getAbsolutePos().x,-getAbsolutePos().y,-getAbsolutePos().z);
		return Matrix4f.translate(negativeCameraPos, view, null);
	}
	
	public Matrix4f getProjectionViewMatrix(){
		return Matrix4f.mul(getProjectionMatrix(), getViewMatrix(), null);
	}
	
}
