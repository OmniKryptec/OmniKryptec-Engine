package omnikryptec.resource.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import de.codemakers.io.file.AdvancedFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import omnikryptec.resource.loader.annotations.DefaultLoader;
import omnikryptec.resource.texture.SimpleTexture;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;
import org.reflections.Reflections;

/**
 * ResourceLoader
 *
 * @author Panzer1119 &amp; pcfreak9000
 */
public class ResourceLoader implements Loader {

    public static final SimpleTexture MISSING_TEXTURE = SimpleTexture.newTexture(new AdvancedFile(true, "", "omnikryptec", "resource", "loader", "missing_texture.png"));

    private static ResourceLoader RESOURCELOADER;

    public static final ResourceLoader createInstanceDefault(boolean ascurrent) {
        final ResourceLoader loader = new ResourceLoader();
        if (ascurrent) {
            RESOURCELOADER = loader;
        }
        return loader.addDefaultLoaders();
    }

    public static final ResourceLoader createInstance(boolean ascurrent) {
        final ResourceLoader loader = new ResourceLoader();
        if (ascurrent) {
            RESOURCELOADER = loader;
        }
        return loader;
    }

    public static final void resetInstance() {
        RESOURCELOADER = null;
    }

    public static final ResourceLoader currentInstance() {
        return RESOURCELOADER;
    }

    public final static <T extends ResourceObject> T getResourceDefault(String name) {
        return currentInstance().getResource(name);
    }

    public final static <T extends ResourceObject> T getResourceDefault(Class<? extends T> clazz, String name) {
        return currentInstance().getResource(clazz, name);
    }

    private ExecutorService executor = null;
    private final ConcurrentHashMap<String, ResourceObject> loadedData = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, ConcurrentLinkedQueue<AdvancedFile>> priorityStagedAdvancedFiles = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<Loader> loaders = new ConcurrentLinkedQueue<>();
    private boolean isLoading = false;
    private ResourceObject temp_simpleTexture;

    @Override
    public final boolean load(AdvancedFile advancedFile, AdvancedFile superFile, ResourceLoader resourceLoader) {
        return loadIntern(advancedFile, superFile, resourceLoader);
    }

    @Override
    public final LoadingType accept(AdvancedFile advancedFile, AdvancedFile superFile, ResourceLoader resourceLoader) {
        return !getLoadersForAdvancedFile(advancedFile, superFile, resourceLoader).isEmpty() ? LoadingType.NORMAL : LoadingType.NOT;
    }

    public final boolean addRessourceObject(String name, ResourceObject resourceObject) {
        if (name != null && !name.isEmpty() && resourceObject != null) {
            loadedData.put(name, resourceObject);
            return true;
        } else {
            return false;
        }
    }

    private final boolean loadIntern(AdvancedFile advancedFile, AdvancedFile superFile, ResourceLoader resourceLoader) {
        try {
            if (advancedFile == null || !advancedFile.exists()) {
                return false;
            }
            if (advancedFile.isDirectory()) {
                advancedFile.listAdvancedFiles().stream().forEach((af) -> {
                    loadIntern(af, superFile, resourceLoader);
                });
                return true;
            } else {
                final List<Loader> loadersForAdvancedFile = getLoadersForAdvancedFile(advancedFile, superFile, resourceLoader);
                if (loadersForAdvancedFile.isEmpty()) {
                    Logger.log(String.format("Failed to load \"%s\"%s, no Loaders available", advancedFile, (Objects.equals(advancedFile, superFile) ? "" : String.format(" (in \"%s\")", superFile))), LogLevel.WARNING);
                    return false;
                }
                boolean loaded = false;
                for (Loader loader : loadersForAdvancedFile) {
                    final LoadingType loadingType = loader.accept(advancedFile, superFile, resourceLoader);
                    if (loadingType == LoadingType.NORMAL) {
                        try {
                            executor.submit(() -> loader.load(advancedFile, superFile, resourceLoader));
                            loaded = true;
                        } catch (Exception ex) {
                            if (Logger.isDebugMode()) {
                                Logger.logErr(String.format("Error while loading multithreaded \"%s\"%s: %s", advancedFile, (Objects.equals(advancedFile, superFile) ? "" : String.format(" (in \"%s\")", superFile)), ex), ex);
                            }
                        }
                    } else if (loadingType == LoadingType.OPENGL) {
                        try {
                            if (loader.load(advancedFile, superFile, resourceLoader)) {
                                loaded = true;
                            }
                        } catch (Exception ex) {
                            if (Logger.isDebugMode()) {
                                Logger.logErr(String.format("Error while loading \"%s\"%s: %s", advancedFile, (Objects.equals(advancedFile, superFile) ? "" : String.format(" (in \"%s\")", superFile)), ex), ex);
                            }
                        }
                    }
                }
                if (!loaded) {
                    Logger.log(String.format("Failed to load \"%s\"%s", advancedFile, (Objects.equals(advancedFile, superFile) ? "" : String.format(" (in \"%s\")", superFile))), LogLevel.WARNING);
                }
                return loaded;
            }
        } catch (Exception ex) {
            Logger.logErr(String.format("Error while loading staged AdvancedFile \"%s\"%s: %s", advancedFile, (Objects.equals(advancedFile, superFile) ? "" : String.format(" (in \"%s\")", superFile)), ex), ex);
            return false;
        }
    }

    public final void loadStagedAdvancedFiles(boolean clearData) {
        loadStagedAdvancedFiles(clearData, 1, TimeUnit.MINUTES);
    }

    public final void loadStagedAdvancedFiles(boolean clearData, long timeout, TimeUnit unit) {
        resetExecutor();
        isLoading = true;
        try {
            if (clearData) {
                loadedData.values().stream().forEach((i) -> i.delete());
                loadedData.clear();
            }
            getStagedAdvancedFilesSorted().stream().forEach((advancedFile) -> load(advancedFile, advancedFile, this));
            if (executor != null) {
                executor.shutdown();
                executor.awaitTermination(timeout, unit);
            }
        } catch (Exception ex) {
            Logger.logErr("Error while loading staged AdvancedFiles: " + ex, ex);
        }
        isLoading = false;
    }

    public final boolean isLoading() {
        return isLoading;
    }

    public final ResourceLoader addLoader(Loader loader) {
        if (isLoading || loader == null || loader.equals(this) || loader instanceof ResourceLoader) {
            return this;
        }
        loaders.add(loader);
        return this;
    }

    public final ResourceLoader removeLoader(Loader loader) {
        if (isLoading || loader == null || loader.equals(this) || loader instanceof ResourceLoader) {
            return this;
        }
        loaders.remove(loader);
        return this;
    }

    public final List<Loader> getLoaders() {
        return new ArrayList(loaders);
    }

    public final List<Loader> getLoadersForAdvancedFile(AdvancedFile advancedFile, AdvancedFile superFile, ResourceLoader resourceLoader) {
        return loaders.stream().filter((loader) -> loader.accept(advancedFile, superFile, resourceLoader) != LoadingType.NOT).collect(Collectors.toList());
    }

    public final ResourceLoader clearLoaders() {
        if (isLoading) {
            return this;
        }
        loaders.clear();
        return this;
    }

    public final ResourceLoader addDefaultLoaders() {
        getDefaultLoaders().forEach(this::addLoader);
        return this;
    }

    public final ResourceLoader stageAdvancedFiles(AdvancedFile... advancedFiles) {
        return stageAdvancedFiles(0, advancedFiles);
    }

    public final ResourceLoader stageAdvancedFiles(int priority, AdvancedFile... advancedFiles) {
        if (isLoading || advancedFiles == null || advancedFiles.length == 0) {
            return this;
        }
        priorityStagedAdvancedFiles.computeIfAbsent(priority, (key) -> new ConcurrentLinkedQueue<>()).addAll(Arrays.asList(advancedFiles));
        return this;
    }

    public final Loader clearStagedAdvancedFiles() {
        priorityStagedAdvancedFiles.clear();
        return this;
    }

    public final List<AdvancedFile> getStagedAdvancedFilesSorted() {
        final List<AdvancedFile> stagedAdvancedFiles = new ArrayList<>();
        priorityStagedAdvancedFiles.keySet().stream().sorted((i1, i2) -> i2 - i1).forEach((i) -> stagedAdvancedFiles.addAll(priorityStagedAdvancedFiles.get(i)));
        return stagedAdvancedFiles;
    }

    public final HashMap<String, ResourceObject> getLoadedData() {
        return new HashMap<>(loadedData);
    }

    @SuppressWarnings("unchecked")
    public final <T extends ResourceObject> T getResource(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        try {
            return (T) loadedData.get(name);
        } catch (ClassCastException ex) {
            return null;
        }
    }

    public final SimpleTexture getTexture(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        try {
            temp_simpleTexture = loadedData.get(name);
            return temp_simpleTexture == null ? MISSING_TEXTURE : (SimpleTexture) temp_simpleTexture;
        } catch (ClassCastException ex) {
            return MISSING_TEXTURE;
        }
    }

    @SuppressWarnings("unchecked")
    public final <T extends ResourceObject> T getResource(Class<? extends T> c, String name) {
        if (c == null || name == null || name.isEmpty()) {
            return null;
        }
        ResourceObject data = loadedData.get(name);
        if (Logger.isDebugMode() && false) {
            Logger.log("FOUND: " + data + " FOR: " + name);
            Logger.log(c + " VS " + (data == null ? "null" : data.getClass()));
        }
        if (data == null && SimpleTexture.class.isAssignableFrom(c)) {
            return (T) MISSING_TEXTURE;
        }
        if (data == null || (!c.isAssignableFrom(data.getClass()) && c != data.getClass())) { //TODO Gucken ob das isAssignableFrom so richtig herum ist
            if (data != null) {
                Logger.log("Wrong Class for " + data);
            }
            return null;
        }
        return (T) data;
    }

    public final <T extends ResourceObject> List<T> getResources(Class<? extends T> c) {
        return getResources(c, null);
    }

    @SuppressWarnings("unchecked")
    private final <T extends ResourceObject> List<T> getResources(Class<? extends T> c, ArrayList<T> dataOld) {
        if (dataOld == null) {
            dataOld = new ArrayList<>();
        }
        if (isLoading) {
            return dataOld;
        }
        final List<T> data = dataOld;
        List<ResourceObject> d = loadedData.values().stream().filter((object) -> (object != null && (c == null || c.isAssignableFrom(object.getClass())))).collect(Collectors.toList()); //TODO Gucken ob das isAssignableFrom so richtig herum ist
        d.stream().forEach((object) -> data.add((T) object));
        d.clear();
        return data;
    }

    public final <T extends ResourceObject> void actions(Class<T> resClass, Consumer<T> action) {
        actions((o) -> resClass.isInstance(o), action);
    }

    @SuppressWarnings("unchecked")
    public final <T extends ResourceObject> void actions(Predicate<T> pre, Consumer<T> action) {
        loadedData.values().stream().filter((Predicate<? super ResourceObject>) pre).forEach((Consumer<? super ResourceObject>) action);
    }

    private final Loader resetExecutor() {
        try {
            if (isLoading) {
                return this;
            }
            if (executor != null) {
                executor.shutdownNow();
            }
            executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        } catch (Exception ex) {
            Logger.logErr("Error while resetting ExecutorService for ResourceLoader: " + ex, ex);
        }
        return this;
    }

    public static final List<Loader> getDefaultLoaders() {
        return getDefaultLoaders(true);
    }

    public static final List<Loader> getDefaultLoaders(boolean distinct) {
        try {
            if (!distinct) {
                return new Reflections(ResourceLoader.class.getPackage()).getTypesAnnotatedWith(DefaultLoader.class).stream().map((clazz) -> {
                    try {
                        return (Loader) clazz.newInstance();
                    } catch (Exception ex) {
                        if (Logger.isDebugMode()) {
                            Logger.logErr(String.format("Error while instancing \"%s\": %s", clazz, ex), ex);
                        }
                        return null;
                    }
                }).collect(Collectors.toList());
            } else {
                final List<Class<?>> classes = new ArrayList<>();
                new Reflections(ResourceLoader.class.getPackage()).getTypesAnnotatedWith(DefaultLoader.class).forEach((clazz) -> {
                    final DefaultLoader defaultLoader = clazz.getAnnotation(DefaultLoader.class);
                    int priority = defaultLoader.priority();
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
                        return (Loader) clazz.newInstance();
                    } catch (Exception ex) {
                        if (Logger.isDebugMode()) {
                            Logger.logErr(String.format("Error while instancing \"%s\": %s", clazz, ex), ex);
                        }
                        return null;
                    }
                }).collect(Collectors.toList());
            }
        } catch (Exception ex) {
            if (Logger.isDebugMode()) {
                Logger.logErr("Error while retrieving DefaultLoaders: " + ex, ex);
            }
            return new ArrayList<>();
        }
    }

}
