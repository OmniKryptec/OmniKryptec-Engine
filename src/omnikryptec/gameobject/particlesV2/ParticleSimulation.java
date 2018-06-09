package omnikryptec.gameobject.particlesV2;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;

import omnikryptec.gameobject.particles.Particle;

public class ParticleSimulation {


	protected Map<String, ParticleSimulationAttribute> buffers = new HashMap<>();
	
	private List<SimulationStep> groupsteps = new ArrayList<>();
	
	protected int particles;
	private int max;
	
	public ParticleSimulation(int max) {
		this.max = max;
		this.particles = 0;
	}
	
	public void add(Object ...objs) {
		if(particles<max||max<=-1) {
			particles++;
			for(int i=0; i<objs.length; i+=2) {
				buffers.get(objs[i]).getBuffer().put((float)objs[i+1]);
			}
		}
	}
	
	public void simulate(float dt) {
		prepareSimulation();
		for(SimulationStep groupstep : groupsteps) {
			groupstep.step(dt, particles, buffers);
		}
	}
	
	protected void prepareSimulation() {
	}
	
	public void addPerAllStep(SimulationStep step) {
		groupsteps.add(step);
	}
	
	public int size() {
		return particles;
	}
	
//	public void put(ParticleDefinition p, float...data) {
//		if(datasize!=p.getLogicFloatSize()||datasize!=data.length) {
//			throw new IllegalArgumentException("The particle is not compatible with the amnount of logic slots in this simulation!");
//		}
//		logicbuffer.put(data);
//	}
//	
}
