package omnikryptec.gameobject.particlesV2;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;

public class DynamicParticleSimulation extends ParticleSimulation{

	protected Map<String, List<Float>> dynamicFloats = new HashMap<>();
	
	public DynamicParticleSimulation() {
		super(-1);
	}

	@Override
	public void add(Object ...objs) {
		particles++;
		for(int i=0; i<objs.length; i+=2) {
			dynamicFloats.get(objs[i]).add((float)objs[i+1]);
		}
	}
	
	@Override
	public void prepareSimulation() {
		for(String s : dynamicFloats.keySet()) {
			FloatBuffer buffer = BufferUtils.createFloatBuffer(dynamicFloats.get(s).size());
			for(Float f : dynamicFloats.get(s)) {
				buffer.put(f);
			}
			buffers.get(s).setBuffer(buffer);
		}
	}
	
}
