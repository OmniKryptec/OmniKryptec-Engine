package omnikryptec.gameobject.particlesV2;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class ParticleSimulationAttribute {
	
	private FloatBuffer buffer;
	private int singlesize;
	
	public ParticleSimulationAttribute(int singlesize) {
		this.singlesize = singlesize;
	}
	
	public ParticleSimulationAttribute(int singlesize, int amount) {
		buffer = BufferUtils.createFloatBuffer(singlesize*amount);
	}
	
	public int getInstanceSize() {
		return singlesize;
	}
	
	public FloatBuffer getBuffer() {
		return buffer;
	}
	
	public float get(int index) {
		return buffer.get(index);
	}
	
	public float get(int particleIndex, int component) {
		return buffer.get(particleIndex*singlesize+component);
	}

	public void set(int index, float data) {
		buffer.put(index, data);
	}
	
	public void set(int particleIndex, int component, float data) {
		buffer.put(particleIndex*singlesize+component, data);
	}
	
	public void setBuffer(FloatBuffer buffer) {
		this.buffer.clear();
		this.buffer = buffer;
	}
	
}
