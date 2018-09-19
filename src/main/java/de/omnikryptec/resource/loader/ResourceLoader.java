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

package de.omnikryptec.resource.loader;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import de.codemakers.base.logger.LogLevel;
import de.codemakers.base.logger.Logger;
import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.resource.loader.annotations.DefaultLoader;
import org.reflections.Reflections;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourceLoader implements IResourceLoader, ResourceManager {
    
    public static final long OPTION_LOAD_XML_INFO = 1 << 0;
    private static ResourceLoader INSTANCE = createDefaultInstance(false);
    private final Multimap<Class<? extends ResourceObject>, ResourceObject> resourceObjects = HashMultimap.create();
    private final Map<Integer, List<StagedAdvancedFile>> priorityStagedAdvancedFiles = new ConcurrentHashMap<>();
    private final List<IResourceLoader> resourceLoaders = new CopyOnWriteArrayList<>();
    private final AtomicBoolean loading = new AtomicBoolean(false);
    private ExecutorService executorService = null;
    
    public static ResourceLoader createDefaultInstance(boolean searchForDefaultLoaders) {
        return createDefaultInstance(false, searchForDefaultLoaders);
    }
    
    public static ResourceLoader createDefaultInstance(boolean asCurrent, boolean searchForDefaultLoaders) {
        final ResourceLoader resourceLoader = new ResourceLoader();
        if (asCurrent) {
            INSTANCE = resourceLoader;
        }
        if (searchForDefaultLoaders) {
            return resourceLoader.addDefaultResourceLoaders(false);
        }
        //TODO Add the default ResourceLoaders to the resourceLoader
        return resourceLoader;
    }
    
    public static ResourceLoader createInstance() {
        return createInstance(false);
    }
    
    public static ResourceLoader createInstance(boolean asCurrent) {
        final ResourceLoader resourceLoader = new ResourceLoader();
        if (asCurrent) {
            INSTANCE = resourceLoader;
        }
        return resourceLoader;
    }
    
    public static void resetInstance() {
        INSTANCE = null;
        createInstance(true);
    }
    
    public static ResourceLoader currentInstance() {
        return INSTANCE;
    }
    
    public static List<IResourceLoader> getDefaultLoaders() {
        return getDefaultLoaders(true);
    }
    
    public static List<IResourceLoader> getDefaultLoaders(boolean distinct) {
        try {
            if (distinct) {
                final List<Class<?>> classes = new ArrayList<>();
                new Reflections(ResourceLoader.class.getPackage()).getTypesAnnotatedWith(DefaultLoader.class).forEach((clazz) -> {
                    final int priority = clazz.getAnnotation(DefaultLoader.class).priority();
                    if (classes.isEmpty()) {
                        classes.add(clazz);
                    } else {
                        final Iterator<Class<?>> iterator = classes.iterator();
                        boolean found = false;
                        while (iterator.hasNext()) {
                            final Class<?> clazz_ = iterator.next();
                            if (clazz_ != clazz && clazz_.getSimpleName().equals(clazz.getSimpleName())) {
                                final int priority_ = clazz_.getAnnotation(DefaultLoader.class).priority();
                                found = true;
                                if (priority_ < priority) {
                                    iterator.remove();
                                    classes.add(clazz);
                                } else if (priority_ == priority) {
                                    classes.add(clazz);
                                }
                            }
                        }
                        if (!found) {
                            classes.add(clazz);
                        }
                    }
                });
                return classes.stream().map((clazz) -> {
                    try {
                        return (IResourceLoader) clazz.newInstance();
                    } catch (Exception ex) {
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toList());
            } else {
                return new Reflections(ResourceLoader.class.getPackage()).getTypesAnnotatedWith(DefaultLoader.class).stream().map((clazz) -> {
                    try {
                        return (IResourceLoader) clazz.newInstance();
                    } catch (Exception ex) {
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toList());
            }
        } catch (Exception ex) {
            Logger.handleError(ex);
            return new ArrayList<>();
        }
    }
    
    @Override
    public boolean load(AdvancedFile advancedFile, AdvancedFile superFile, Properties properties, ResourceLoader resourceLoader) throws Exception {
        return loadIntern(advancedFile, superFile, properties, resourceLoader);
    }
    
    @Override
    public LoadingType accept(AdvancedFile advancedFile, AdvancedFile superFile, Properties properties, ResourceLoader resourceLoader) throws Exception {
        return getResourceLoadersForAdvancedFile(advancedFile, superFile, properties, resourceLoader).isEmpty() ? LoadingType.NOT : LoadingType.NORMAL;
    }
    
    private boolean loadIntern(AdvancedFile advancedFile, AdvancedFile superFile, Properties properties, ResourceLoader resourceLoader) throws Exception {
        if (advancedFile == null || !advancedFile.exists()) {
            return false;
        }
        if (advancedFile.isDirectory()) {
            return advancedFile.listFiles().stream().allMatch((advancedFile_) -> {
                try {
                    return loadIntern(advancedFile_, superFile, properties, resourceLoader);
                } catch (Exception ex) {
                    Logger.handleError(ex);
                    return false;
                }
            });
        } else {
            final Properties properties_temp = null;
            final List<IResourceLoader> resourceLoaders = getResourceLoadersForAdvancedFile(advancedFile, superFile, properties, resourceLoader);
            if (resourceLoaders == null || resourceLoaders.isEmpty()) {
                Logger.log(String.format("Failed to load \"%s\"%s, no ResourceLoaders available", advancedFile, Objects.equals(advancedFile, superFile) ? "" : String.format(" (in \"%s\")", superFile)), LogLevel.WARNING);
                return false;
            }
            final AtomicBoolean mayLoaded = new AtomicBoolean(false);
            for (IResourceLoader resourceLoader_ : resourceLoaders) {
                final LoadingType loadingType = resourceLoader_.accept(advancedFile, superFile, properties_temp, resourceLoader);
                if (loadingType == LoadingType.NORMAL) {
                    try {
                        executorService.submit(() -> {
                            try {
                                resourceLoader_.load(advancedFile, superFile, properties_temp, resourceLoader);
                            } catch (Exception ex) {
                                Logger.logErr(String.format("Error while loading multithreaded intern \"%s\"%s", advancedFile, Objects.equals(advancedFile, superFile) ? "" : String.format(" (in \"%s\")", superFile)), ex);
                            }
                        });
                        mayLoaded.set(true);
                    } catch (Exception ex) {
                        Logger.logErr(String.format("Error while loading multithreaded \"%s\"%s", advancedFile, Objects.equals(advancedFile, superFile) ? "" : String.format(" (in \"%s\")", superFile)), ex);
                    }
                } else if (loadingType == LoadingType.OPENGL) {
                    try {
                        mayLoaded.set(resourceLoader_.load(advancedFile, superFile, properties_temp, resourceLoader));
                    } catch (Exception ex) {
                        Logger.logErr(String.format("Error while loading \"%s\"%s", advancedFile, Objects.equals(advancedFile, superFile) ? "" : String.format(" (in \"%s\")", superFile)), ex);
                    }
                }
            }
            if (!mayLoaded.get()) {
                Logger.log(String.format("Failed to load \"%s\"%s", advancedFile, Objects.equals(advancedFile, superFile) ? "" : String.format(" (in \"%s\")", superFile)), LogLevel.WARNING);
            }
            return mayLoaded.get();
        }
    }
    
    public boolean loadStagedAdvancedFiles() {
        return loadStagedAdvancedFiles(true);
    }
    
    public boolean loadStagedAdvancedFiles(boolean clearData) {
        return loadStagedAdvancedFiles(clearData, 5, TimeUnit.MINUTES);
    }
    
    public boolean loadStagedAdvancedFiles(boolean clearData, long timeout, TimeUnit unit) {
        resetExecutor();
        loading.set(true);
        try {
            if (clearData) {
                loading.set(false);
                clearResources();
                loading.set(true);
            }
            final List<StagedAdvancedFile> stagedAdvancedFiles = getStagedAdvancedFilesSorted();
            stagedAdvancedFiles.forEach((stagedAdvancedFile) -> {
                try {
                    load(stagedAdvancedFile.getAdvancedFile(), stagedAdvancedFile.getAdvancedFile(), null, this);
                } catch (Exception ex) {
                    Logger.handleError(ex);
                }
            });
            if (executorService != null) {
                executorService.shutdown();
                executorService.awaitTermination(timeout, unit);
            }
            loading.set(false);
            return true;
        } catch (Exception ex) {
            loading.set(false);
            Logger.handleError(ex);
            return false;
        }
    }
    
    private ResourceLoader resetExecutor() {
        checkAndErrorIfLoading(true);
        try {
            if (executorService != null) {
                executorService.shutdownNow();
            }
            executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        } catch (Exception ex) {
            Logger.handleError(ex);
        }
        return this;
    }
    
    public ExecutorService getExecutorService() {
        return executorService;
    }
    
    @Override
    public <T extends ResourceObject> T getResource(long id) {
        checkAndErrorIfLoading(true);
        return (T) resourceObjects.values().stream().filter((resourceObject) -> resourceObject.getId() == id).findFirst().orElse(null);
    }
    
    @Override
    public <T extends ResourceObject> T getResource(String name) {
        checkAndErrorIfLoading(true);
        Objects.requireNonNull(name);
        return (T) resourceObjects.values().stream().filter((resourceObject) -> name.equals(resourceObject.getName())).findFirst().orElse(null);
    }
    
    @Override
    public <T extends ResourceObject> T getResource(Class<T> clazz, long id) {
        checkAndErrorIfLoading(true);
        if (!containsClass(clazz)) {
            return null;
        }
        return (T) resourceObjects.get(clazz).stream().filter((resourceObject) -> resourceObject.getId() == id).findFirst().orElse(null);
    }
    
    @Override
    public <T extends ResourceObject> T getResource(Class<T> clazz, String name) {
        checkAndErrorIfLoading(true);
        Objects.requireNonNull(name);
        if (!containsClass(clazz)) {
            return null;
        }
        return (T) resourceObjects.get(clazz).stream().filter((resourceObject) -> name.equals(resourceObject.getName())).findFirst().orElse(null);
    }
    
    @Override
    public <T extends ResourceObject> Collection<T> getResources(Class<T> clazz) {
        checkAndErrorIfLoading(true);
        if (!containsClass(clazz)) {
            return new ArrayList<>();
        }
        return resourceObjects.get(clazz).stream().map((resourceObject) -> (T) resourceObject).collect(Collectors.toList());
    }
    
    @Override
    public Collection<ResourceObject> getAllResources() {
        checkAndErrorIfLoading(true);
        return resourceObjects.values().stream().collect(Collectors.toList());
    }
    
    @Override
    public boolean clearResources() {
        checkAndErrorIfLoading(true);
        resourceObjects.clear();
        return resourceObjects.isEmpty();
    }
    
    @Override
    public <T extends ResourceObject> boolean clearResources(Class<T> clazz) {
        checkAndErrorIfLoading(true);
        Objects.requireNonNull(clazz);
        resourceObjects.removeAll(clazz);
        return true;
    }
    
    @Override
    public boolean removeResource(long id) {
        checkAndErrorIfLoading(true);
        final ResourceObject resourceObject = getResource(id);
        if (resourceObject == null) {
            return false;
        }
        resourceObjects.remove(resourceObject.getClass(), resourceObject);
        return true;
    }
    
    @Override
    public boolean removeResource(String name) {
        checkAndErrorIfLoading(true);
        Objects.requireNonNull(name);
        final ResourceObject resourceObject = getResource(name);
        if (resourceObject == null) {
            return false;
        }
        resourceObjects.remove(resourceObject.getClass(), resourceObject);
        return false;
    }
    
    @Override
    public boolean addResources(ResourceObject... resourceObjects) {
        checkAndErrorIfLoading(true);
        return false;
    }
    
    public boolean containsClass(Class<? extends ResourceObject> clazz) {
        return resourceObjects.containsKey(clazz);
    }
    
    public ResourceLoader stageAdvancedFiles(long options, AdvancedFile... advancedFiles) {
        return stageAdvancedFiles(0, options, advancedFiles);
    }
    
    public ResourceLoader stageAdvancedFiles(int priority, AdvancedFile... advancedFiles) {
        return stageAdvancedFiles(priority, 0L, advancedFiles);
    }
    
    public ResourceLoader stageAdvancedFiles(int priority, long options, AdvancedFile... advancedFiles) {
        checkAndErrorIfLoading(true);
        if (advancedFiles == null || advancedFiles.length == 0) {
            return this;
        }
        priorityStagedAdvancedFiles.computeIfAbsent(priority, (key) -> new CopyOnWriteArrayList<>()).addAll(Stream.of(advancedFiles).map((advancedFile) -> new StagedAdvancedFile(options, advancedFile)).collect(Collectors.toList()));
        return this;
    }
    
    public ResourceLoader clearStagedAdvancedFiles() {
        checkAndErrorIfLoading(true);
        priorityStagedAdvancedFiles.clear();
        return this;
    }
    
    public List<StagedAdvancedFile> getStagedAdvancedFilesSorted() {
        final List<StagedAdvancedFile> stagedAdvancedFilesSorted = new ArrayList<>();
        priorityStagedAdvancedFiles.keySet().stream().sorted((i_1, i_2) -> i_2 - i_1).forEach((priority) -> stagedAdvancedFilesSorted.addAll(priorityStagedAdvancedFiles.get(priority)));
        return stagedAdvancedFilesSorted;
    }
    
    public boolean isLoading() {
        return loading.get();
    }
    
    public boolean checkAndErrorIfLoading(boolean throwException) {
        if (isLoading()) {
            if (throwException) {
                throw new IllegalStateException(getClass().getSimpleName() + " is currently loading");
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
    
    public boolean addResourceObject(ResourceObject resourceObject) {
        Objects.requireNonNull(resourceObject);
        return resourceObjects.put(resourceObject.getClass(), resourceObject);
    }
    
    public ResourceLoader addResourceLoader(IResourceLoader resourceLoader) {
        checkAndErrorIfLoading(true);
        Objects.requireNonNull(resourceLoader);
        if (equals(resourceLoader)) {
            return this;
        }
        resourceLoaders.add(resourceLoader);
        return this;
    }
    
    public ResourceLoader removeResourceLoader(IResourceLoader resourceLoader) {
        checkAndErrorIfLoading(true);
        resourceLoaders.remove(resourceLoader);
        return this;
    }
    
    public List<IResourceLoader> getResourceLoaders() {
        return resourceLoaders;
    }
    
    public List<IResourceLoader> getResourceLoadersForAdvancedFile(AdvancedFile advancedFile, AdvancedFile superFile, Properties properties, ResourceLoader resourceLoader) {
        return resourceLoaders.stream().filter((resourceLoader_) -> resourceLoader_.acceptWithoutException(advancedFile, superFile, properties, resourceLoader) != LoadingType.NOT).collect(Collectors.toList());
    }
    
    public ResourceLoader clearResourceLoaders() {
        checkAndErrorIfLoading(true);
        resourceLoaders.clear();
        return this;
    }
    
    public ResourceLoader addDefaultResourceLoaders() {
        getDefaultLoaders().forEach(this::addResourceLoader);
        return this;
    }
    
    public ResourceLoader addDefaultResourceLoaders(boolean distinct) {
        getDefaultLoaders(distinct).forEach(this::addResourceLoader);
        return this;
    }
    
}
