package omnikryptec.particles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Matrix4f;

import omnikryptec.entity.Camera;

public class ParticleMaster {
	private static Map<ParticleTexture, List<Particle>> particles = new HashMap<ParticleTexture, List<Particle>>();
	private static ParticleRenderer rend;

	public static void init() {
		rend = new ParticleRenderer();
	}

	static Particle p;
	static Entry<ParticleTexture, List<Particle>> entry;
	static List<Particle> list;
	static Iterator<Particle> iterator;
	static Iterator<Entry<ParticleTexture, List<Particle>>> mapIterator;

	public static void update(Camera cam) {
		mapIterator = particles.entrySet().iterator();
		while (mapIterator.hasNext()) {
			entry = mapIterator.next();
			list = entry.getValue();
			iterator = list.iterator();
			while (iterator.hasNext()) {
				p = iterator.next();
				if (!p.update(cam, 1)) {
					iterator.remove();
					if (list.isEmpty()) {
						mapIterator.remove();
					}
				}
			}
			if (!entry.getKey().useAlphaBlending()) {
				InsertionSort.sortHighToLow(list);
			}
		}
	}

	public static void renderParticles(Camera cam) {
		rend.render(particles, cam);
	}

	public static void cleanup() {
		rend.cleanUp();
	}

	private static List<Particle> list1;

	public static void addParticle(Particle par) {
		list1 = particles.get(par.getTex());
		if (list1 == null) {
			list1 = new ArrayList<Particle>();
			particles.put(par.getTex(), list1);
		}
		list1.add(par);
	}
}
