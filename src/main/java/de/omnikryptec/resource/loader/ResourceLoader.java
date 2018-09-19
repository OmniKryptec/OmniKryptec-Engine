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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourceLoader implements IResourceLoader {
    
    public static final long OPTION_LOAD_XML_INFO = 1 << 0;
    
    private final Multimap<Class<? extends ResourceObject>, ResourceObject> resourceObjects = HashMultimap.create();
    private final Map<Integer, List<StagedAdvancedFile>> priorityStagedAdvancedFiles = new ConcurrentHashMap<>();
    private final List<IResourceLoader> resourceLoaders = new CopyOnWriteArrayList<>();
    private final AtomicBoolean loading = new AtomicBoolean(false);
    
    @Override
    public boolean load(AdvancedFile advancedFile, AdvancedFile superFile, Properties properties, IResourceLoader resourceLoader) throws Exception {
        return loadIntern(advancedFile, superFile, properties, resourceLoader);
    }
    
    @Override
    public LoadingType accept(AdvancedFile advancedFile, AdvancedFile superFile, Properties properties, IResourceLoader resourceLoader) throws Exception {
        return getResourceLoadersForAdvancedFile(advancedFile, superFile, properties, resourceLoader).isEmpty() ? LoadingType.NOT : LoadingType.NORMAL;
    }
    
    private boolean loadIntern(AdvancedFile advancedFile, AdvancedFile superFile, Properties properties, IResourceLoader resourceLoader) throws Exception {
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
            final AtomicBoolean loaded = new AtomicBoolean(false);
            for (IResourceLoader resourceLoader_ : resourceLoaders) {
                //TODO
            }
            if (!loaded.get()) {
                Logger.log(String.format("Failed to load \"%s\"%s", advancedFile, Objects.equals(advancedFile, superFile) ? "" : String.format(" (in \"%s\")", superFile)), LogLevel.WARNING);
            }
            return loaded.get();
        }
    }
    
    @Override
    public <T extends ResourceObject> T getResource(long id) {
        return (T) resourceObjects.values().stream().filter((resourceObject) -> resourceObject.getId() == id).findFirst().orElse(null);
    }
    
    @Override
    public <T extends ResourceObject> T getResource(String name) {
        Objects.requireNonNull(name);
        return (T) resourceObjects.values().stream().filter((resourceObject) -> name.equals(resourceObject.getName())).findFirst().orElse(null);
    }
    
    @Override
    public <T extends ResourceObject> T getResource(Class<T> clazz, long id) {
        if (!containsClass(clazz)) {
            return null;
        }
        return (T) resourceObjects.get(clazz).stream().filter((resourceObject) -> resourceObject.getId() == id).findFirst().orElse(null);
    }
    
    @Override
    public <T extends ResourceObject> T getResource(Class<T> clazz, String name) {
        Objects.requireNonNull(name);
        if (!containsClass(clazz)) {
            return null;
        }
        return (T) resourceObjects.get(clazz).stream().filter((resourceObject) -> name.equals(resourceObject.getName())).findFirst().orElse(null);
    }
    
    @Override
    public <T extends ResourceObject> List<T> getResources(Class<T> clazz) {
        if (!containsClass(clazz)) {
            return new ArrayList<>();
        }
        return resourceObjects.get(clazz).stream().map((resourceObject) -> (T) resourceObject).collect(Collectors.toList());
    }
    
    @Override
    public List<ResourceObject> getAllResources() {
        return resourceObjects.values().stream().collect(Collectors.toList());
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
    
    public List<IResourceLoader> getResourceLoadersForAdvancedFile(AdvancedFile advancedFile, AdvancedFile superFile, Properties properties, IResourceLoader resourceLoader) {
        return resourceLoaders.stream().filter((resourceLoader_) -> resourceLoader_.acceptWithoutException(advancedFile, superFile, properties, resourceLoader) != LoadingType.NOT).collect(Collectors.toList());
    }
    
    public ResourceLoader clearResourceLoaders() {
        checkAndErrorIfLoading(true);
        resourceLoaders.clear();
        return this;
    }
    
}
