package omnikryptec.gameobject.particles;

import java.util.Random;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import omnikryptec.display.DisplayManager;
import omnikryptec.gameobject.gameobject.GameObject;
import omnikryptec.gameobject.gameobject.Entity.RenderType;
import omnikryptec.util.Maths;

public class SimpleParticleSystem extends GameObject {

	private float pps, averageSpeed, averageLifeLength, averageScale;

	private float speedError, lifeError, scaleError = 0;
	private boolean randomRotation = false;
	private Vector3f direction, gravityComplient;
	private float directionDeviation = 0;
	private float timemultiplier = 1;
	private RenderType type = RenderType.MEDIUM;

	private float lifelengthf = -1;
	private float elapsedtime = 0;

	private Random random = new Random();

	private ParticleTexture tex;

	public SimpleParticleSystem(Vector3f pos, ParticleTexture tex, float pps, float speed, float lifeLength, float scale,
			RenderType type) {
		this(pos.x, pos.y, pos.z, tex, pps, speed, lifeLength, scale, type);
	}

	public SimpleParticleSystem(float x, float y, float z, ParticleTexture tex, float pps, float speed, float lifeLength,
			float scale, RenderType type) {
		this(x, y, z, tex, pps, speed, Maths.ZERO, lifeLength, scale, type);
	}

	public SimpleParticleSystem(float x, float y, float z, ParticleTexture tex, float pps, float speed,
			Vector3f gravityComplient, float lifeLength, float scale, RenderType type) {
		this.type = type;
		this.pps = pps;
		this.averageSpeed = speed;
		this.gravityComplient = gravityComplient;
		this.averageLifeLength = lifeLength;
		this.averageScale = scale;
		this.tex = tex;
		setRelativePos(x, y, z);
	}

	public SimpleParticleSystem(Vector3f pos, ParticleTexture tex, float pps, float speed, Vector3f gravityComplient,
			float lifeLength, float scale, RenderType type) {
		this(pos.x, pos.y, pos.z, tex, pps, speed, gravityComplient, lifeLength, scale, type);
	}

	public SimpleParticleSystem setSystemLifeLength(float f) {
		this.lifelengthf = f;
		return this;
	}

	/**
	 * @param direction
	 *            - The average direction in which particles are emitted.
	 * @param deviation
	 *            - A value between 0 and 1 indicating how far from the chosen
	 *            direction particles can deviate.
	 */
	public SimpleParticleSystem setDirection(Vector3f direction, float angel) {
		this.direction = new Vector3f(direction);
		this.directionDeviation = angel;
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
	
	public float getTimemultiplier() {
		return timemultiplier;
	}

	public SimpleParticleSystem setTimemultiplier(float timemultiplier) {
		this.timemultiplier = timemultiplier;
		return this;
	}
	
	
	@Override
	public void doLogic() {
		if (elapsedtime <= lifelengthf || lifelengthf < 0) {
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
		particlesToCreate = pps * delta;
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
			velocity = generateRandomUnitVectorWithinCone(direction, directionDeviation);
		} else {
			velocity = generateRandomUnitVector();
		}
		velocity.normalize();
		velocity.mul(generateValue(averageSpeed, speedError));
		scale = generateValue(averageScale, scaleError);
		lifeLength = generateValue(averageLifeLength, lifeError);
		return new SimpleParticle(tex, new Vector3f(center), velocity, gravityComplient,
				lifeLength, generateRotation(), scale, this, type);
	}

	private float offset;

	private float generateValue(float average, float errorMargin) {
		offset = (random.nextFloat() - 0.5f) * 2f * errorMargin;
		return average + offset;
	}

	private float generateRotation() {
		if (randomRotation) {
			return random.nextFloat() * 360f;
		} else {
			return 0;
		}
	}

	private static float cosAngle, rotateAngle;
	private static Random randoms;
	private static Vector4f tmp4f = new Vector4f();
	private static Vector3f rotateAxis;
	private static Matrix4f rotationMatrix = new Matrix4f();

	private static Vector3f generateRandomUnitVectorWithinCone(Vector3f coneDirection, float angle) {
		cosAngle = (float) Math.cos(angle);
		randoms = new Random();
		theta = (float) (randoms.nextFloat() * 2f * Math.PI);
		z = cosAngle + (randoms.nextFloat() * (1 - cosAngle));
		rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
		x = (float) (rootOneMinusZSquared * Math.cos(theta));
		y = (float) (rootOneMinusZSquared * Math.sin(theta));

		tmp4f.set(x, y, z, 1);
		if (coneDirection.x != 0 || coneDirection.y != 0 || (coneDirection.z != 1 && coneDirection.z != -1)) {
			coneDirection.cross(Maths.Z, rotateAxis);
			rotateAxis.normalize();
			rotateAngle = (float) Math.acos(coneDirection.dot(Maths.Z));
			rotationMatrix.identity();
			rotationMatrix.rotate(-rotateAngle, rotateAxis);
			rotationMatrix.transform(tmp4f);
		} else if (coneDirection.z == -1) {
			tmp4f.z *= -1;
		}
		return new Vector3f(tmp4f.x, tmp4f.y, tmp4f.z);
	}

	private static float theta, z, rootOneMinusZSquared, x, y;

	private Vector3f generateRandomUnitVector() {
		theta = (float) (random.nextFloat() * 2f * Math.PI);
		z = (random.nextFloat() * 2) - 1;
		rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
		x = (float) (rootOneMinusZSquared * Math.cos(theta));
		y = (float) (rootOneMinusZSquared * Math.sin(theta));
		return new Vector3f(x, y, z);
	}

}