package omnikryptec.gameobject.particlesV2;

public class AttributeStorage {
	
	private FloatBufferBuffer buffer;
	private ParticleAttribute attribute;
	
	public AttributeStorage(ParticleAttribute att, int initialSize) {
		this.attribute = att;
		this.buffer = new FloatBufferBuffer(2, attribute.getComponentSize()*initialSize);
	}
	
	public ParticleAttribute getAttribute() {
		return attribute;
	}
	
	public FloatBufferBuffer getFlBuBuffer() {
		return buffer;
	}
	
	public void set(int particleIndex, int comp, float data) {
		buffer.put(attribute.calcIndex(particleIndex, comp), data);
	}
	
	public void add(int particleIndex, int comp, float data) {
		buffer.add(attribute.calcIndex(particleIndex, comp), data);
	}
	
	public float get(int particleIndex, int comp) {
		return buffer.get(attribute.calcIndex(particleIndex, comp));
	}
	
}
