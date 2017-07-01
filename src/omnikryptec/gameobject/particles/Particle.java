package omnikryptec.gameobject.particles;

import omnikryptec.gameobject.gameobject.Camera;
import omnikryptec.gameobject.gameobject.Rangeable;

import org.joml.Vector2f;
import org.joml.Vector3f;

import omnikryptec.gameobject.gameobject.Entity.RenderType;

public class Particle implements Rangeable{
	
	protected ParticleTexture texture;
	private RenderType type;
	private Vector3f pos = new Vector3f(0);
	private float scale=1, rot=0;
	protected Vector2f texOffset1 = new Vector2f(), texOffset2 = new Vector2f();
	protected float blend;
	private float distance;
	
	public Particle(ParticleTexture texture){
		this(texture, RenderType.ALWAYS);
	}
	
	public Particle(ParticleTexture texture, RenderType type){
		this(null, texture, type);
	}
	
	public Particle(Vector3f pos, ParticleTexture texture, RenderType type){
		this.texture = texture;
		this.type = type;
		if(pos!=null){
			this.pos = pos;
		}
	}
	
	public final ParticleTexture getParticleTexture(){
		return texture;
	}

	public final Particle setPos(float x, float y, float z){
		pos.set(x, y, z);
		return this;
	}
	
	public final float getScale(){
		return scale;
	}
	
	public final Particle setScale(float f){
		this.scale = f;
		return this;
	}
	
	public final float getRotation(){
		return rot;
	}
	
	public final Particle setRotation(float f){
		this.rot = f;
		return this;
	}
	
	@Override
	public final Vector3f getAbsolutePos() {
		return pos;
	}

	@Override
	public final RenderType getType() {
		return type;
	}
	
	
	private static Vector3f tmp = new Vector3f();

	final boolean update(Camera cam) {
		distance = (cam.getAbsolutePos().sub(pos, tmp)).lengthSquared();
		updateTexCoordInfo();
		return update();
	}
	
	protected boolean update(){
		return true;
	}
	
	/**
	 *
	 * @return -1 to show the first texturetile or return elapsedtime/lifetime
	 */
	protected float getLifeFactor(){
		return -1;
	}
	
	private static float lifeFactor, atlasProg;
	private static int stageCount, index1, index2;

	private void updateTexCoordInfo() {
		lifeFactor = getLifeFactor();
		if(lifeFactor==-1){
			blend = 0;
			texOffset1.set(0, 0);
			texOffset2.set(0, 0);
		}else{
			stageCount = texture.getNumberOfRows() * texture.getNumberOfRows();
			atlasProg = lifeFactor * stageCount;
			index1 = (int) atlasProg;
			index2 = index1 < stageCount - 1 ? index1 + 1 : index1;
			this.blend = atlasProg % 1;
			if(index1==0){
				texOffset1.set(0);
			}else{
				texOffset1 = setTexOffset(texOffset1, index1);
			}
			if(index2==0){
				texOffset2.set(0);
			}else{
				texOffset2 = setTexOffset(texOffset2, index2);
			}
		}
	}

	private static int column, row;

	private Vector2f setTexOffset(Vector2f offset, int index) {
		column = index % texture.getNumberOfRows();
		row = index / texture.getNumberOfRows();
		offset.x = (float) column / texture.getNumberOfRows();
		offset.y = (float) row / texture.getNumberOfRows();
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
	
	public float getDistance(){
		return distance;
	}
}
