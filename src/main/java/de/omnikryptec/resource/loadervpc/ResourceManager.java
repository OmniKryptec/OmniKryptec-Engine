/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.resource.loadervpc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.util.ExecutorsUtil;

public class ResourceManager {
    
    private final Collection<LoadingProgressCallback> callbacks;
    private final Collection<ResourceLoader<?>> loadersThreadGroup;
    private final Collection<ResourceLoader<?>> loadersMainThread;
    private final List<ResourceLocation> staged;
    private final ResourceProvider resourceProvider;
    private final ResourceNameGenerator resourceNameGenerator;
    
    public ResourceManager() {
        this(new DefaultResourceProvider(), ResourceNameGenerator.defaultNameGen());
    }
    
    public ResourceManager(final ResourceProvider resProv, final ResourceNameGenerator nameGen) {
        this.resourceProvider = resProv;
        this.resourceNameGenerator = nameGen;
        this.callbacks = new ArrayList<>();
        this.staged = new ArrayList<>();
        this.loadersThreadGroup = new ArrayList<>();
        this.loadersMainThread = new ArrayList<>();
    }
    
    public void addDefaultLoaders() {
        addLoader(new TextureLoader());
        addLoader(new MeshLoader());
        addLoader(new ShaderLoader());
        addLoader(new FontLoader());
    }
    
    public void clearLoaders() {
        this.loadersThreadGroup.clear();
        this.loadersMainThread.clear();
    }
    
    public ResourceProvider getProvider() {
        return this.resourceProvider;
    }
    
    public void addCallback(final LoadingProgressCallback callback) {
        this.callbacks.add(callback);
    }
    
    public void stage(final String file) {
        this.stage(new AdvancedFile(file));
    }
    
    public void stage(final AdvancedFile file) {
        stage(file, 0);
    }
    
    public void stage(final String file, final int prio) {
        this.stage(new AdvancedFile(file), prio);
    }
    
    public void stage(final AdvancedFile file, final int priority) {
        stage(new ResourceLocation(priority, file));
    }
    
    public void stage(final ResourceLocation file) {
        this.staged.add(file);
    }
    
    public void clearStaged() {
        this.staged.clear();
    }
    
    public void processStaged(final boolean override, final boolean flat) {
        new Processor(override, flat).processStaged();
    }
    
    public void load(final boolean override, final boolean flat, final String file) {
        this.load(override, flat, new AdvancedFile(file));
    }
    
    public void load(final boolean override, final boolean flat, final AdvancedFile file) {
        new Processor(override, flat).loadSimple(file);
    }
    
    private void addResource(final Object res, final AdvancedFile file, final AdvancedFile superfile,
            final boolean override) {
        if (res != null) {
            final String name = this.resourceNameGenerator.genName(res, file, superfile);
            this.resourceProvider.add(res, name, override);
        }
    }
    
    public void addLoader(final ResourceLoader<?> loader) {
        if (loader.requiresMainThread()) {
            this.loadersMainThread.add(loader);
        } else {
            this.loadersThreadGroup.add(loader);
        }
    }
    
    public static class ResourceLocation implements Comparable<ResourceLocation> {
        
        private final int priority;
        private final AdvancedFile loc;
        
        public ResourceLocation(final int priority, final AdvancedFile file) {
            this.priority = priority;
            this.loc = file;
        }
        
        public int getPriority() {
            return this.priority;
        }
        
        public AdvancedFile getFile() {
            return this.loc;
        }
        
        @Override
        public int compareTo(final ResourceLocation o) {
            return this.priority - o.priority;
        }
    }
    
    /*
     * Kind of messy, in here
     */
    private class Processor {
        private final boolean override;
        private final boolean flat;
        private ExecutorService executorService = null;
        private final ConcurrentLinkedQueue<ResTask> resourcesTmp;
        
        private class ResTask {
            
            private ResTask(final Object res, final AdvancedFile file, final AdvancedFile superfile) {
                this.res = res;
                this.file = file;
                this.superfile = superfile;
            }
            
            private final Object res;
            private final AdvancedFile file;
            private final AdvancedFile superfile;
        }
        
        /* for #notifyProcessed() */
        private int localprocessed;
        /**/
        
        private Processor(final boolean override, final boolean flat) {
            this.override = override;
            this.flat = flat;
            this.executorService = ExecutorsUtil.newFixedThreadPool();
            this.resourcesTmp = new ConcurrentLinkedQueue<>();
        }
        
        private void processStaged() {
            Collections.sort(ResourceManager.this.staged);
            final int[] localmaxs = new int[ResourceManager.this.staged.size()];
            int size = 0;
            for (int i = 0; i < ResourceManager.this.staged.size(); i++) {
                localmaxs[i] = countFiles(ResourceManager.this.staged.get(i).getFile(), 0);
                size += localmaxs[i];
            }
            notifyStart(size, ResourceManager.this.staged.size());
            for (int i = 0; i < ResourceManager.this.staged.size(); i++) {
                final AdvancedFile file = ResourceManager.this.staged.get(i).getFile();
                notifyStage(file, i, localmaxs[i]);
                processStagedIntern(file, file);
            }
            finish();
        }
        
        private void processStagedIntern(final AdvancedFile file, final AdvancedFile superFile) {
            if (canList(file) && (!this.flat || file.equals(superFile))) {
                for (final AdvancedFile subFile : file.listFiles()) {
                    processStagedIntern(subFile, superFile);
                }
            } else {
                load(true, file, superFile);
            }
        }
        
        private void loadSimple(final AdvancedFile file) {
            final int size = countFiles(file, 0);
            notifyStart(size, 1);
            notifyStage(file, 0, size);
            loadSimpleIntern(size > 1, file, file);
            finish();
        }
        
        private void loadSimpleIntern(final boolean exec, final AdvancedFile file, final AdvancedFile superFile) {
            if (canList(file) && (!this.flat || file.equals(superFile))) {
                for (final AdvancedFile subFile : file.listFiles()) {
                    loadSimpleIntern(exec, subFile, superFile);
                }
            } else {
                load(exec, file, superFile);
            }
        }
        
        private int countFiles(final AdvancedFile file, int old) {
            if ((canList(file))) {
                final List<AdvancedFile> filesHere = file.listFiles();
                for (final AdvancedFile f : filesHere) {
                    if (canList(f) && this.flat) {
                        continue;
                    }
                    old = countFiles(f, old);
                }
            } else {
                old++;
            }
            return old;
        }
        
        private boolean canList(final AdvancedFile f) {
            return f.isDirectory() || f.isFileProvided();
        }
        
        private void notifyStage(final AdvancedFile file, final int stagenumber, final int localmax) {
            this.localprocessed = 0;
            for (final LoadingProgressCallback callback : ResourceManager.this.callbacks) {
                callback.onStageChange(file, localmax, stagenumber);
            }
        }
        
        private void notifyProcessed(final AdvancedFile file) {
            this.localprocessed++;
            for (final LoadingProgressCallback callback : ResourceManager.this.callbacks) {
                callback.onProgressChange(file, this.localprocessed);
            }
        }
        
        private void notifyStart(final int size, final int maxstages) {
            for (final LoadingProgressCallback callback : ResourceManager.this.callbacks) {
                callback.onLoadingStart(size, maxstages);
            }
        }
        
        private void finish() {
            ExecutorsUtil.shutdown(this.executorService, 1, TimeUnit.HOURS, false);
            for (final ResTask rt : this.resourcesTmp) {
                addResource(rt.res, rt.file, rt.superfile, this.override);
            }
            notifyDone();
        }
        
        private void notifyDone() {
            for (final LoadingProgressCallback callback : ResourceManager.this.callbacks) {
                callback.onLoadingDone();
            }
        }
        
        private void load(final boolean useExecutor, final AdvancedFile file, final AdvancedFile superFile) {
            final Runnable r = () -> {
                for (final ResourceLoader<?> loader : ResourceManager.this.loadersThreadGroup) {
                    if (file.getName().matches(loader.getFileNameRegex())) {
                        Object res = null;
                        try {
                            res = loader.load(file);
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }
                        this.resourcesTmp.add(new ResTask(res, file, superFile));
                    }
                }
            };
            if (useExecutor) {
                this.executorService.submit(r);
            } else {
                r.run();
            }
            for (final ResourceLoader<?> loader : ResourceManager.this.loadersMainThread) {
                if (file.getName().matches(loader.getFileNameRegex())) {
                    Object res = null;
                    try {
                        res = loader.load(file);
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                    addResource(res, file, superFile, this.override);
                }
            }
            notifyProcessed(file);
        }
    }
    
}
