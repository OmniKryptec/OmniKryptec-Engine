package omnikryptec.gameobject.particles;

import omnikryptec.gameobject.gameobject.RenderType;
import omnikryptec.resource.texture.ParticleAtlas;

import java.util.ArrayList;

import org.joml.Vector3f;

/**
 *
 * @author Panzer1119 &amp; pcfreak9000
 */
public class AttractedParticle extends Particle {

    protected Vector3f velocity;
    protected float lifeLength;
    protected float elapsedTime = 0;
    private final AttractedPaticleSystem system;
    private boolean living=true;
    private float startscale=1,endscale=1;
    private float[] color1 = new float[]{1,1,1,1};
	private float[] color2 = new float[]{1,1,1,1};
    
    
    public AttractedParticle(ParticleAtlas tex, Vector3f pos, Vector3f vel, float lifeLength, float rot, float scale, AttractedPaticleSystem system, RenderType type, float[] startcolor, float[] endcolor) {
    	this(tex,pos,vel,lifeLength,rot,scale,scale,system,type,startcolor,endcolor);
    }

    
    public AttractedParticle(ParticleAtlas tex, Vector3f pos, Vector3f vel, float lifeLength, float rot, float startscale, float endscale, AttractedPaticleSystem system, RenderType type, float[] startcolor, float[] endcolor) {
        super(pos, tex, type);
        setRotation(rot);
        this.velocity = vel;
        this.lifeLength = lifeLength;
        this.elapsedTime = 0;
        this.system = system;
        this.startscale = startscale;
        this.endscale = endscale;
        this.color1 = startcolor;
        this.color2 = endcolor;
		color.setFrom(startcolor);
        setScale(startscale);
    }

    public AttractedPaticleSystem getSystem() {
        return system;
    }

    @Override
    protected float getLifeFactor() {
        return lifeLength <= -1 ? -1 : elapsedTime / lifeLength;
    }

    private static float timemultiplier, lf;
    private static Vector3f changeable = new Vector3f(), att;
    private static boolean allowMovementThisFrame=false;
    
    
    private int disallowing_mov=0;
    private ArrayList<ParticleAttractor> disabler;
    @Override
    protected boolean update() {
    	timemultiplier = system.getScaledDeltatime();
        elapsedTime += timemultiplier;
        if(disabler!=null){
        	for(ParticleAttractor atr : system.getAttractorData()){
        		if(!atr.isEnabled()&&disabler.contains(atr)){
        			disallowing_mov--;
        			disabler.remove(atr);
        			if(disabler.size()==0){
        				disabler=null;
        				break;
        			}
        		}
        	}
        }
        if(disallowing_mov<=0){
        	final Vector3f acceleration = new Vector3f(0, 0, 0);
        	final Vector3f direction = new Vector3f(0, 0, 0);
        	boolean b = false;
        	float ddd = 0, attenu=1;
        	outer:
        	for (ParticleAttractor attractorData : system.getAttractorData()) {
	        	if(!attractorData.isEnabled()){
	        		continue;
	        	}
	        	changeable = attractorData.getAbsolutePos();
	        	allowMovementThisFrame = true;
	        	if (!attractorData.isInfinite()) {
	            	direction.set(changeable).sub(position);
	            	b = true;
	            } else {
	                direction.set(changeable);
	                b = false;
	            }
	            if((direction.lengthSquared() <= (attractorData.getTolerance()*attractorData.getTolerance()))){
		            switch(attractorData.getMode()){
					case KILL_ON_REACH:
						living = false;
						break outer;
					case NOTHING:
						break;
					case STOP_FOREVER_ON_REACH:
						disallowing_mov++;
						velocity.set(0);
						break outer;
					case STOP_ON_REACH:
						allowMovementThisFrame = false;
						velocity.set(0);
						break outer;
					case STOP_UNTIL_DISABLED_ON_REACH:
						disallowing_mov++;
						velocity.set(0);
						if(disabler==null){
							disabler=new ArrayList<>(1);
						}
						disabler.add(attractorData);
						break outer;
					default:
						break;
		            }
	            }
	        	if(attractorData.getMode()==AttractorMode.NOTHING||allowMovementThisFrame){
	                if(b){
	                	att = attractorData.getAttenuation();
	                	if(att.y!=0||att.z!=0){
	                		ddd = direction.length();
	                	}
	                	attenu = att.x + att.y * ddd + att.z * ddd * ddd;
	                	attenu = 1/attenu;
	                }
	        		direction.normalize();
	                direction.mul(attractorData.getAcceleration()*attenu);
	                acceleration.add(direction);
	        	}
	        }
	        if (disallowing_mov<=0&&elapsedTime != lifeLength) {
	            velocity.add(acceleration.mul(timemultiplier, changeable));
	            position.add(velocity.mul(timemultiplier, changeable));
	        }
        }
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
        return lifeLength == -2 || (lifeLength == -1 && living) || (elapsedTime < lifeLength && living);
    }

}
