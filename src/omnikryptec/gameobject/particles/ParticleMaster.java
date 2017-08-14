package omnikryptec.gameobject.particles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import omnikryptec.gameobject.Camera;
import omnikryptec.resource.texture.ParticleAtlas;
import omnikryptec.util.ArrayUtil;
import omnikryptec.util.Instance;
import omnikryptec.util.profiler.Profilable;
import omnikryptec.util.profiler.ProfileContainer;
import omnikryptec.util.profiler.Profiler;

public class ParticleMaster implements Profilable {

    private static final Map<ParticleAtlas, ParticleList> particles = new HashMap<>();
    private static final ParticleRenderer rend = new ParticleRenderer();

    static Particle p;
    static Entry<ParticleAtlas, ParticleList> entry;
    static List<Particle> list;
    static ParticleList pl;
    static Iterator<Particle> iterator;
    static Iterator<Entry<ParticleAtlas, ParticleList>> mapIterator;
    static boolean tmpboolean,tmpboolean2;
    static ExecutorService executor;

    private static ParticleMaster instance;
    private volatile long updatedParticlesCount = 0;

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
    private final List<Particle> particlesToRemove = new ArrayList<>();

    public void render(Camera cam) {
        tmptime = Instance.getDisplayManager().getCurrentTime();
        rend.render(particles, cam);
        rendertime = Instance.getDisplayManager().getCurrentTime() - tmptime;
    }

    private void resetExecutor() {
        if (executor != null) {
            executor.shutdownNow();
        }
        executor = Executors.newFixedThreadPool(10);
    }

    public void logic(Camera c) {
        boolean multithread_ = Instance.getGameSettings().isMultithreadedParticles();
        if(multithread_){
        	long count = 0;
        	boolean mt = false;
        	for(ParticleList l : particles.values()){
        		count += l.list.size();
        		if(l.mt) {
        			mt = true;
        		}
        	}
        	if(count < Instance.getGameSettings().getMinMultithreadParticles() || !mt){
        		multithread_ = false;
        	}
        }
        updatedParticlesCount = 0;
        tmptime2 = Instance.getDisplayManager().getCurrentTime();
        mapIterator = particles.entrySet().iterator();
        while (mapIterator.hasNext()) {
            entry = mapIterator.next();
            pl = entry.getValue();
            list = pl.list;
            tmpboolean = pl.wantsUpdateLast;
            tmpboolean2 = true;
            iterator = list.iterator();
            if (multithread_) {
                resetExecutor();
                particlesToRemove.clear();
            }
            while (iterator.hasNext()) {
                p = iterator.next();
                if(tmpboolean&&tmpboolean2){
                	if(p.wantsupdatelast){
                		tmpboolean2 = false;
                	}
                }
                if (!multithread_) {
                    if (!p.update(c)) {
                        iterator.remove();
                        if (list.isEmpty()) {
                            mapIterator.remove(); 
                            break;
                        }
                    } else {
                        updatedParticlesCount++;
                    }
                } else {
                	final Particle p_ = p;
                    executor.execute(new Runnable() {
                        @Override
                        public final void run() {
                            if (!p_.update(c)) {
                                particlesToRemove.add(p_);
                            } else {
                                updatedParticlesCount++;
                            }
                        }
                    });
                }
            }
            if (multithread_) {
                executor.shutdown();
                try {
                    executor.awaitTermination(1, TimeUnit.MINUTES);
                } catch (InterruptedException ex) {
                    System.err.println(ex);
                }
                //TODO perfomance?!?!?
                particlesToRemove.stream().forEach((p_) -> {
                    list.remove(p_);
                });
                particlesToRemove.clear();
                if (list.isEmpty()) {
                    mapIterator.remove();
                }
            }
            if(tmpboolean){
            	for(Particle p : list){
            		p.updateLast();
            	}
            }
            if(tmpboolean2){
            	list1.wantsUpdateLast = false;
            }
            if (!entry.getKey().useAlphaBlending()) {
                ArrayUtil.parallelSortArrayListAsArray(list, Sorting.PARTICLE_COMPARATOR);
                //list.sort(Sorting.PARTICLE_COMPARATOR); //Sorry You Are Too Slow
            }
        }

        updatetime = Instance.getDisplayManager().getCurrentTime() - tmptime2;
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
    
    private static ParticleList list1;

    public void addParticle(Particle par) {
        list1 = particles.get(par.getParticleTexture());
        if (list1 == null) {
            list1 = new ParticleList();
            particles.put(par.getParticleTexture(), list1);
        }
        if(par.wantsUpdateLast()){
        	list1.wantsUpdateLast = true;
        }
        if(par.wantsMultithreaded()) {
        	list1.mt = true;
        }
        list1.list.add(par);
    }

    @Override
    public ProfileContainer[] getProfiles() {
        return new ProfileContainer[]{new ProfileContainer(Profiler.PARTICLE_RENDERER, getRenderTimeMS()), new ProfileContainer(Profiler.PARTICLE_UPDATER, getUpdateTimeMS())};
    }
}
