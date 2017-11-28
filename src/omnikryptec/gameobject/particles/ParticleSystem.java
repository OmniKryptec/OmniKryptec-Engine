package omnikryptec.gameobject.particles;

import java.util.Random;

import org.joml.Vector3f;

import omnikryptec.gameobject.GameObject3D;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.util.Maths;

public class ParticleSystem extends GameObject3D{
	
	/**
	 * The particlesystem will emit the particles/sec amount of particles in one tick and after that it will die. The system maybe can be resetted.
	 * Maybe not supported by all ParticleSystems 
	 */
	public static final float LIFELENGTH_SYSTEM_ONETICKBURST = -3;
	
	/**
	 * Maybe not supported by all ParticleSystems.
	 */
	public static final float LIFELENGTH_NEVER_DIE=-2;
	/**
	 * Maybe not supported by all ParticleSystems.
	 */
	public static final float LIFELENGTH_DIE_ONLY_IN_ATTRACTOR=-1;
	
	protected float timemultiplier=1;
	protected Random random = new Random();
	
	public ParticleSystem setTimeMultiplier(float f){
		this.timemultiplier = f;
		return this;
	}
	
	public float getTimeMultiplier(){
		return timemultiplier;
	}
	
	protected float getScaledDeltatime(){
		return OmniKryptecEngine.instance().getDeltaTimef()*getTimeMultiplier();
	}
	
	protected Vector3f generateRandomUnitVectorWithinCone(Vector3f coneDirection, double coneangle) {
		return Maths.generateRandomUnitVectorWithinCone(random, coneDirection, coneangle);
	}

	protected Vector3f generateRandomUnitVector() {
		return Maths.generateRandomUnitVector(random);
	}
	
	protected float getErroredValue(float average, float errorMargin) {
		return Maths.getErroredValue(random, average, errorMargin);
	}
}
