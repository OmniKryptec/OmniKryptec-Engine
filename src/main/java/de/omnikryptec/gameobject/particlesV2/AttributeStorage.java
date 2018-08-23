package de.omnikryptec.gameobject.particlesV2;

public class AttributeStorage {
	
	private ParticleFloatBuffer buffer;
	private ParticleAttribute attribute;
	
	public AttributeStorage(ParticleAttribute att, int initialSize) {
		this.attribute = att;
		this.buffer = new ParticleFloatBuffer(attribute.getComponentSize()*initialSize);
	}
	
	public ParticleAttribute getAttribute() {
		return attribute;
	}
	
	public ParticleFloatBuffer getParticleBuffer() {
		return buffer;
	}
	
	public void set(int particleIndex, int comp, float data) {
		buffer.getWriteBuffer().put(attribute.calcIndex(particleIndex, comp), data);
	}
	
	public void add(int particleIndex, int comp, float data) {
		set(particleIndex, comp, data+get(particleIndex, comp));
	}
	
	public float get(int particleIndex, int comp) {
		return buffer.getReadBuffer().get(attribute.calcIndex(particleIndex, comp));
	}
	
	public float get(int index) {
		return buffer.getReadBuffer().get(index);
	}
	
}
