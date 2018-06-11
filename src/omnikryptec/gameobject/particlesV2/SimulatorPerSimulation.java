package omnikryptec.gameobject.particlesV2;

public interface SimulatorPerSimulation {
	
	public static final String[] EMPTY = new String[0];
	
	void step(float dt, ParticleSimulation sim);
	
	default String[] requireStashed() {
		return EMPTY;
	}
}
