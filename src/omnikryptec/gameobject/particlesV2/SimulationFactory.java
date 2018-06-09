package omnikryptec.gameobject.particlesV2;

import java.util.ArrayList;

public class SimulationFactory {


	public static final String LIFESTATUS = "lifestatus";

	public static final String POSITION = "position";

	public static final String VELOCITY = "velocity";

	public static final String ACCELERATION = "acceleration";
	
	public static final String COLOR = "color";

	public static final String MASS = "mass";

	public static ParticleSimulation createStaticSimulation(int maxparticles, Object ...objs) {
		ParticleSimulation simulation = new ParticleSimulation(maxparticles);
		for(int i=0; i<objs.length; i+=2) {
			simulation.buffers.put((String)objs[i], new ParticleSimulationAttribute((int)objs[i+1], maxparticles));
		}
		return simulation;
	}
	
	public static DynamicParticleSimulation createDynamicSimulation(Object ...objs) {
		DynamicParticleSimulation simulation = new DynamicParticleSimulation();
		for(int i=0; i<objs.length; i+=2) {
			simulation.buffers.put((String)objs[i], new ParticleSimulationAttribute((int)objs[i+1]));
		}
		for(int i=0; i<objs.length; i+=2) {
			simulation.dynamicFloats.put((String)objs[i], new ArrayList<>());
		}
		return simulation;
	}
	
}
