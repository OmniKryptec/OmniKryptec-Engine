package omnikryptec.entity;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import omnikryptec.deferredlight.DeferredLightPrepare;
import omnikryptec.util.Color;

public class Light extends GameObject {
	
	public static final float NO_CUTOFF_RANGE = -1;
	
	private Color color = new Color(1, 1, 1, 1);
	private DeferredLightPrepare mylightprepare = DeferredLightPrepare.FORWARD_RENDERING;
	private Vector4f coneinfo = new Vector4f(1, 1, 1, 180);
	private boolean directional = false;	
	private Vector4f att = new Vector4f(1, 1, 1, NO_CUTOFF_RANGE);
	private Vector3f coneAtt = new Vector3f(1, 0, 0);
	
	public Light setConeAttenuation(float a, float b, float c){
		coneAtt.set(a, b, c);
		return this;
	}
	
	public Vector3f getConeAttenuation(){
		return coneAtt;
	}

	public Light setColor(Color c) {
		this.color = c;
		return this;
	}

	public Light setColor(float r, float g, float b) {
		color.set(r, g, b);
		return this;
	}

	public Color getColor() {
		return color;
	}
	
	public Light setConeDirection(float x, float y, float z){
		coneinfo.set(x, y, z);
		return this;
	}
	
	public Light setConeDegrees(float d){
		coneinfo.w = (float) Math.cos(Math.toRadians(d));
		return this;
	}
	
	public Vector4f getConeInfo(){
		return coneinfo;
	}
	
	public Light setDirectional(boolean b){
		directional = b;
		return this;
	}
	
	public Light setPointLight(){
		setConeDegrees(180);
		setConeAttenuation(1, 0, 0);
		return this;
	}

	public Light setShader(DeferredLightPrepare shader) {
		this.mylightprepare = shader;
		return this;
	}

	public DeferredLightPrepare getShader() {
		return mylightprepare;
	}

	public Vector4f getAttenuation() {
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
		att.x = a;
		att.y = b;
		att.z = c;
		return this;
	}
	
	public Light setCuttOffRange(float r){
		att.w = r;
		return this;
	}
	
	public final boolean isDirectional() {
		return directional;
	}
}
