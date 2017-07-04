package omnikryptec.gameobject.particles;

import org.joml.Vector3f;

import omnikryptec.gameobject.gameobject.GameObject;

public class ParticleAttractor {
	
	
	protected float acceleration=0, tolerance=0;
	protected boolean infinite=false;
	protected GameObject positionable;
	protected AttractorMode mode = AttractorMode.NOTHING;
	protected boolean enabled=true;
	
	public ParticleAttractor(GameObject p){
		this.positionable = p;
	}
	
	public ParticleAttractor(float x, float y, float z){
		this.positionable = new GameObject().setRelativePos(x, y, z);
	}
	
	public float getAcceleration() {
		return acceleration;
	}
	
	public ParticleAttractor setAcceleration(float acceleration) {
		this.acceleration = acceleration;
		return this;
	}
	
	public float getTolerance() {
		return tolerance;
	}
	
	public ParticleAttractor setTolerance(float tolerance) {
		this.tolerance = tolerance;
		return this;
	}
	
	public AttractorMode getMode(){
		return this.mode;
	}
	
	public ParticleAttractor setMode(AttractorMode mode) {
		this.mode = mode;
		return this;
	}
	
	public boolean isInfinite() {
		return infinite;
	}
	
	public ParticleAttractor setInfinite(boolean infinite) {
		this.infinite = infinite;
		return this;
	}
	
	public GameObject getAttractor(){
		return positionable;
	}
	
	public ParticleAttractor setAttractor(GameObject go){
		this.positionable = go;
		return this;
	}
	
	public Vector3f getAbsolutePos(){
		return positionable.getAbsolutePos();
	}
	
	public ParticleAttractor setEnabled(boolean b){
		this.enabled = b;
		return this;
	}
	
	public boolean isEnabled(){
		return this.enabled;
	}
	
}
