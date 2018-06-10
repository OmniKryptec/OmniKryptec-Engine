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
	
}
