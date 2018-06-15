package omnikryptec.gameobject.particlesV2;

public class ParticleAttribute {
	
	/**
	 * if PGC use -1 to remove the particle from all buffers
	 */
	public static final String LIFESTATUS = "lifestatus";
	
	public static final String MASS ="mass";
	
	public static final String POSITION="position";
	
	public static final String VELOCITY ="velocity";
	
	public static final String ACCELERATION ="acceleration";
	
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
