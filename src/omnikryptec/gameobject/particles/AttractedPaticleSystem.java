package omnikryptec.gameobject.particles;

import java.util.ArrayList;
import java.util.LinkedList;
import omnikryptec.display.DisplayManager;
import omnikryptec.gameobject.gameobject.Entity;
import omnikryptec.resource.texture.ParticleAtlas;
import omnikryptec.util.Maths;
import org.joml.Vector3f;

/**
 *
 * @author Panzer1119 &amp; pcfreak9000
 */
public class AttractedPaticleSystem extends ParticleSystem {

    protected float particlepersec, averageSpeed, averageLifeLength, averageScale;

    protected float speedError, lifeError, scaleError = 0;
    protected boolean randomRotation = false;

    protected Vector3f startDirection = null;
    protected double startDirectionAngle = 0;
    private Entity.RenderType type = Entity.RenderType.MEDIUM;

    protected float lifelengthsystem = -1;
    protected float elapsedtime = 0;

    protected ParticleAtlas particletexture;
    /**
     * Index = Meaning;
     * 0 = Pos.x;
     * 1 = Pos.y;
     * 2 = Pos.z;
     * 3 = Acceleration;
     * 4 = Tolerance;
     * 5 = Die on Reach;
     * 6 = Infinite;
     */
    protected final LinkedList<Float[]> attractorData = new LinkedList<>();
    protected ParticleSpawnArea spawnarea = new ParticleSpawnArea(ParticleSpawnArea.ParticleSpawnAreaType.POINT, 0);

    public AttractedPaticleSystem(Vector3f pos, ParticleAtlas tex, float pps, float speed, float lifeLength, float scale, Entity.RenderType type) {
        this(pos.x, pos.y, pos.z, tex, pps, speed, lifeLength, scale, type);
    }

    public AttractedPaticleSystem(float x, float y, float z, ParticleAtlas tex, float pps, float speed, float lifeLength, float scale, Entity.RenderType type) {
        this.type = type;
        this.particlepersec = pps;
        this.averageSpeed = speed;
        this.averageLifeLength = lifeLength;
        this.averageScale = scale;
        this.particletexture = tex;
        setRelativePos(x, y, z);
    }

    public AttractedPaticleSystem setSystemLifeLength(float f) {
        this.lifelengthsystem = f;
        return this;
    }

    public AttractedPaticleSystem randomizeRotation(boolean b) {
        randomRotation = b;
        return this;
    }

    /**
     * @param error - A number between 0 and 1, where 0 means no error margin.
     */
    public void setSpeedError(float error) {
        this.speedError = error * averageSpeed;
    }

    /**
     * @param error - A number between 0 and 1, where 0 means no error margin.
     */
    public void setLifeError(float error) {
        this.lifeError = error * averageLifeLength;
    }

    /**
     * @param error - A number between 0 and 1, where 0 means no error margin.
     */
    public void setScaleError(float error) {
        this.scaleError = error * averageScale;
    }

    public void resetTime() {
        elapsedtime = 0;
    }

    public ParticleSpawnArea getSpawnArea() {
        return spawnarea;
    }

    public AttractedPaticleSystem setSpawnArea(ParticleSpawnArea area) {
        this.spawnarea = area;
        return this;
    }

    public Vector3f getStartDirection() {
        return startDirection;
    }

    public AttractedPaticleSystem setStartDirection(Vector3f startDirection) {
        this.startDirection = startDirection;
        return this;
    }

    public double getStartDirectionAngle() {
        return startDirectionAngle;
    }

    public AttractedPaticleSystem setStartDirectionAngle(double startDirectionAngle) {
        this.startDirectionAngle = startDirectionAngle;
        return this;
    }
    
    public AttractedPaticleSystem addAttractor(float x, float y, float z, float a, float t) {
        return addAttractor(x, y, z, a, t, false);
    }
    
    public AttractedPaticleSystem addAttractor(float x, float y, float z, float a, float t, boolean d) {
        return addAttractor(x, y, z, a, t, d, false);
    }
    
    public AttractedPaticleSystem addAttractor(Vector3f pos, float a, float t) {
        return addAttractor(pos, a, t, false);
    }
    
    public AttractedPaticleSystem addAttractor(Vector3f pos, float a, float t, boolean d) {
        return addAttractor(pos.x, pos.y, pos.z, a, t, d, false);
    }

    public AttractedPaticleSystem addAttractor(float x, float y, float z, float a, float t, boolean d, boolean i) {
        this.attractorData.add(new Float[] {x, y, z, a, t, (d ? 1.0F : 0.0F), (i ? 1.0F : 0.0F)});
        return this;
    }

    /**
     * Index = Meaning;
     * 0 = Pos.x;
     * 1 = Pos.y;
     * 2 = Pos.z;
     * 3 = Acceleration;
     * 4 = Tolerance;
     * 5 = Die on Reach;
     * 6 = Infinite;
     */
    public LinkedList<Float[]> getAttractorData() {
        return attractorData;
    }

    @Override
    public void update() {
        if (elapsedtime <= lifelengthsystem || lifelengthsystem < 0) {
            generateParticles(timemultiplier);
            elapsedtime += DisplayManager.instance().getDeltaTimef() * timemultiplier;
        }
    }

    private static float delta, particlesToCreate, partialParticle;
    private static int count;
    private static Vector3f pos;
    /**
     * for <1 particle/sec
     */
    private float lastParticlef = 0;

    public AttractedPaticleSystem generateParticles(float timemultiplier) {
        delta = DisplayManager.instance().getDeltaTimef() * timemultiplier;
        particlesToCreate = particlepersec * delta;
        if (particlesToCreate < 1f) {
            lastParticlef += particlesToCreate;
            if (lastParticlef >= 1f) {
                particlesToCreate++;
                lastParticlef = 0;
            }
        }
        count = (int) Math.floor(particlesToCreate);
        partialParticle = particlesToCreate % 1;
        pos = getAbsolutePos();
        for (int i = 0; i < count; i++) {
            emitAndAdd(pos);
        }
        if (Math.random() < partialParticle) {
            emitAndAdd(pos);
        }
        return this;
    }

    private void emitAndAdd(Vector3f center) {
        ParticleMaster.instance().addParticle(emitParticle(center));
    }

    private static Vector3f velocity;
    private static float scale, lifeLength;

    protected Particle emitParticle(Vector3f center) {
        if (startDirection != null) {
            velocity = generateRandomUnitVectorWithinCone(startDirection, startDirectionAngle);
        } else {
            velocity = generateRandomUnitVector();
        }
        velocity.mul(getErroredValue(averageSpeed, speedError));
        scale = getErroredValue(averageScale, scaleError);
        lifeLength = averageLifeLength == -1 ? -1 : getErroredValue(averageLifeLength, lifeError);
        return new AttractedParticle(particletexture, calcNewSpawnPos(center), velocity, lifeLength, generateRotation(), scale, this, type);
    }

    protected Vector3f calcNewSpawnPos(Vector3f center) {
        switch (spawnarea.getType()) {
            case CIRCLE:
                return Maths.getRandomPointInCircle(random, center, spawnarea.getData(), spawnarea.getDirection());
            case LINE:
                return Maths.getRandomPointOnLine(random, spawnarea.getDirection(), center, spawnarea.getData());
            case POINT:
                return new Vector3f(center);
            case SHPERE:
                return Maths.getRandomPointInSphere(random, center, spawnarea.getData());
            case DIRECTION:
                return Maths.getRandomPointOnLine(random, center, spawnarea.getDirection().mul(spawnarea.getData(), new Vector3f()));
            default:
                return new Vector3f(center);
        }
    }

    protected float generateRotation() {
        if (randomRotation) {
            return random.nextFloat() * 360f;
        } else {
            return 0;
        }
    }

}
