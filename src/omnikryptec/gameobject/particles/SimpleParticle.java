package omnikryptec.gameobject.particles;

import org.joml.Vector3f;

import omnikryptec.display.DisplayManager;
import omnikryptec.gameobject.gameobject.Entity.RenderType;
import omnikryptec.resource.texture.ParticleAtlas;

public class SimpleParticle extends Particle {

	protected Vector3f velocity;
	protected Vector3f force;
	protected float lifeLength;
	protected float elapsedTime = 0;
	private SimpleParticleSystem mysystem;


	public SimpleParticle(ParticleAtlas tex, Vector3f pos, Vector3f vel, Vector3f force, float lifeLength, float rot,
			float scale, SimpleParticleSystem sys, RenderType type) {
		super(pos,tex,type);
		setRotation(rot);
		setScale(scale);
		this.velocity = vel;
		this.force = force;
		this.lifeLength = lifeLength;
		this.elapsedTime = 0;
		this.mysystem = sys;
	}

	public SimpleParticleSystem getSystem() {
		return mysystem;
	}

	@Override
	protected float getLifeFactor(){
		return lifeLength==-1?-1:elapsedTime/lifeLength;
	}

	private static float timemultiplier;
	private static Vector3f changeable = new Vector3f(), changeable2 = new Vector3f();
	@Override
	protected boolean update() {
		timemultiplier = mysystem.getTimeMultiplier() * DisplayManager.instance().getDeltaTimef();
		changeable = velocity.mul(timemultiplier, changeable);
		changeable2 = force.mul(timemultiplier*timemultiplier, changeable2).mul(0.5f);
		System.out.println(changeable+"|"+changeable2+"|"+timemultiplier);
		changeable = changeable.add(changeable2);
		position.add(changeable);
		elapsedTime += timemultiplier;
		return lifeLength==-1||elapsedTime < lifeLength;
	}

}
