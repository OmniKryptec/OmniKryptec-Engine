package omnikryptec.gameobject.particlesV2;

public class ParticleAttribute {
	
	private String name;
	private int componentsize;
	
	public ParticleAttribute(String name, int componentsize) {
		this.name = name;
		this.componentsize = componentsize;
	}
	
	public String getName() {
		return name;
	}
	
	public int getComponentSize() {
		return componentsize;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
}
