package omnikryptec.gameobject.particles;

import org.joml.Vector3f;

import omnikryptec.util.logger.Logger;
import omnikryptec.util.logger.LogEntry.LogLevel;

public class ParticleSpawnArea {
	
	public static enum ParticleSpawnAreaType{
		POINT, CIRCLE, SHPERE, LINE;
	}
	
	private float data=0;
	private ParticleSpawnAreaType type;
	private Vector3f direction = new Vector3f(0,1,0);
	
	public ParticleSpawnArea(ParticleSpawnAreaType type, float data){
		this.data = data;
		this.type = type;
	}
	
	public ParticleSpawnArea(ParticleSpawnAreaType type, Vector3f offset1, float length){
		this.type = type;
		this.direction = offset1;
		this.data = length;
	}
	
	public Vector3f getDirection(){
		return direction;
	}
	
	public float getData(){
		return data;
	}
	
	public ParticleSpawnArea setData(float d){
		this.data = d;
		return this;
	}
	
	public ParticleSpawnAreaType getType(){
		return type;
	}
	
	public ParticleSpawnArea setType(ParticleSpawnAreaType type){
		this.type = type;
		return this;
	}
	
}
