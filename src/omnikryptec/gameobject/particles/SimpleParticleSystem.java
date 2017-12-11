package omnikryptec.gameobject.particles;

import org.joml.Vector3f;

import omnikryptec.gameobject.particles.ParticleSpawnArea.ParticleSpawnAreaType;
import omnikryptec.resource.texture.ParticleAtlas;
import omnikryptec.util.Color;
import omnikryptec.util.EnumCollection.RenderType;
import omnikryptec.util.Maths;

public class SimpleParticleSystem extends ParticleSystem {

    protected float particlepersec, averageSpeed, averageLifeLength, averageStartScale, averageEndScale;

    protected Color startcolor = new Color(1, 1, 1, 1);
    protected Color endcolor = new Color(1, 1, 1, 1);

    protected float speedError, lifeError, startScaleError = 0, endScaleError = 0;
    protected boolean randomRotation = false;

    /**
     * direction can be null
     */
    protected Vector3f direction, force;
    protected double directionAngel = 0;
    protected RenderType type = RenderType.MEDIUM;

    protected float lifelengthsystem = -1;
    protected float elapsedtime = 0;

    protected ParticleAtlas particletexture;
    protected ParticleSpawnArea spawnarea = new ParticleSpawnArea(ParticleSpawnAreaType.POINT, 0);

    protected boolean wasonetickburst = false;
    protected Vector3f[] vecs;

    protected SimpleParticleSystem() {
    }

    public SimpleParticleSystem(Vector3f pos, ParticleAtlas tex, float pps, float speed, float lifeLength,
            RenderType type) {
        this(pos, tex, pps, speed, lifeLength, 1, type);
    }

    public SimpleParticleSystem(Vector3f pos, ParticleAtlas tex, float pps, float speed, float lifeLength, float scale,
            RenderType type) {
        this(pos.x, pos.y, pos.z, tex, pps, speed, lifeLength, scale, type);
    }

    public SimpleParticleSystem(Vector3f pos, ParticleAtlas tex, float pps, float speed, float lifeLength, float scale,
            float endscale, RenderType type) {
        this(pos.x, pos.y, pos.z, tex, pps, speed, lifeLength, scale, endscale, type);
    }

    public SimpleParticleSystem(float x, float y, float z, ParticleAtlas tex, float pps, float speed, float lifeLength,
            float scale, RenderType type) {
        this(x, y, z, tex, Maths.ZERO, pps, speed, lifeLength, scale, scale, type);
    }

    public SimpleParticleSystem(float x, float y, float z, ParticleAtlas tex, float pps, float speed, float lifeLength,
            float scale, float endscale, RenderType type) {
        this(x, y, z, tex, Maths.ZERO, pps, speed, lifeLength, scale, endscale, type);
    }

    public SimpleParticleSystem(float x, float y, float z, ParticleAtlas tex, Vector3f gravityComplient, float pps,
            float speed, float lifeLength, float startscale, float endscale, RenderType type) {
        this.type = type;
        this.particlepersec = pps;
        this.averageSpeed = speed;
        this.force = gravityComplient;
        this.averageLifeLength = lifeLength;
        this.averageStartScale = scale;
        this.particletexture = tex;
        getTransform().setPosition(x, y, z);
    }

    public SimpleParticleSystem(Vector3f pos, ParticleAtlas tex, Vector3f gravityComplient, float pps, float speed,
            float lifeLength, float scale, float endscale, RenderType type) {
        this(pos.x, pos.y, pos.z, tex, gravityComplient, pps, speed, lifeLength, scale, endscale, type);
    }

    public SimpleParticleSystem(Vector3f pos, ParticleAtlas tex, Vector3f gravityComplient, float pps, float speed,
            float lifeLength, float scale, RenderType type) {
        this(pos.x, pos.y, pos.z, tex, gravityComplient, pps, speed, lifeLength, scale, scale, type);
    }

    public SimpleParticleSystem setSystemLifeLength(float f) {
        this.lifelengthsystem = f;
        return this;
    }

    /**
     * @param direction - The average direction in which particles are emitted.
     * @param angel - A value between 0 and 1 indicating how far from the chosen
     * direction particles can deviate.
     */
    public SimpleParticleSystem setCone(Vector3f direction, double angel) {
        this.direction = new Vector3f(direction);
        this.directionAngel = angel;
        return this;
    }

    public SimpleParticleSystem randomizeRotation(boolean b) {
        randomRotation = b;
        return this;
    }

    public SimpleParticleSystem setSpawnOffsets(Vector3f[] array) {
        this.vecs = array;
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
    public void setStartScaleError(float error) {
        this.startScaleError = error * averageStartScale;
    }

    /**
     * @param error - A number between 0 and 1, where 0 means no error margin.
     */
    public void setEndScaleError(float error) {
        this.endScaleError = error * averageEndScale;
    }

    public void resetTime() {
        elapsedtime = 0;
        if (wasonetickburst) {
            wasonetickburst = false;
            lifelengthsystem = LIFELENGTH_SYSTEM_ONETICKBURST;
        }
    }

    public ParticleSpawnArea getSpawnArea() {
        return spawnarea;
    }

    public SimpleParticleSystem setSpawnArea(ParticleSpawnArea area) {
        this.spawnarea = area;
        return this;
    }

    @Override
    public void update() {
        if (elapsedtime <= lifelengthsystem || lifelengthsystem <= -1) {
            generateParticles(timemultiplier);
            elapsedtime += getScaledDeltatime();
            if (lifelengthsystem == LIFELENGTH_SYSTEM_ONETICKBURST) {
                lifelengthsystem = 0;
                wasonetickburst = true;
            }
        }
    }

    private float particlesToCreate, partialParticle;
    private int count;
    private Vector3f pos;
    /**
     * for <1 particle/sec
     */
    private float lastParticlef = 0;

    public SimpleParticleSystem generateParticles(float timemultiplier) {
        particlesToCreate = particlepersec
                * (lifelengthsystem == LIFELENGTH_SYSTEM_ONETICKBURST ? 1 : getScaledDeltatime());
        if (particlesToCreate < 1f) {
            lastParticlef += particlesToCreate;
            if (lastParticlef >= 1f) {
                particlesToCreate++;
                lastParticlef = 0;
            }
        }
        count = (int) Math.floor(particlesToCreate);
        partialParticle = particlesToCreate % 1;
        if (vecs != null && vecs.length > 0) {
            int rand = random.nextInt(vecs.length + 1);
            pos = getTransform().getPosition(rand >= vecs.length);
            if (rand < vecs.length) {
                pos = pos.add(vecs[rand]);
            }
        } else {
            pos = getTransform().getPosition(true);
        }
        for (int i = 0; i < count; i++) {
            emitAndAdd(pos);
        }
        if (Math.random() < partialParticle) {
            emitAndAdd(pos);
        }
        return this;
    }

    protected void emitAndAdd(Vector3f center) {
        ParticleMaster.instance().addParticle(emitParticle(center));
    }

    private Vector3f velocity;
    private float scale, lifeLength, endscale;

    protected Particle emitParticle(Vector3f center) {
        if (direction != null) {
            velocity = generateRandomUnitVectorWithinCone(direction, directionAngel);
        } else {
            velocity = generateRandomUnitVector();
        }
        velocity.mul(getErroredValue(averageSpeed, speedError));
        scale = getErroredValue(averageStartScale, startScaleError);
        if (averageStartScale == averageEndScale) {
            endscale = scale;
        } else {
            endscale = getErroredValue(averageEndScale, endScaleError);
        }
        lifeLength = averageLifeLength == -1 ? -1 : getErroredValue(averageLifeLength, lifeError);
        return new SimpleParticle(particletexture, calcNewSpawnPos(center), velocity, force, lifeLength,
                generateRotation(), scale, endscale, this, type, startcolor.getArray(), endcolor.getArray());
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
                return Maths.getRandomPointOnLine(random, center,
                        spawnarea.getDirection().mul(spawnarea.getData(), new Vector3f()));
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

    public float getParticlepersec() {
        return particlepersec;
    }

    public SimpleParticleSystem setParticlepersec(float particlepersec) {
        this.particlepersec = particlepersec;
        return this;
    }

    public float getAverageSpeed() {
        return averageSpeed;
    }

    public SimpleParticleSystem setAverageSpeed(float averageSpeed) {
        this.averageSpeed = averageSpeed;
        return this;
    }

    public float getAverageLifeLength() {
        return averageLifeLength;
    }

    public SimpleParticleSystem setAverageLifeLength(float averageLifeLength) {
        this.averageLifeLength = averageLifeLength;
        return this;
    }

    public float getAverageStartScale() {
        return averageStartScale;
    }

    public SimpleParticleSystem setAverageStartScale(float averageStartScale) {
        this.averageStartScale = averageStartScale;
        return this;
    }

    public float getAverageEndScale() {
        return averageEndScale;
    }

    public SimpleParticleSystem setAverageEndScale(float averageEndScale) {
        this.averageEndScale = averageEndScale;
        return this;
    }

    public ParticleAtlas getParticletexture() {
        return particletexture;
    }

    public SimpleParticleSystem setParticletexture(ParticleAtlas particletexture) {
        this.particletexture = particletexture;
        return this;
    }

    public Color getStartcolor() {
        return startcolor;
    }

    public SimpleParticleSystem setStartcolor(Color startcolor) {
        this.startcolor = startcolor;
        return this;
    }

    public Color getEndcolor() {
        return endcolor;
    }

    public SimpleParticleSystem setEndcolor(Color endcolor) {
        this.endcolor = endcolor;
        return this;
    }

}
