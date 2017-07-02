package omnikryptec.gameobject.particles;

import org.joml.Vector3f;
import omnikryptec.display.DisplayManager;
import omnikryptec.resource.texture.ParticleAtlas;
import omnikryptec.gameobject.gameobject.Entity.RenderType;
import omnikryptec.gameobject.particles.ParticleSpawnArea.ParticleSpawnAreaType;
import omnikryptec.util.Maths;

public class SimpleParticleSystem extends ParticleSystem {

	protected float particlepersec, averageSpeed, averageLifeLength, averageScale;

	protected float speedError, lifeError, scaleError = 0;
	protected boolean randomRotation = false;
	
	/**
	 * direction can be null
	 */
	protected Vector3f direction, force;
	protected double directionAngel = 0;
	private RenderType type = RenderType.MEDIUM;

	protected float lifelengthsystem = -1;
	protected float elapsedtime = 0;

	protected ParticleAtlas particletexture;
	protected ParticleSpawnArea spawnarea = new ParticleSpawnArea(ParticleSpawnAreaType.POINT, 0);
	
	public SimpleParticleSystem(Vector3f pos, ParticleAtlas tex, float pps, float speed, float lifeLength, float scale,
			RenderType type) {
		this(pos.x, pos.y, pos.z, tex, pps, speed, lifeLength, scale, type);
	}

	public SimpleParticleSystem(float x, float y, float z, ParticleAtlas tex, float pps, float speed, float lifeLength,
			float scale, RenderType type) {
		this(x, y, z, tex, Maths.ZERO, pps, speed, lifeLength, scale, type);
	}

	public SimpleParticleSystem(float x, float y, float z, ParticleAtlas tex, Vector3f gravityComplient, float pps, float speed,
			float lifeLength, float scale, RenderType type) {
		this.type = type;
		this.particlepersec = pps;
		this.averageSpeed = speed;
		this.force = gravityComplient;
		this.averageLifeLength = lifeLength;
		this.averageScale = scale;
		this.particletexture = tex;
		setRelativePos(x, y, z);
	}

	public SimpleParticleSystem(Vector3f pos, ParticleAtlas tex, Vector3f gravityComplient, float pps, float speed,
			float lifeLength, float scale, RenderType type) {
		this(pos.x, pos.y, pos.z, tex, gravityComplient, pps, speed, lifeLength, scale, type);
	}

	public SimpleParticleSystem setSystemLifeLength(float f) {
		this.lifelengthsystem = f;
		return this;
	}

	/**
	 * @param direction
	 *            - The average direction in which particles are emitted.
	 * @param deviation
	 *            - A value between 0 and 1 indicating how far from the chosen
	 *            direction particles can deviate.
	 */
	public SimpleParticleSystem setDirection(Vector3f direction, double angel) {
		this.direction = new Vector3f(direction);
		this.directionAngel = angel;
		return this;
	}

	public SimpleParticleSystem randomizeRotation(boolean b) {
		randomRotation = b;
		return this;
	}

	/**
	 * @param error
	 *            - A number between 0 and 1, where 0 means no error margin.
	 */
	public void setSpeedError(float error) {
		this.speedError = error * averageSpeed;
	}

	/**
	 * @param error
	 *            - A number between 0 and 1, where 0 means no error margin.
	 */
	public void setLifeError(float error) {
		this.lifeError = error * averageLifeLength;
	}

	/**
	 * @param error
	 *            - A number between 0 and 1, where 0 means no error margin.
	 */
	public void setScaleError(float error) {
		this.scaleError = error * averageScale;
	}


	public void resetTime() {
		elapsedtime = 0;
	}
	
	public ParticleSpawnArea getSpawnArea(){
		return spawnarea;
	}
	
	public SimpleParticleSystem setSpawnArea(ParticleSpawnArea area){
		this.spawnarea = area;
		return this;
	}
	
	@Override
	public void update() {
		if (elapsedtime <= lifelengthsystem || lifelengthsystem < 0) {
			generateParticles(timemultiplier);
			elapsedtime += DisplayManager.instance().getDeltaTimef()*timemultiplier;
		}
	}
	
	
	private static float delta, particlesToCreate, partialParticle;
	private static int count;
	private static Vector3f pos;
	/**
	 * for <1 particle/sec
	 */
	private float lastParticlef = 0;

	public SimpleParticleSystem generateParticles(float timemultiplier) {
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

	private void emitAndAdd(Vector3f center){
		ParticleMaster.instance().addParticle(emitParticle(center));
	}
	
	
	private static Vector3f velocity;
	private static float scale, lifeLength;

	protected Particle emitParticle(Vector3f center) {
		if (direction != null) {
			velocity = generateRandomUnitVectorWithinCone(direction, directionAngel);
		} else {
			velocity = generateRandomUnitVector();
		}
		velocity.mul(getErroredValue(averageSpeed, speedError));
		scale = getErroredValue(averageScale, scaleError);
		lifeLength = averageLifeLength==-1?-1:getErroredValue(averageLifeLength, lifeError);
		return new SimpleParticle(particletexture,	calcNewSpawnPos(center), velocity, force,
				lifeLength, generateRotation(), scale, this, type);
	}

	protected Vector3f calcNewSpawnPos(Vector3f center){
		switch(spawnarea.getType()){
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