package omnikryptec.gameobject.particles;

import org.joml.Vector3f;

import omnikryptec.display.DisplayManager;
import omnikryptec.gameobject.gameobject.Entity.RenderType;
import omnikryptec.resource.texture.ParticleAtlas;

public class SimpleParticle extends Particle {

	protected Vector3f velocity;
	protected Vector3f acceleration;
	protected float lifeLength;
	protected float elapsedTime = 0;
	private SimpleParticleSystem mysystem;


	public SimpleParticle(ParticleAtlas tex, Vector3f pos, Vector3f vel, Vector3f force, float lifeLength, float rot,
			float scale, SimpleParticleSystem sys, RenderType type) {
		super(pos,tex,type);
		setRotation(rot);
		setScale(scale);
		this.velocity = vel;
		this.acceleration = force;
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
	private static Vector3f changeable = new Vector3f();
	@Override
	protected boolean update() {
		timemultiplier = mysystem.getTimeMultiplier() * DisplayManager.instance().getDeltaTimef();
		elapsedTime += timemultiplier;
		velocity.add(acceleration.mul(timemultiplier, changeable));
		position.add(velocity.mul(timemultiplier, changeable));
		if(mysystem.getAttractor()!=null&&mysystem.getAttractorData()!=null){
			changeable = mysystem.getAttractor().getAbsolutePos().sub(position, changeable);
			if(changeable.lengthSquared()>mysystem.getAttractorData().w*mysystem.getAttractorData().w){
				changeable.normalize().mul(mysystem.getAttractorData().x*timemultiplier+mysystem.getAttractorData().y*elapsedTime*elapsedTime*0.5f);
				position.add(changeable);
			}else{
				if(mysystem.getAttractorData().z==1){
					elapsedTime = lifeLength;
				}
			}
		}
		return lifeLength==-1||elapsedTime < lifeLength;
	}

}
