package omnikryptec.particles;

import java.util.Random;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import omnikryptec.display.DisplayManager;
import omnikryptec.entity.Entity.RenderType;
import omnikryptec.entity.GameObject;
import omnikryptec.util.Maths;

public class ParticleSystem extends GameObject{

	private float pps, averageSpeed, averageLifeLength, averageScale;

	private float speedError, lifeError, scaleError = 0;
	private boolean randomRotation = false;
	private Vector3f direction, gravityComplient;
	private float directionDeviation = 0;
	private float timemultiplier=1;
	private RenderType type = RenderType.MEDIUM;
	
	private Random random = new Random();

	private ParticleTexture tex;
	
	public ParticleSystem(float x, float y, float z, ParticleTexture tex, float pps, float speed, Vector3f gravityComplient,
			float lifeLength, float scale, RenderType type){
		this.type = type;
		this.pps = pps;
		this.averageSpeed = speed;
		this.gravityComplient = gravityComplient;
		this.averageLifeLength = lifeLength;
		this.averageScale = scale;
		this.tex = tex;
		setRelativePos(x, y, z);
	}
	
	public ParticleSystem(Vector3f pos, ParticleTexture tex, float pps, float speed, Vector3f gravityComplient,
			float lifeLength, float scale, RenderType type) {
		this(pos.x, pos.y, pos.z, tex, pps, speed, gravityComplient, lifeLength, scale, type);
	}

	/**
	 * @param direction
	 *            - The average direction in which particles are emitted.
	 * @param deviation
	 *            - A value between 0 and 1 indicating how far from the chosen
	 *            direction particles can deviate.
	 */
	public void setDirection(Vector3f direction, float deviation) {
		this.direction = new Vector3f(direction);
		this.directionDeviation = (float) (deviation * Math.PI);
	}

	public void randomizeRotation() {
		randomRotation = true;
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
	
	@Override
	public void doLogic(){
		generateParticles(timemultiplier);
	}
	
	private static float delta,particlesToCreate,partialParticle;
	private static int count;
	public void generateParticles(float timemultiplier) {
		delta = DisplayManager.instance().getDeltaTime()*timemultiplier;
		particlesToCreate = pps * delta;
		count = (int) Math.floor(particlesToCreate);
		partialParticle = particlesToCreate % 1;
		for (int i = 0; i < count; i++) {
			emitParticle(getAbsolutePos());
		}
		if (Math.random() < partialParticle) {
			emitParticle(getAbsolutePos());
		}
	}

	private static Vector3f velocity;
	private static float scale, lifeLength;
	
	private void emitParticle(Vector3f center) {
		if (direction != null) {
			velocity = generateRandomUnitVectorWithinCone(direction, directionDeviation);
		} else {
			velocity = generateRandomUnitVector();
		}
		velocity.normalise();
		velocity.scale(generateValue(averageSpeed, speedError));
		scale = generateValue(averageScale, scaleError);
		lifeLength = generateValue(averageLifeLength, lifeError);
		ParticleMaster.instance().addParticle(new Particle(tex, new Vector3f(center), velocity, gravityComplient, lifeLength, generateRotation(), scale, this, type));
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
			Vector3f.cross(coneDirection, Maths.Z, rotateAxis);
			rotateAxis.normalise();
			rotateAngle = (float) Math.acos(Vector3f.dot(coneDirection, Maths.Z));
			rotationMatrix.setIdentity();
			rotationMatrix.rotate(-rotateAngle, rotateAxis);
			Matrix4f.transform(rotationMatrix, tmp4f, tmp4f);
		} else if (coneDirection.z == -1) {
			tmp4f.z *= -1;
		}
		return new Vector3f(tmp4f);
	}

	private static float theta,z,rootOneMinusZSquared,x,y;
	private Vector3f generateRandomUnitVector() {
		theta = (float) (random.nextFloat() * 2f * Math.PI);
		z = (random.nextFloat() * 2) - 1;
		rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
		x = (float) (rootOneMinusZSquared * Math.cos(theta));
		y = (float) (rootOneMinusZSquared * Math.sin(theta));
		return new Vector3f(x, y, z);
	}

	public float getTimemultiplier() {
		return timemultiplier;
	}

	public void setTimemultiplier(float timemultiplier) {
		this.timemultiplier = timemultiplier;
	}


}