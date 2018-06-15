package omnikryptec.gameobject.particlesV2;

import java.util.ArrayList;
import java.util.List;

public class ParticleSimulation {

	private ParticleSimulationBuffers buffers = new ParticleSimulationBuffers();
	private List<SimulatorPerSimulation> sims = new ArrayList<>();
	
	private int max;
	private int size;
	
	public ParticleSimulation(int max) {
		this.max = max;
	}
	
	public void simulate(float dt) {
		for(SimulatorPerSimulation s : sims) {
			if(s.isOpenCLSimulation()) {
				buffers.enableOpenCLMode(true);
			}
			s.step(dt, this);
			if(s.isOpenCLSimulation()) {
				buffers.enableOpenCLMode(false);
			}
		}
	}
	
	public void addSimulator(SimulatorPerSimulation sim) {
		sims.add(sim);
	}
	
	public int size() {
		return size;
	}
	
	public int limit() {
		return max;
	}
	
	public boolean isUnlimited() {
		return max<0;
	}
	
	public ParticleSimulationBuffers getParticleData() {
		return buffers;
	}
	
	
}
