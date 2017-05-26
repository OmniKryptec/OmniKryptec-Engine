package omnikryptec.storing;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Light extends GameObject {
	
	private Vector4f vec = new Vector4f();
	private float radius=1;
	private Vector3f color = new Vector3f();
	
	private Vector3f tmp;
	public Vector4f getPosRad(){
		tmp = getAbsolutePos();
		vec.x = tmp.x;
		vec.y = tmp.y;
		vec.z = tmp.z;
		vec.w = radius;
		return vec;
	}
	
	public void setColor(float r, float g, float b){
		color.x = r;
		color.y = g;
		color.z = b;
	}
	
	public Vector3f getColor(){
		return color;
	}
	
	public void setRadius(float r){
		this.radius = r;
	}
	
	public float getRadius(){
		return radius;
	}
	
}
