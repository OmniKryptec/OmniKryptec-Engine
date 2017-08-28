package omnikryptec.gameobject;

import org.joml.Vector3f;
import org.joml.Vector4f;

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
	 * final <code>coneattenuation = a + b * distance + c * distance * distance</code>
	 * <br>
	 * lower values mean less light.
	 * the final light attenuation then is <code>attenuation * cone-attenuation</code>.
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
	
	/**
	 * the lights cone-attenuation.
	 * @see #setConeAttenuation(float, float, float)
	 * @return
	 */
	public Vector3f getConeAttenuation(){
		return coneAtt;
	}

	/**
	 * the color and intensity of the light. values greater than 1 are allowed and used for the intensity of the light.
	 * @param c
	 * @return this Light
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
	 * @return this Light
	 */
	public Light setColor(float r, float g, float b) {
		color.set(r, g, b);
		return this;
	}

	/**
	 * the color/intensity of this light.
	 * @see #setColor(Color)
	 * @return
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * the direction the light is facing.
	 * if this light is not a spot- or directionallight, this is ignored.
	 * @param x
	 * @param y
	 * @param z
	 * @return this Light
	 */
	public Light setDirection(float x, float y, float z){
		coneinfo.x = x;
		coneinfo.y = y;
		coneinfo.z = z;
		return this;
	}
	
	/**
	 * @see #setDirection(float, float, float)
	 * @param d directionvector
	 * @return this Light
	 */
	public Light setDirection(Vector3f d) {
		return setDirection(d.x, d.y, d.z);
	}
	
	/**
	 * how big the angel in degrees of the spotlight is.
	 * if the light is not a spotlight, this is ignored.
	 * @param d
	 * @return this Light
	 */
	public Light setConeDegrees(float d){
		return setConeRadians(Math.toRadians(d));
	}
	
	/**
	 * how big the angel in radians of the spotlight is.
	 * if the light is not a spotlight, this is ignored.
	 * @param d
	 * @return this Light
	 */
	public Light setConeRadians(double d){
		coneinfo.w = (float) Math.cos(d);
		return this;
	}
	
	/**
	 * coneinfo. only used if this light is a spotlight.
	 * @see #setDirection(float, float, float)
	 * @see #setConeDegrees(float)
	 * @see #setConeRadians(double)
	 * @return conedata
	 */
	public Vector4f getConeInfo(){
		return coneinfo;
	}
	
	/**
	 * sets this light to be a directional light (e.g. sun).
	 * the normalized position of this light is the direction of the light.
	 * @param b
	 * @return this Light
	 */
	public Light setDirectional(boolean b){
		directional = b;
		return this;
	}
	
	/**
	 * sets this light to be a point light. 
	 * modifys {@link #getConeInfo()} and if this light is a directional light.
	 * @return this Light
	 */
	public Light setPointLight(){
		setConeDegrees(180);
		setConeAttenuation(1, 0, 0);
		setDirectional(false);
		return this;
	}

	/**
	 * the attenuation of this light.
	 * @see #setAttenuation(float, float, float)
	 * @see #setCuttOffRange(float)
	 * @return attenuationdata
	 */
	public Vector4f getAttenuation() {
		return att;
	}

	/**
	 * how much the light attenuates into the distance (only for point- and spotlights).
	 * distance: value between 0 and infinity.<br>
	 * <code>attenuation = 1.0 / (a + b * distance + c * distance * distance)</code>
	 * <br>
	 * higher values mean less light.
	 * @param a
	 * @param b
	 * @param c
	 * @return this Light
	 */
	public Light setAttenuation(float a, float b, float c) {
		att.x = a;
		att.y = b;
		att.z = c;
		return this;
	}
	
	/**
	 * the maximum range the light can shine or <code>NO_CUTOFF_RANGE</code> to not cutoff the light.
	 * this does not affect the attenuation of this light.
	 * @param r
	 * @return this Light
	 */
	public Light setCuttOffRange(float r){
		att.w = r;
		return this;
	}
	
	/**
	 * returns if this light is a directional light.
	 * @see #setDirectional(boolean)
	 * @return
	 */
	public final boolean isDirectional() {
		return directional;
	}
}
