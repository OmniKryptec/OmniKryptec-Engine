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
	
	public int calcIndex(int pIndex, int comp) {
		return pIndex*componentsize+comp;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
}
