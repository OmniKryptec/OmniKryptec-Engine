package omnikryptec.particles;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import omnikryptec.display.DisplayManager;
import omnikryptec.entity.Camera;

public class Particle {

	private Vector3f pos;
	private Vector3f vel;
	private Vector3f gravityEffect;
	private float lifeLength;
	private float rot;
	private float scale;

	private float elapsedTime = 0;
	private float distance;

	private ParticleTexture tex;
	
	private ParticleSystem mysystem;
	
	private Vector2f texOffset1 = new Vector2f();
	private Vector2f texOffset2 = new Vector2f();
	private float blend;

	private static Vector3f changeable = new Vector3f();

	public Particle(ParticleTexture tex, Vector3f pos, Vector3f vel, Vector3f gravityEffect, float lifeLength, float rot,
			float scale, ParticleSystem sys) {
		this.pos = pos;
		this.vel = vel;
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeLength;
		this.elapsedTime = 0;
		this.rot = rot;
		this.scale = scale;
		this.tex = tex;
		this.mysystem = sys;
	}

	public float getDistance() {
		return distance;
	}
	
	public ParticleSystem getSystem(){
		return mysystem;
	}
	
	private static float lifeFactor,atlasProg;
	private static int stageCount, index1,index2;
	private void updateTexCoordInfo() {
		lifeFactor = elapsedTime / lifeLength;
		stageCount = tex.getNumberOfRows() * tex.getNumberOfRows();
		atlasProg = lifeFactor * stageCount;
		index1 = (int) Math.floor(atlasProg);
		index2 = index1 < stageCount - 1 ? index1 + 1 : index1;
		this.blend = atlasProg % 1;
		texOffset1 = setTexOffset(texOffset1, index1);
		texOffset2 = setTexOffset(texOffset2, index2);
	}

	private static int column,row;
	private Vector2f setTexOffset(Vector2f offset, int index) {
		column = index % tex.getNumberOfRows();
		row =  index / tex.getNumberOfRows();
		offset.x = (float) column / tex.getNumberOfRows();
		offset.y = (float) row / tex.getNumberOfRows();
		return offset;
	}

	public Vector2f getTexOffset1() {
		return texOffset1;
	}

	public Vector2f getTexOffset2() {
		return texOffset2;
	}

	public float getBlend() {
		return blend;
	}

	public ParticleTexture getTexture() {
		return tex;
	}

	public Vector3f getPos() {
		return pos;
	}

	public float getRot() {
		return rot;
	}

	public float getScale() {
		return scale;
	}

	
	private static Vector3f tmp;
	
	protected boolean update(Camera cam, float timemultiplier) {
		Vector3f.add(vel, gravityEffect, vel);
		changeable.set(vel);
		changeable.scale(DisplayManager.instance().getDeltaTime()*timemultiplier);
		Vector3f.add(changeable, pos, pos);
		distance = (tmp=Vector3f.sub(cam.getPos(), pos, tmp)).lengthSquared();
		updateTexCoordInfo();
		elapsedTime += DisplayManager.instance().getDeltaTime()*timemultiplier;
		return elapsedTime < lifeLength;
	}

}
