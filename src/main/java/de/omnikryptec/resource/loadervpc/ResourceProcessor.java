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

package de.omnikryptec.resource.loadervpc;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.util.ExecutorsUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ResourceProcessor {

    private Collection<LoadingProgressCallback> callbacks;
    private Collection<ResourceLoader<?>> loadersThreadGroup;
    private Collection<ResourceLoader<?>> loadersMainThread;
    private List<ResourceLocation> staged;
    private ResourceProvider resourceProvider;
    private ResourceNameGenerator resourceNameGenerator;

    public ResourceProcessor() {
        this(new DefaultResourceProvider(), ResourceNameGenerator.defaultNameGen());
    }

    public ResourceProcessor(ResourceProvider resProv, ResourceNameGenerator nameGen) {
        this.resourceProvider = resProv;
        this.resourceNameGenerator = nameGen;
        this.callbacks = new ArrayList<>();
        this.staged = new ArrayList<>();
        this.loadersThreadGroup = new ArrayList<>();
        this.loadersMainThread = new ArrayList<>();
    }

    public ResourceProvider getProvider() {
        return resourceProvider;
    }

    public void addCallback(LoadingProgressCallback callback) {
        callbacks.add(callback);
    }

    public void stage(AdvancedFile file) {
        stage(new ResourceLocation(0, file));
    }

    public void stage(ResourceLocation file) {
        staged.add(file);
    }

    public void clearStaged() {
        staged.clear();
    }

    public void processStaged(boolean override) {
        new Processor(override).processStaged();
    }

    public void instantLoad(boolean override, AdvancedFile file) {
        new Processor(override).loadSimple(file);
    }

    private void addResource(Object res, AdvancedFile file, AdvancedFile superfile, boolean override) {
        if (res != null) {
            String name = resourceNameGenerator.genName(res, file, superfile);
            resourceProvider.add(res, name, override);
        }
    }

    public void addLoader(ResourceLoader<?> loader) {
        if (loader.requiresMainThread()) {
            loadersMainThread.add(loader);
        } else {
            loadersThreadGroup.add(loader);
        }
    }

    public static class ResourceLocation implements Comparable<ResourceLocation> {

        private int priority;
        private AdvancedFile loc;

        public ResourceLocation(int priority, AdvancedFile file) {
            this.priority = priority;
            this.loc = file;
        }

        public int getPriority() {
            return priority;
        }

        public AdvancedFile getFile() {
            return loc;
        }

        @Override
        public int compareTo(ResourceLocation o) {
            return priority - o.priority;
        }
    }

    /*
     * Kind of messy, in here
     */
    private class Processor {
        private boolean override;
        private ExecutorService executorService = null;

        /* for #notifyProcessed() */
        private int localprocessed;
        /**/

        private Processor(boolean override) {
            this.override = override;
            this.executorService = ExecutorsUtil.newFixedThreadPool(ExecutorsUtil.getAvailableThreads());
        }

        private void processStaged() {
            Collections.sort(staged);
            int[] localmaxs = new int[staged.size()];
            int size = 0;
            for (int i = 0; i < staged.size(); i++) {
                localmaxs[i] = countFiles(staged.get(i).getFile(), 0);
                size += localmaxs[i];
            }
            notifyStart(size, staged.size());
            for (int i = 0; i < staged.size(); i++) {
                AdvancedFile file = staged.get(i).getFile();
                notifyStage(file, i, localmaxs[i]);
                processStagedIntern(file, file);
            }
            notifyDone();
        }

        private void processStagedIntern(AdvancedFile file, AdvancedFile superFile) {
            if (file.isDirectory()) {
                for (AdvancedFile subFile : file.listFiles()) {
                    processStagedIntern(subFile, superFile);
                }
            } else {
                load(true, file, superFile);
                notifyProcessed(file);
            }
        }

        private void loadSimple(AdvancedFile file) {
            int size = countFiles(file, 0);
            notifyStart(size, 1);
            notifyStage(file, 0, size);
            loadSimpleIntern(size > 1, file, file);
            notifyDone();
        }

        private void loadSimpleIntern(boolean exec, AdvancedFile file, AdvancedFile superFile) {
            if (file.isDirectory()) {
                for (AdvancedFile subFile : file.listFiles()) {
                    loadSimpleIntern(exec, subFile, superFile);
                }
            } else {
                load(exec, file, superFile);
                notifyProcessed(file);
            }
        }

        private int countFiles(AdvancedFile file, int old) {
            if (file.isDirectory()) {
                List<AdvancedFile> filesHere = file.listFiles();
                for (AdvancedFile f : filesHere) {
                    old = countFiles(f, old);
                }
            } else {
                old++;
            }
            return old;
        }

        private void notifyStage(AdvancedFile file, int stagenumber, int localmax) {
            localprocessed = 0;
            for (LoadingProgressCallback callback : callbacks) {
                callback.onStageChange(file, localmax, stagenumber);
            }
        }

        private void notifyProcessed(AdvancedFile file) {
            localprocessed++;
            for (LoadingProgressCallback callback : callbacks) {
                callback.onProgressChange(file, localprocessed);
            }
        }

        private void notifyStart(int size, int maxstages) {
            for (LoadingProgressCallback callback : callbacks) {
                callback.onLoadingStart(size, maxstages);
            }
        }

        private void notifyDone() {
            ExecutorsUtil.shutdown(executorService, 1, TimeUnit.HOURS);
            for (LoadingProgressCallback callback : callbacks) {
                callback.onLoadingDone();
            }
        }

        private void load(boolean useExecutor, AdvancedFile file, AdvancedFile superfile) {
            Runnable r = () -> {
                for (ResourceLoader<?> loader : loadersThreadGroup) {
                    if (file.getName().matches(loader.getFileNameRegex())) {
                        Object res = null;
                        try {
                            res = loader.load(file);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        addResource(res, file, superfile, override);
                    }
                }
            };
            if (useExecutor) {
                executorService.submit(r);
            } else {
                r.run();
            }
            for (ResourceLoader<?> loader : loadersMainThread) {
                if (file.getName().matches(loader.getFileNameRegex())) {
                    Object res = null;
                    try {
                        res = loader.load(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    addResource(res, file, superfile, override);
                }
            }
        }
    }

}
