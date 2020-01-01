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

package de.omnikryptec.resource.loader;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import de.codemakers.base.logger.LogLevel;
import de.codemakers.base.logger.Logger;
import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.resource.loader.annotations.DefaultLoader;
import de.omnikryptec.util.Util;
import org.reflections.Reflections;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultResourceManager implements ResourceLoader, ResourceManager {
    
    public static final long OPTION_LOAD_XML_INFO = 1 << 0;
    private static DefaultResourceManager INSTANCE = createDefaultInstance(false);
    private final Multimap<Class<? extends ResourceObject>, ResourceObject> resourceObjects = HashMultimap.create();
    private final Map<Integer, List<StagedAdvancedFile>> priorityStagedAdvancedFiles = new ConcurrentHashMap<>();
    private final List<ResourceLoader> resourceLoaders = new CopyOnWriteArrayList<>();
    private final AtomicBoolean loading = new AtomicBoolean(false);
    private ExecutorService executorService = null;
    
    public static DefaultResourceManager createDefaultInstance(final boolean searchForDefaultLoaders) {
        return createDefaultInstance(false, searchForDefaultLoaders);
    }
    
    public static DefaultResourceManager createDefaultInstance(final boolean asCurrent,
            final boolean searchForDefaultLoaders) {
        final DefaultResourceManager defaultResourceManager = new DefaultResourceManager();
        if (asCurrent) {
            INSTANCE = defaultResourceManager;
        }
        if (searchForDefaultLoaders) {
            return defaultResourceManager.addDefaultResourceLoaders(false);
        }
        // TODx Add the default ResourceLoaders to the defaultResourceManager
        return defaultResourceManager;
    }
    
    public static DefaultResourceManager createInstance() {
        return createInstance(false);
    }
    
    public static DefaultResourceManager createInstance(final boolean asCurrent) {
        final DefaultResourceManager defaultResourceManager = new DefaultResourceManager();
        if (asCurrent) {
            INSTANCE = defaultResourceManager;
        }
        return defaultResourceManager;
    }
    
    public static void resetInstance() {
        INSTANCE = null;
        createInstance(true);
    }
    
    public static DefaultResourceManager currentInstance() {
        return INSTANCE;
    }
    
    public static List<ResourceLoader> getDefaultLoaders() {
        return getDefaultLoaders(true);
    }
    
    public static List<ResourceLoader> getDefaultLoaders(final boolean distinct) {
        try {
            if (distinct) {
                final List<Class<?>> classes = new ArrayList<>();
                new Reflections(DefaultResourceManager.class.getPackage()).getTypesAnnotatedWith(DefaultLoader.class)
                        .forEach((clazz) -> {
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
                        return (ResourceLoader) clazz.newInstance();
                    } catch (final Exception ex) {
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toList());
            } else {
                return new Reflections(DefaultResourceManager.class.getPackage())
                        .getTypesAnnotatedWith(DefaultLoader.class).stream().map((clazz) -> {
                            try {
                                return (ResourceLoader) clazz.newInstance();
                            } catch (final Exception ex) {
                                return null;
                            }
                        }).filter(Objects::nonNull).collect(Collectors.toList());
            }
        } catch (final Exception ex) {
            Logger.handleError(ex);
            return new ArrayList<>();
        }
    }
    
    @Override
    public boolean load(final AdvancedFile advancedFile, final AdvancedFile superFile, final Properties properties,
            final ResourceManager resourceManager) throws Exception {
        return loadIntern(advancedFile, superFile, properties, resourceManager);
    }
    
    @Override
    public LoadingType accept(final AdvancedFile advancedFile, final AdvancedFile superFile,
            final Properties properties, final ResourceManager resourceManager) throws Exception {
        return getResourceLoadersForAdvancedFile(advancedFile, superFile, properties, resourceManager).isEmpty()
                ? LoadingType.NOT
                : LoadingType.NORMAL;
    }
    
    private boolean loadIntern(final AdvancedFile advancedFile, final AdvancedFile superFile,
            final Properties properties, final ResourceManager resourceManager) throws Exception {
        if (advancedFile == null || !advancedFile.exists()) {
            return false;
        }
        if (advancedFile.isDirectory()) {
            return advancedFile.listFiles().stream().allMatch((advancedFile_) -> {
                try {
                    return loadIntern(advancedFile_, superFile, properties, resourceManager);
                } catch (final Exception ex) {
                    Logger.handleError(ex);
                    return false;
                }
            });
        } else {
            final Properties properties_temp = null;
            final List<ResourceLoader> resourceLoaders = getResourceLoadersForAdvancedFile(advancedFile, superFile,
                    properties, resourceManager);
            if (resourceLoaders == null || resourceLoaders.isEmpty()) {
                Logger.log(String.format("Failed to load \"%s\"%s, no ResourceLoaders available", advancedFile,
                        Objects.equals(advancedFile, superFile) ? "" : String.format(" (in \"%s\")", superFile)),
                        LogLevel.WARNING);
                return false;
            }
            final AtomicBoolean mayLoaded = new AtomicBoolean(false);
            for (final ResourceLoader resourceLoader_ : resourceLoaders) {
                final LoadingType loadingType = resourceLoader_.accept(advancedFile, superFile, properties_temp,
                        resourceManager);
                if (loadingType == LoadingType.NORMAL) {
                    try {
                        this.executorService.submit(() -> {
                            try {
                                resourceLoader_.load(advancedFile, superFile, properties_temp, resourceManager);
                            } catch (final Exception ex) {
                                Logger.logError(String.format("Error while loading multithreaded intern \"%s\"%s",
                                        advancedFile, Objects.equals(advancedFile, superFile) ? ""
                                                : String.format(" (in \"%s\")", superFile)),
                                        ex);
                            }
                        });
                        mayLoaded.set(true);
                    } catch (final Exception ex) {
                        Logger.logError(String.format("Error while loading multithreaded \"%s\"%s", advancedFile,
                                Objects.equals(advancedFile, superFile) ? ""
                                        : String.format(" (in \"%s\")", superFile)),
                                ex);
                    }
                } else if (loadingType == LoadingType.OPENGL) {
                    try {
                        mayLoaded.set(resourceLoader_.load(advancedFile, superFile, properties_temp, resourceManager));
                    } catch (final Exception ex) {
                        Logger.logError(String.format("Error while loading \"%s\"%s", advancedFile,
                                Objects.equals(advancedFile, superFile) ? ""
                                        : String.format(" (in \"%s\")", superFile)),
                                ex);
                    }
                }
            }
            if (!mayLoaded.get()) {
                Logger.log(String.format("Failed to load \"%s\"%s", advancedFile,
                        Objects.equals(advancedFile, superFile) ? "" : String.format(" (in \"%s\")", superFile)),
                        LogLevel.WARNING);
            }
            return mayLoaded.get();
        }
    }
    
    public boolean loadStagedAdvancedFiles() {
        return loadStagedAdvancedFiles(true);
    }
    
    public boolean loadStagedAdvancedFiles(final boolean clearData) {
        return loadStagedAdvancedFiles(clearData, 5, TimeUnit.MINUTES);
    }
    
    public boolean loadStagedAdvancedFiles(final boolean clearData, final long timeout, final TimeUnit unit) {
        resetExecutor();
        this.loading.set(true);
        try {
            if (clearData) {
                this.loading.set(false);
                clearResources();
                this.loading.set(true);
            }
            final List<StagedAdvancedFile> stagedAdvancedFiles = getStagedAdvancedFilesSorted();
            stagedAdvancedFiles.forEach((stagedAdvancedFile) -> {
                try {
                    load(stagedAdvancedFile.getAdvancedFile(), stagedAdvancedFile.getAdvancedFile(), null, this);
                } catch (final Exception ex) {
                    Logger.handleError(ex);
                }
            });
            if (this.executorService != null) {
                this.executorService.shutdown();
                this.executorService.awaitTermination(timeout, unit);
            }
            this.loading.set(false);
            return true;
        } catch (final Exception ex) {
            this.loading.set(false);
            Logger.handleError(ex);
            return false;
        }
    }
    
    private DefaultResourceManager resetExecutor() {
        checkAndErrorIfLoading(true);
        try {
            if (this.executorService != null) {
                this.executorService.shutdownNow();
            }
            this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        } catch (final Exception ex) {
            Logger.handleError(ex);
        }
        return this;
    }
    
    public ExecutorService getExecutorService() {
        return this.executorService;
    }
    
    @Override
    public <T extends ResourceObject> T getResource(final long id) {
        checkAndErrorIfLoading(true);
        return (T) this.resourceObjects.values().stream().filter((resourceObject) -> resourceObject.getId() == id)
                .findFirst().orElse(null);
    }
    
    @Override
    public <T extends ResourceObject> T getResource(final String name) {
        checkAndErrorIfLoading(true);
        Objects.requireNonNull(name);
        return (T) this.resourceObjects.values().stream()
                .filter((resourceObject) -> name.equals(resourceObject.getName())).findFirst().orElse(null);
    }
    
    @Override
    public <T extends ResourceObject> T getResource(final Class<T> clazz, final long id) {
        checkAndErrorIfLoading(true);
        if (!containsClass(clazz)) {
            return null;
        }
        return (T) this.resourceObjects.get(clazz).stream().filter((resourceObject) -> resourceObject.getId() == id)
                .findFirst().orElse(null);
    }
    
    @Override
    public <T extends ResourceObject> T getResource(final Class<T> clazz, final String name) {
        checkAndErrorIfLoading(true);
        Objects.requireNonNull(name);
        if (!containsClass(clazz)) {
            return null;
        }
        return (T) this.resourceObjects.get(clazz).stream()
                .filter((resourceObject) -> name.equals(resourceObject.getName())).findFirst().orElse(null);
    }
    
    @Override
    public <T extends ResourceObject> Collection<T> getResources(final Class<T> clazz) {
        checkAndErrorIfLoading(true);
        if (!containsClass(clazz)) {
            return new ArrayList<>();
        }
        return this.resourceObjects.get(clazz).stream().map((resourceObject) -> (T) resourceObject)
                .collect(Collectors.toList());
    }
    
    @Override
    public Collection<ResourceObject> getAllResources() {
        checkAndErrorIfLoading(true);
        return this.resourceObjects.values().stream().collect(Collectors.toList());
    }
    
    @Override
    public boolean clearResources() {
        checkAndErrorIfLoading(true);
        this.resourceObjects.clear();
        return this.resourceObjects.isEmpty();
    }
    
    @Override
    public <T extends ResourceObject> boolean clearResources(final Class<T> clazz) {
        checkAndErrorIfLoading(true);
        Objects.requireNonNull(clazz);
        this.resourceObjects.removeAll(clazz);
        return true;
    }
    
    @Override
    public boolean removeResource(final long id) {
        final ResourceObject resourceObject = getResource(id);
        if (resourceObject == null) {
            return false;
        }
        this.resourceObjects.remove(resourceObject.getClass(), resourceObject);
        return true;
    }
    
    @Override
    public boolean removeResource(final String name) {
        Objects.requireNonNull(name);
        final ResourceObject resourceObject = getResource(name);
        if (resourceObject == null) {
            return false;
        }
        this.resourceObjects.remove(resourceObject.getClass(), resourceObject);
        return true;
    }
    
    @Override
    public boolean addResources(final ResourceObject... resourceObjects) {
        if (resourceObjects.length == 0) {
            return false;
        }
        return Stream.of(resourceObjects)
                .allMatch((resourceObject) -> this.resourceObjects.put(resourceObject.getClass(), resourceObject));
    }
    
    @Override
    public <T extends ResourceObject> boolean addResources(final Class<T> clazz, final T... resourceObjects) {
        Util.ensureNonNull(clazz);
        if (resourceObjects.length == 0) {
            return false;
        }
        return this.resourceObjects.putAll(clazz, Arrays.asList(resourceObjects));
    }
    
    public boolean containsClass(final Class<? extends ResourceObject> clazz) {
        return this.resourceObjects.containsKey(clazz);
    }
    
    public DefaultResourceManager stageAdvancedFiles(final AdvancedFile... advancedFiles) {
        return stageAdvancedFiles(0, 0L, advancedFiles);
    }
    
    public DefaultResourceManager stageAdvancedFiles(final long options, final AdvancedFile... advancedFiles) {
        return stageAdvancedFiles(0, options, advancedFiles);
    }
    
    public DefaultResourceManager stageAdvancedFiles(final int priority, final AdvancedFile... advancedFiles) {
        return stageAdvancedFiles(priority, 0L, advancedFiles);
    }
    
    public DefaultResourceManager stageAdvancedFiles(final int priority, final long options,
            final AdvancedFile... advancedFiles) {
        checkAndErrorIfLoading(true);
        if (advancedFiles == null || advancedFiles.length == 0) {
            return this;
        }
        this.priorityStagedAdvancedFiles.computeIfAbsent(priority, (key) -> new CopyOnWriteArrayList<>())
                .addAll(Stream.of(advancedFiles).map((advancedFile) -> new StagedAdvancedFile(options, advancedFile))
                        .collect(Collectors.toList()));
        return this;
    }
    
    public DefaultResourceManager clearStagedAdvancedFiles() {
        checkAndErrorIfLoading(true);
        this.priorityStagedAdvancedFiles.clear();
        return this;
    }
    
    public List<StagedAdvancedFile> getStagedAdvancedFilesSorted() {
        final List<StagedAdvancedFile> stagedAdvancedFilesSorted = new ArrayList<>();
        this.priorityStagedAdvancedFiles.keySet().stream().sorted((i_1, i_2) -> i_2 - i_1).forEach(
                (priority) -> stagedAdvancedFilesSorted.addAll(this.priorityStagedAdvancedFiles.get(priority)));
        return stagedAdvancedFilesSorted;
    }
    
    public boolean isLoading() {
        return this.loading.get();
    }
    
    public boolean checkAndErrorIfLoading(final boolean throwException) {
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
    
    public boolean addResourceObject(final ResourceObject resourceObject) {
        Util.ensureNonNull(resourceObject);
        return this.resourceObjects.put(resourceObject.getClass(), resourceObject);
    }
    
    public DefaultResourceManager addResourceLoader(final ResourceLoader resourceLoader) {
        checkAndErrorIfLoading(true);
        Util.ensureNonNull(resourceLoader);
        if (equals(resourceLoader)) {
            return this;
        }
        this.resourceLoaders.add(resourceLoader);
        return this;
    }
    
    public DefaultResourceManager removeResourceLoader(final ResourceLoader resourceLoader) {
        checkAndErrorIfLoading(true);
        this.resourceLoaders.remove(resourceLoader);
        return this;
    }
    
    public List<ResourceLoader> getResourceLoaders() {
        return this.resourceLoaders;
    }
    
    public List<ResourceLoader> getResourceLoadersForAdvancedFile(final AdvancedFile advancedFile,
            final AdvancedFile superFile, final Properties properties, final ResourceManager resourceManager) {
        return this.resourceLoaders.stream().filter((resourceLoader_) -> resourceLoader_
                .acceptWithoutException(advancedFile, superFile, properties, resourceManager) != LoadingType.NOT)
                .collect(Collectors.toList());
    }
    
    public DefaultResourceManager clearResourceLoaders() {
        checkAndErrorIfLoading(true);
        this.resourceLoaders.clear();
        return this;
    }
    
    public DefaultResourceManager addDefaultResourceLoaders() {
        getDefaultLoaders().forEach(this::addResourceLoader);
        return this;
    }
    
    public DefaultResourceManager addDefaultResourceLoaders(final boolean distinct) {
        getDefaultLoaders(distinct).forEach(this::addResourceLoader);
        return this;
    }
    
}
