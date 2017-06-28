package omnikryptec.particles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import omnikryptec.entity.Camera;
import omnikryptec.util.ArrayUtil;
import omnikryptec.util.Instance;
import omnikryptec.util.profiler.Profilable;
import omnikryptec.util.profiler.ProfileContainer;
import omnikryptec.util.profiler.Profiler;

public class ParticleMaster implements Profilable {

    private static Map<ParticleTexture, List<Particle>> particles = new HashMap<ParticleTexture, List<Particle>>();
    private static ParticleRenderer rend = new ParticleRenderer();

    static Particle p;
    static Entry<ParticleTexture, List<Particle>> entry;
    static List<Particle> list;
    static Iterator<Particle> iterator;
    static Iterator<Entry<ParticleTexture, List<Particle>>> mapIterator;

    private static ParticleMaster instance;
    private long updatedParticlesCount = 0;

    public static ParticleMaster instance() {
        if (instance == null) {
            instance = new ParticleMaster();
        }
        return instance;
    }

    private ParticleMaster() {
        Profiler.addProfilable(this, 1);
    }

    private double rendertime = 0;
    private double tmptime = 0, tmptime2;
    private double updatetime = 0;

    public void update(Camera cam) {
        updatedParticlesCount = 0;
        tmptime2 = Instance.getDisplayManager().getCurrentTime();
        mapIterator = particles.entrySet().iterator();
        while (mapIterator.hasNext()) {
            entry = mapIterator.next();
            list = entry.getValue();
            iterator = list.iterator();
            while (iterator.hasNext()) {
                p = iterator.next();
                if (!p.update(cam)) {
                    iterator.remove();
                    if (list.isEmpty()) {
                        mapIterator.remove();
                    }
                } else {
                    updatedParticlesCount++;
                }
            }
            if (!entry.getKey().useAlphaBlending()) {
                ArrayUtil.parallelSortArrayListAsArray(list, Sorting.PARTICLE_COMPARATOR);
                //list.sort(Sorting.PARTICLE_COMPARATOR); //Sorry You Are Too Slow
            }
        }
        updatetime = Instance.getDisplayManager().getCurrentTime() - tmptime2;
        tmptime = Instance.getDisplayManager().getCurrentTime();
        rend.render(particles, cam);
        rendertime = Instance.getDisplayManager().getCurrentTime() - tmptime;
    }

    public double getRenderTimeMS() {
        return rendertime;
    }

    public double getUpdateTimeMS() {
        return updatetime;
    }

    public double getOverallParticleTimeMS() {
        return getRenderTimeMS() + getUpdateTimeMS();
    }

    public long getRenderedParticlesCount() {
        return rend.getParticleCount();
    }

    public long getUpdatedParticlesCount() {
        return updatedParticlesCount;
    }

    public static void cleanup() {
        rend.cleanUp();
    }

    private static List<Particle> list1;

    public void addParticle(Particle par) {
        list1 = particles.get(par.getTexture());
        if (list1 == null) {
            list1 = new ArrayList<Particle>(100);
            particles.put(par.getTexture(), list1);
        }
        list1.add(par);
    }

    @Override
    public ProfileContainer[] getProfiles() {
        return new ProfileContainer[]{new ProfileContainer(Profiler.PARTICLE_RENDERER, getRenderTimeMS()), new ProfileContainer(Profiler.PARTICLE_UPDATER, getUpdateTimeMS())};
    }
}
