package omnikryptec.light;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import omnikryptec.entity.GameObject;

public class Light extends GameObject {

	private Vector4f vec = new Vector4f();
	private float radius = 1;
	private Vector3f color = new Vector3f();
	private LightPrepare mylightprepare = LightPrepare.DEFAULT_LIGHT_PREPARE;

	private Vector3f att;

	private Vector3f tmp;

	public Vector4f getPosRad() {
		tmp = getAbsolutePos();
		vec.x = tmp.x;
		vec.y = tmp.y;
		vec.z = tmp.z;
		vec.w = radius;
		return vec;
	}

	public Light setColor(float r, float g, float b) {
		color.x = r;
		color.y = g;
		color.z = b;
		return this;
	}

	public Vector3f getColor() {
		return color;
	}

	public Light setRadius(float r) {
		this.radius = r;
		return this;
	}

	public float getRadius() {
		return radius;
	}

	/**
	 * Sets this light to be a directional light.
	 * 
	 * the radius will be overwritten.
	 * 
	 * @return this light
	 */
	public Light setDirectional() {
		radius = -1;
		return this;
	}

	public boolean isDirectional() {
		return radius < 0;
	}

	public Light setShader(LightPrepare shader) {
		this.mylightprepare = shader;
		return this;
	}

	public LightPrepare getShader() {
		return mylightprepare;
	}

	public Vector3f getAttenuation() {
		return att;
	}

	/**
	 * not supported by all LightPrepare
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public Light setAttenuation(float a, float b, float c) {
		if (att == null) {
			att = new Vector3f();
		}
		att.x = a;
		att.y = b;
		att.z = c;
		return this;
	}
}
