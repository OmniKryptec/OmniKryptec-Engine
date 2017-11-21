package omnikryptec.gameobject.particles;

import org.joml.Vector3f;

import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.resource.texture.ParticleAtlas;
import omnikryptec.util.EnumCollection.RenderType;

public class SimpleParticle extends Particle {

	protected Vector3f velocity;
	protected Vector3f acceleration;
	protected float lifeLength;
	protected float elapsedTime = 0;
	private SimpleParticleSystem mysystem;
	private float startscale=1,endscale=1;
	private float[] color1 = new float[]{1,1,1,1};
	private float[] color2 = new float[]{1,1,1,1};

	
	public SimpleParticle(ParticleAtlas tex, Vector3f pos, Vector3f vel, Vector3f force, float lifeLength, float rot,
			float scale, SimpleParticleSystem sys, RenderType type, float[] startcolor, float endcolor[]) {
		this(tex,pos,vel,force,lifeLength,rot,scale,scale,sys,type, startcolor, endcolor);
	}
	
	public SimpleParticle(ParticleAtlas tex, Vector3f pos, Vector3f vel, Vector3f force, float lifeLength, float rot,
			float startscale, float endscale, SimpleParticleSystem sys, RenderType type, float[] startcolor, float endcolor[]) {
		super(pos,tex,type);
		setRotation(rot);
		this.velocity = vel;
		this.acceleration = force;
		this.lifeLength = lifeLength;
		this.elapsedTime = 0;
		this.mysystem = sys;
		this.startscale = startscale;
		this.endscale = endscale;
		this.color1 = startcolor;
		this.color2 = endcolor;
		color.setFrom(startcolor);
		setScale(startscale);
	}

	public SimpleParticleSystem getSystem() {
		return mysystem;
	}

	@Override
	protected float getLifeFactor(){
		return lifeLength<=-1?-1:elapsedTime/lifeLength;
	}

	private static float timemultiplier, lf;
	private static Vector3f changeable = new Vector3f();
	@Override
	protected boolean update() {
		timemultiplier = (mysystem==null?1:mysystem.getTimeMultiplier()) * OmniKryptecEngine.instance().getDisplayManager().getDeltaTimef();
		elapsedTime += timemultiplier;
		velocity.add(acceleration.mul(timemultiplier, changeable));
		position.add(velocity.mul(timemultiplier, changeable));
		lf = getLifeFactor();
		if(lf<=-1){
			setScale(startscale);
			color.set(color1[0], color1[1],color1[2], color1[3]);
		}else{
			setScale((endscale-startscale)*lf+startscale);
			color.setR((color2[0]-color1[0])*lf+color1[0]);
			color.setG((color2[1]-color1[1])*lf+color1[1]);
			color.setB((color2[2]-color1[2])*lf+color1[2]);
			color.setA((color2[3]-color1[3])*lf+color1[3]);
		}
		return lifeLength<=-1||elapsedTime < lifeLength;
	}

}
