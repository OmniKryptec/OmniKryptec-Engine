package omnikryptec.gameobject.particles;

import omnikryptec.gameobject.gameobject.Entity.RenderType;
import omnikryptec.gameobject.particles.ParticleAttractor.AttractorMode;
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
    
    
    public AttractedParticle(ParticleAtlas tex, Vector3f pos, Vector3f vel, float lifeLength, float rot, float scale, AttractedPaticleSystem system, RenderType type) {
        super(pos, tex, type);
        setRotation(rot);
        setScale(scale);
        this.velocity = vel;
        this.lifeLength = lifeLength;
        this.elapsedTime = 0;
        this.system = system;
    }

    public AttractedPaticleSystem getSystem() {
        return system;
    }

    @Override
    protected float getLifeFactor() {
        return lifeLength <= -1 ? -1 : elapsedTime / lifeLength;
    }

    private static float timemultiplier;
    private static Vector3f changeable = new Vector3f();
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
	        outer:
        	for (ParticleAttractor attractorData : system.getAttractorData()) {
	        	if(!attractorData.isEnabled()){
	        		continue;
	        	}
	        	changeable = attractorData.getAbsolutePos();
	        	allowMovementThisFrame = true;
	        	if (!attractorData.isInfinite()) {
	            	direction.set(changeable).sub(position);
	            } else {
	                direction.set(changeable);
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
	                direction.normalize();
	                direction.mul(attractorData.getAcceleration());
	                acceleration.add(direction);
	        	}
	        }
	        if (disallowing_mov<=0&&elapsedTime != lifeLength) {
	            velocity.add(acceleration.mul(timemultiplier, changeable));
	            position.add(velocity.mul(timemultiplier, changeable));
	        }
        }
        return lifeLength == -2 || (lifeLength == -1 && living) || (elapsedTime < lifeLength && living);
    }

}
