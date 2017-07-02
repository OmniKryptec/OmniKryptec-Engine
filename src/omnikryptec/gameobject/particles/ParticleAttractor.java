package omnikryptec.gameobject.particles;

import org.joml.Vector3f;

import omnikryptec.gameobject.gameobject.GameObject;

public class ParticleAttractor {
	
	protected float acceleration=0, tolerance=0;
	protected boolean dieOnReach=false, infinite=true;
	protected GameObject positionable;
	
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
	
	public boolean isDieOnReach() {
		return dieOnReach;
	}
	
	public ParticleAttractor setDieOnReach(boolean dieOnReach) {
		this.dieOnReach = dieOnReach;
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
	
	public Vector3f getAbsolutePos(){
		return positionable.getAbsolutePos();
	}
	
	
}
