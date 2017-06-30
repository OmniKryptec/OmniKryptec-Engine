package omnikryptec.gameobject.gameobject;

import org.joml.Vector3f;
import org.joml.Vector4f;

import omnikryptec.deferredlight.DeferredLightPrepare;
import omnikryptec.util.Color;

public class Light extends GameObject {
	
	public static final float NO_CUTOFF_RANGE = -1;
	
	private Color color = new Color(1, 1, 1, 1);
	private Vector4f coneinfo = new Vector4f(1, 1, 1, -1);
	private boolean directional = false;	
	private Vector4f att = new Vector4f(1, 1, 1, NO_CUTOFF_RANGE);
	private Vector3f coneAtt = new Vector3f(1, 0, 0);
	
	/**
	 * how much the light attenuates to the side of the cone of a spotlight.
	 * distance: value between 0 and 1, 0 is in the middle and 1 is the highest distance.
	 * final cone-attenuation = a + b * distance + c * distance * distance
	 * lower values mean less light.
	 * the final light attenuation then is attenuation * cone-attenuation.
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public Light setConeAttenuation(float a, float b, float c){
		coneAtt.set(a, b, c);
		return this;
	}
	
	public Vector3f getConeAttenuation(){
		return coneAtt;
	}

	/**
	 * the color and intensity of the light. values greater than 1 are allowed.
	 * @param c
	 * @return
	 */
	public Light setColor(Color c) {
		this.color = c;
		return this;
	}

	/**
	 * the color and intensity of the light. values greater than 1 are allowed.
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public Light setColor(float r, float g, float b) {
		color.set(r, g, b);
		return this;
	}

	public Color getColor() {
		return color;
	}
	
	/**
	 * the direction the spotlight is facing.
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Light setConeDirection(float x, float y, float z){
		coneinfo.x = x;
		coneinfo.y = y;
		coneinfo.z = z;
		return this;
	}
	
	/**
	 * how big the angel of the spotlight is.
	 * @param d
	 * @return
	 */
	public Light setConeDegrees(float d){
		coneinfo.w = (float) Math.cos(Math.toRadians(d));
		return this;
	}
	
	public Vector4f getConeInfo(){
		return coneinfo;
	}
	
	/**
	 * sets this light to be a directional light (e.g. sun).
	 * the position of this light is the direction of the light.
	 * @param b
	 * @return
	 */
	public Light setDirectional(boolean b){
		directional = b;
		return this;
	}
	
	/**
	 * sets this light to be a point light. 
	 * modifys condedegress, coneattenuation and if this light is a directional light.
	 * @return
	 */
	public Light setPointLight(){
		setConeDegrees(180);
		setConeAttenuation(1, 0, 0);
		setDirectional(false);
		return this;
	}

	public Vector4f getAttenuation() {
		return att;
	}

	/**
	 * how much the light attenuates into the distance (only for point- and spotlights).
	 * distance: value between 0 and infinity.
	 * attenuation = 1.0 / (a + b * distance + c * distance * distance)
	 * higher values mean less light.
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
	
	/**
	 * the maximum range the light can shine or NO_CUTOFF_RANGE to not cutoff the light.
	 * this does not affect the attenuation of this light.
	 * @param r
	 * @return
	 */
	public Light setCuttOffRange(float r){
		att.w = r;
		return this;
	}
	
	public final boolean isDirectional() {
		return directional;
	}
}
