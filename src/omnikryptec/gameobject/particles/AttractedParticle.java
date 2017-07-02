package omnikryptec.gameobject.particles;

import omnikryptec.display.DisplayManager;
import omnikryptec.gameobject.gameobject.Entity.RenderType;
import omnikryptec.resource.texture.ParticleAtlas;
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
        return lifeLength == -1 ? -1 : elapsedTime / lifeLength;
    }

    private static float timemultiplier;
    private static Vector3f changeable = new Vector3f();

    @Override
    protected boolean update() {
        timemultiplier = system.getScaledDeltatime();
        elapsedTime += timemultiplier;
        final Vector3f acceleration = new Vector3f(0, 0, 0);
        final Vector3f acceleration_temp = new Vector3f(0, 0, 0);
        for (ParticleAttractor attractorData : system.getAttractorData()) {
        	changeable = attractorData.getAbsolutePos();
            if (!attractorData.isInfinite()) {
            	acceleration_temp.set(changeable).sub(position);
            } else {
                acceleration_temp.set(changeable);
            }
            if ((attractorData.isDieOnReach()) && (acceleration_temp.lengthSquared() <= (attractorData.getTolerance()*attractorData.getTolerance()))) {
                elapsedTime = lifeLength;
                break;
            } else {
                acceleration_temp.normalize();
                acceleration_temp.mul(attractorData.getAcceleration());
                acceleration.add(acceleration_temp);
            }
        }
        if (elapsedTime != lifeLength) {
            velocity.add(acceleration.mul(timemultiplier, changeable));
            position.add(velocity.mul(timemultiplier, changeable));
        }
        return lifeLength == -1 || elapsedTime < lifeLength;
    }

}
