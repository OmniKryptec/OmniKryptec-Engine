/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.old.gameobject.particles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.codemakers.util.ArrayUtil;
import de.omnikryptec.old.gameobject.Camera;
import de.omnikryptec.old.resource.texture.ParticleAtlas;
import de.omnikryptec.old.util.EnumCollection.BlendMode;
import de.omnikryptec.old.util.Instance;
import de.omnikryptec.old.util.profiler.Profilable;
import de.omnikryptec.old.util.profiler.ProfileContainer;
import de.omnikryptec.old.util.profiler.Profiler;

public class ParticleMaster implements Profilable {

    private static final Map<ParticleAtlas, ParticleList> particles = new HashMap<>();
    private static final ParticleRenderer renderer = new ParticleRenderer();

    private static Particle particleCurrent;
    private static Entry<ParticleAtlas, ParticleList> entry;
    private static List<Particle> list;
    private static ParticleList particlelist;
    private static Iterator<Particle> listIterator;
    private static Iterator<Entry<ParticleAtlas, ParticleList>> mapIterator;
    private static boolean listWantsUpdateLastTmp, cansetWantsUpdateLastFalse;
    private static ExecutorService executor;

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
        renderer.render(particles, cam);
        rendertime += Instance.getDisplayManager().getCurrentTime() - tmptime;
    }

    private void resetExecutor() {
        if (executor != null) {
            executor.shutdownNow();
        }
        executor = Executors.newFixedThreadPool(Instance.getGameSettings().getParticleThreadpoolSize());
    }

    public void logic(Camera c) {
        boolean multithread_ = Instance.getGameSettings().isMultithreadedParticles();
        if (multithread_) {
            long count = 0;
            boolean mt = false;
            for (ParticleList l : particles.values()) {
                count += l.list.size();
                if (l.mt) {
                    mt = true;
                }
            }
            if (count < Instance.getGameSettings().getMinMultithreadParticles() || !mt) {
                multithread_ = false;
            }
        }
        updatedParticlesCount = 0;
        tmptime2 = Instance.getDisplayManager().getCurrentTime();
        mapIterator = particles.entrySet().iterator();
        while (mapIterator.hasNext()) {
            entry = mapIterator.next();
            particlelist = entry.getValue();
            list = particlelist.list;
            listWantsUpdateLastTmp = particlelist.wantsUpdateLast;
            cansetWantsUpdateLastFalse = true;
            listIterator = list.iterator();
            if (multithread_) {
                resetExecutor();
                particlesToRemove.clear();
            }
            while (listIterator.hasNext()) {
                particleCurrent = listIterator.next();
                if (listWantsUpdateLastTmp && cansetWantsUpdateLastFalse) {
                    if (particleCurrent.wantsupdatelast) {
                        cansetWantsUpdateLastFalse = false;
                    }
                }
                if (!multithread_) {
                    if (!particleCurrent.update(c)) {
                        listIterator.remove();
                        if (list.isEmpty()) {
                            mapIterator.remove();
                            break;
                        }
                    } else {
                        updatedParticlesCount++;
                    }
                } else {
                    final Particle p_ = particleCurrent;
                    executor.execute(new Runnable() {
                        @Override
                        public final void run() {
                            if (!p_.update(c)) {
                                synchronized (particlesToRemove) {
                                    particlesToRemove.add(p_);
                                }
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
                for (Particle p_ : particlesToRemove) {
                    list.remove(p_);
                }
                particlesToRemove.clear();
                if (list.isEmpty()) {
                    mapIterator.remove();
                }
            }
            if (listWantsUpdateLastTmp) {
                for (Particle p : list) {
                    p.updateLast();
                }
            }
            if (cansetWantsUpdateLastFalse) {
                list1.wantsUpdateLast = false;
            }
            //Brauch man es bei anderen vlt auch nicht? TOD-
            if (!(entry.getKey().getBlendMode() == BlendMode.ALPHA)) {
                ArrayUtil.parallelSortArrayListAsArray(list, Sorting.PARTICLE_COMPARATOR);
                //list.sort(Sorting.PARTICLE_COMPARATOR); //Sorry You Are Too Slow
            }
        }

        updatetime += Instance.getDisplayManager().getCurrentTime() - tmptime2;
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
        return renderer.getParticleCount();
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
        if (par.wantsUpdateLast()) {
            list1.wantsUpdateLast = true;
        }
        if (par.wantsMultithreaded()) {
            list1.mt = true;
        }
        list1.list.add(par);
    }

    @Override
    public ProfileContainer[] getProfiles() {
        return new ProfileContainer[]{new ProfileContainer(Profiler.PARTICLE_RENDERER, getRenderTimeMS()), new ProfileContainer(Profiler.PARTICLE_UPDATER, getUpdateTimeMS())};
    }

    public static void resetTimes() {
        if (instance != null) {
            instance.rendertime = 0;
            instance.updatetime = 0;
        }
    }
}