package omnikryptec.gameobject.particles;

import omnikryptec.gameobject.gameobject.GameObject;

public class ParticleAttractor extends GameObject{
	
	protected float acceleration=0, tolerance=0;
	protected boolean dieOnReach=false, infinite=true;
	
	public ParticleAttractor(){
	}
	
	public ParticleAttractor(float x, float y, float z){
		super.setRelativePos(x, y, z);
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
	
	
	
}
