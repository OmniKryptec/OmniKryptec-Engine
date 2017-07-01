package omnikryptec.gameobject.particles;

import org.joml.Vector3f;

import omnikryptec.display.DisplayManager;
import omnikryptec.gameobject.gameobject.Entity.RenderType;

public class SimpleParticle extends Particle {

	private Vector3f vel;
	private Vector3f force;
	private float lifeLength;
	private float elapsedTime = 0;
	private ParticleSystem mysystem;


	public SimpleParticle(ParticleTexture tex, Vector3f pos, Vector3f vel, Vector3f force, float lifeLength, float rot,
			float scale, ParticleSystem sys, RenderType type) {
		super(pos,tex,type);
		setRotation(rot);
		setScale(scale);
		this.vel = vel;
		this.force = force;
		this.lifeLength = lifeLength;
		this.elapsedTime = 0;
		this.mysystem = sys;
	}

	public ParticleSystem getSystem() {
		return mysystem;
	}

	@Override
	protected float getLifeFactor(){
		return elapsedTime/lifeLength;
	}

	private static float timemultiplier;
	private static Vector3f changeable = new Vector3f();
	@Override
	protected boolean update() {
		timemultiplier = mysystem.getTimemultiplier() * DisplayManager.instance().getDeltaTimef();
		changeable = vel.add(force.mul(timemultiplier, changeable));
		getAbsolutePos().add(changeable);
		elapsedTime += timemultiplier;
		return elapsedTime < lifeLength;
	}

}
