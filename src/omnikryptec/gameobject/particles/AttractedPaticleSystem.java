package omnikryptec.gameobject.particles;

import java.util.LinkedList;
import omnikryptec.gameobject.gameobject.Entity;
import omnikryptec.gameobject.gameobject.GameObject;
import omnikryptec.gameobject.particles.ParticleAttractor.AttractorMode;
import omnikryptec.resource.texture.ParticleAtlas;
import org.joml.Vector3f;

/**
 *
 * @author Panzer1119 &amp; pcfreak9000
 */
public class AttractedPaticleSystem extends SimpleParticleSystem {

    protected final LinkedList<ParticleAttractor> attractorData = new LinkedList<>();

    public AttractedPaticleSystem(Vector3f pos, ParticleAtlas tex, float pps, float startspeed, float lifeLength, float scale, Entity.RenderType type) {
        this(pos.x, pos.y, pos.z, tex, pps, startspeed, lifeLength, scale, type);
    }

    public AttractedPaticleSystem(float x, float y, float z, ParticleAtlas tex, float pps, float startspeed, float lifeLength, float scale, Entity.RenderType type) {
        this.type = type;
        this.particlepersec = pps;
        this.averageSpeed = startspeed;
        this.averageLifeLength = lifeLength;
        this.averageScale = scale;
        this.particletexture = tex;
        setRelativePos(x, y, z);
    }
    
    public AttractedPaticleSystem addAttractor(float x, float y, float z, float a, float t) {
        return addAttractor(x, y, z, a, t, AttractorMode.NOTHING);
    }
    
    public AttractedPaticleSystem addAttractor(float x, float y, float z, float a, float t, AttractorMode d) {
        return addAttractor(x, y, z, a, t, d, false);
    }
    
    public AttractedPaticleSystem addAttractor(Vector3f pos, float a, float t) {
        return addAttractor(pos, a, t, AttractorMode.NOTHING);
    }
    
    public AttractedPaticleSystem addAttractor(Vector3f pos, float a, float t, AttractorMode d) {
        return addAttractor(pos.x, pos.y, pos.z, a, t, d, false);
    }

    public AttractedPaticleSystem addAttractor(GameObject go, float a, float t) {
        return addAttractor(go, a, t, AttractorMode.NOTHING);
    }
    
    public AttractedPaticleSystem addAttractor(GameObject go, float a, float t, AttractorMode d) {
        return addAttractor(go, a, t, d, false);
    }
    
    public AttractedPaticleSystem addAttractor(float x, float y, float z, float a, float t, AttractorMode mode, boolean i) {
    	return addAttractor(new ParticleAttractor(x,y,z).setAcceleration(a).setTolerance(t).setMode(mode).setInfinite(i));
    }
    
    public AttractedPaticleSystem addAttractor(GameObject go, float a, float t, AttractorMode mode, boolean i) {
    	return addAttractor(new ParticleAttractor(go).setAcceleration(a).setTolerance(t).setMode(mode).setInfinite(i));
    }
    
    public AttractedPaticleSystem addAttractor(ParticleAttractor atr){
    	this.attractorData.add(atr);
    	return this;
    }

    public LinkedList<ParticleAttractor> getAttractorData() {
        return attractorData;
    }

    private static Vector3f velocity;
    private static float scale, lifeLength;

    @Override
    protected Particle emitParticle(Vector3f center) {
        if (direction != null) {
            velocity = generateRandomUnitVectorWithinCone(direction, directionAngel);
        } else {
            velocity = generateRandomUnitVector();
        }
        velocity.mul(getErroredValue(averageSpeed, speedError));
        scale = getErroredValue(averageScale, scaleError);
        lifeLength = averageLifeLength <= -1 ? averageLifeLength : getErroredValue(averageLifeLength, lifeError);
        return new AttractedParticle(particletexture, calcNewSpawnPos(center), velocity, lifeLength, generateRotation(), scale, this, type);
    }

}
