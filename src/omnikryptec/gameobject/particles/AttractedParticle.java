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
        timemultiplier = system.getTimeMultiplier() * DisplayManager.instance().getDeltaTimef();
        elapsedTime += timemultiplier;
        final Vector3f acceleration = new Vector3f(0, 0, 0);
        final Vector3f acceleration_temp = new Vector3f(0, 0, 0);

        for (Float[] attractorData : system.getAttractorData()) {
            if (attractorData[6] == 0.0F) {
                acceleration_temp.x = (attractorData[0] - position.x);
                acceleration_temp.y = (attractorData[1] - position.y);
                acceleration_temp.z = (attractorData[2] - position.z);
            } else if (attractorData[6] == 1.0F) {
                acceleration_temp.x = attractorData[0];
                acceleration_temp.y = attractorData[1];
                acceleration_temp.z = attractorData[2];
            }
            if ((attractorData[5] == 1.0F) && (acceleration_temp.lengthSquared() <= (attractorData[4] * attractorData[4]))) {
                elapsedTime = lifeLength;
                break;
            } else {
                acceleration_temp.normalize();
                acceleration_temp.mul(attractorData[3]);
                acceleration.add(acceleration_temp);
            }
        }
        if (elapsedTime != lifeLength) {
            velocity.add(acceleration.mul(timemultiplier, changeable));
            position.add(velocity.mul(timemultiplier, changeable));
        }
        /*
        velocity.add(acceleration.mul(timemultiplier, changeable));
        position.add(velocity.mul(timemultiplier, changeable));
        if (system.getAttractor() != null && system.getAttractorData() != null) {
            changeable = system.getAttractor().getAbsolutePos().sub(position, changeable);
            if (changeable.lengthSquared() > system.getAttractorData().w * system.getAttractorData().w) {
                changeable.normalize().mul(system.getAttractorData().x * timemultiplier + system.getAttractorData().y * elapsedTime * elapsedTime * 0.5f);
                position.add(changeable);
            } else if (system.getAttractorData().z == 1) {
                elapsedTime = lifeLength;
            }
        }
         */
        return lifeLength == -1 || elapsedTime < lifeLength;
    }

}
