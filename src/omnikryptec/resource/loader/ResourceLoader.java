package omnikryptec.resource.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import de.codemakers.io.file.AdvancedFile;
import de.codemakers.util.ArrayUtil;
import de.codemakers.util.ArrayUtil.Filter;
import omnikryptec.resource.texture.SimpleTexture;
import omnikryptec.util.action.Action;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

/**
 * ResourceLoader
 *
 * @author Panzer1119 &amp; pcfreak9000
 */
public class ResourceLoader implements Loader {

    public static final SimpleTexture MISSING_TEXTURE = SimpleTexture.newTexture(new AdvancedFile(true, "", "omnikryptec", "resource", "loader", "missing_texture.png"));

    private static ResourceLoader resourceLoader;

    public static final ResourceLoader createInstanceDefault(boolean ascurrent) {
        ResourceLoader loader = new ResourceLoader();
        if (ascurrent) {
            resourceLoader = loader;
        }
        loader.addLoader(new DefaultModelLoader());
        loader.addLoader(new DefaultTextureLoader());
        loader.addLoader(new DefaultAnimationLoader());
        loader.addLoader(new DefaultAnimatedModelDataLoader());
        return loader;
    }

    public static final ResourceLoader createInstance(boolean ascurrent) {
        ResourceLoader loader = new ResourceLoader();
        if (ascurrent) {
            resourceLoader = loader;
        }
        return loader;
    }

    public static final void resetInstance() {
        resourceLoader = null;
    }

    public static final ResourceLoader currentInstance() {
        return resourceLoader;
    }

    public final static <T extends ResourceObject> T getResourceDefault(String name) {
        return currentInstance().getResource(name);
    }

    public final static <T extends ResourceObject> T getResourceDefault(Class<? extends T> clazz, String name) {
        return currentInstance().getResource(clazz, name);
    }

    //use static create methods
    private ResourceLoader() {
    }

    //private ExecutorService executor = null;
    private final HashMap<String, ResourceObject> loadedData = new HashMap<>();
    private final HashMap<Integer, ArrayList<AdvancedFile>> priorityStagedAdvancedFiles = new HashMap<>();
    private final ArrayList<Loader> loaders = new ArrayList<>();
    private String[] extensions = null;
    private String[] blacklist = null;
    private boolean isLoading = false;
    private ResourceObject tmp;

    @Override
    public boolean load(AdvancedFile advancedFile, AdvancedFile superFile, ResourceLoader resourceLoader) {
        return loadIntern(advancedFile, superFile, resourceLoader);
    }

    public boolean addRessourceObject(String name, ResourceObject resourceObject) {
        if (name != null && !name.isEmpty() && resourceObject != null) {
            loadedData.put(name, resourceObject);
            return true;
        } else {
            return false;
        }
    }

    final boolean loadIntern(AdvancedFile advancedFile, AdvancedFile superFile, ResourceLoader resourceLoader) {
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
                final List<Loader> loadersForExtension = getLoaderForExtensions(advancedFile.getExtension());
                if (loadersForExtension.isEmpty()) {
                    Logger.log(String.format("Failed to load, no Loaders available: \"%s\"%s", advancedFile, (Objects.equals(advancedFile, superFile) ? "" : String.format(" (in \"%s\")", superFile))), LogLevel.WARNING);
                    return false;
                }
                boolean loaded = false;
                for (Loader loader : loadersForExtension) {
                    try {
                        if (loader.load(advancedFile, superFile, resourceLoader)) {
                            loaded = true;
                        }
                    } catch (Exception ex) {
                        if (Logger.isDebugMode()) {
                            Logger.logErr("Error while loading: " + ex, ex);
                        }
                    }
                }
                if (!loaded) {
                    Logger.log(String.format("Failed to load: \"%s\"%s", advancedFile, (Objects.equals(advancedFile, superFile) ? "" : String.format(" (in \"%s\")", superFile))), LogLevel.WARNING);
                }
                return loaded;
            }
        } catch (Exception ex) {
            Logger.logErr("Error while loading staged advanced file: " + ex, ex);
            return false;
        }
    }

    public final void loadStagedAdvancedFiles(boolean clearData/*, long timeout, TimeUnit unit*/) {
        isLoading = true;
        try {
            //resetExecutor();
            if (clearData) {
                loadedData.values().stream().forEach((i) -> i.delete());
                loadedData.clear();
            }
            final ArrayList<AdvancedFile> stagedAdvancedFiles = getStagedAdvancedFilesSorted();
            stagedAdvancedFiles.stream().forEach((advancedFile) -> {
                load(advancedFile, advancedFile, this);
                //executor.submit(() -> {
                //    loadIntern(advancedFile, advancedFile);
                //});
            });
            //executor.shutdown();
            //executor.awaitTermination(timeout, unit);
        } catch (Exception ex) {
            Logger.logErr("Error while loading staged advanced files: " + ex, ex);
        }
        isLoading = false;
    }

    public final boolean isLoading() {
        return isLoading;
    }

    @Override
    public final String[] getExtensions() {
        if (extensions == null) {
            extensions = generateExtensions();
        }
        return extensions;
    }

    @Override
    public final String[] getBlacklist() {
        if (blacklist == null) {
            blacklist = generateBlacklist();
        }
        return blacklist;
    }

    public final Loader addLoader(Loader loader) {
        if (isLoading || loader == null || loader.equals(this) || loader instanceof ResourceLoader) {
            return this;
        }
        resetValues();
        loaders.add(loader);
        return this;
    }

    public final Loader removeLoader(Loader loader) {
        if (isLoading || loader == null || loader.equals(this) || loader instanceof ResourceLoader) {
            return this;
        }
        resetValues();
        loaders.remove(loader);
        return this;
    }

    public final Loader clearLoaders() {
        if (isLoading) {
            return this;
        }
        resetValues();
        loaders.clear();
        return this;
    }

    public final Loader stageAdvancedFiles(AdvancedFile... advancedFiles) {
        return stageAdvancedFiles(0, advancedFiles);
    }

    public final Loader stageAdvancedFiles(int priority, AdvancedFile... advancedFiles) {
        if (isLoading || advancedFiles == null || advancedFiles.length == 0) {
            return this;
        }
        ArrayList<AdvancedFile> stagedAdvancedFiles = priorityStagedAdvancedFiles.get(priority);
        if (stagedAdvancedFiles == null) {
            stagedAdvancedFiles = new ArrayList<>();
            priorityStagedAdvancedFiles.put(priority, stagedAdvancedFiles);
        }
        stagedAdvancedFiles.addAll(Arrays.asList(advancedFiles));
        return this;
    }

    public final Loader clearStagedAdvancedFiles() {
        priorityStagedAdvancedFiles.clear();
        return this;
    }

    public final ArrayList<AdvancedFile> getStagedAdvancedFilesSorted() {
        final ArrayList<AdvancedFile> stagedAdvancedFiles = new ArrayList<>();
        priorityStagedAdvancedFiles.keySet().stream().sorted((i1, i2) -> i2 - i1).forEach((i) -> {
            stagedAdvancedFiles.addAll(priorityStagedAdvancedFiles.get(i));
        });
        return stagedAdvancedFiles;
    }

    public final List<Loader> getLoaderForExtensions(String... extensions) {
        return loaders.stream().filter((loader) -> ArrayUtil.contains(loader.getExtensions(), Filter.createStringFilterEqualsIgnoreCase(), extensions)).collect(Collectors.toList());
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
            tmp = loadedData.get(name);
            return tmp == null ? MISSING_TEXTURE : (SimpleTexture) tmp;
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
        if (Logger.isDebugMode()) {
            Logger.log("FOUND: " + data + " FOR: " + name);
            Logger.log(c + " VS " + (data == null ? "null" : data.getClass()));
        }
        if (data == null && SimpleTexture.class.isAssignableFrom(c)) {
            return (T) MISSING_TEXTURE;
        }
        if (data == null || (!c.isAssignableFrom(data.getClass()) && c != data.getClass())) { //TODO Gucken ob das isAssignableFrom so richtig herum ist
            Logger.log("Wrong Class!");
            return null;
        }
        return (T) data;
    }

    public final <T extends ResourceObject> ArrayList<T> getResources(Class<? extends T> c) {
        return getResources(c, null);
    }

    @SuppressWarnings("unchecked")
    private final <T extends ResourceObject> ArrayList<T> getResources(Class<? extends T> c, ArrayList<T> dataOld) {
        if (dataOld == null) {
            dataOld = new ArrayList<>();
        }
        if (isLoading) {
            return dataOld;
        }
        final ArrayList<T> data = dataOld;
        List<ResourceObject> d = loadedData.values().stream().filter((object) -> (object != null && c.isAssignableFrom(object.getClass()))).collect(Collectors.toList()); //TODO Gucken ob das isAssignableFrom so richtig herum ist
        d.stream().forEach((object) -> {
            data.add((T) object);
        });
        d.clear();
        return data;
    }

    private final void resetValues() {
        extensions = null;
        blacklist = null;
    }

    private final String[] generateExtensions() {
        final ArrayList<String> extensions_temp = new ArrayList<>();
        loaders.stream().forEach((loader) -> {
            String[] ext = loader.getExtensions();
            if (ext != null && ext.length > 0) {
                for (String e : ext) {
                    if (!extensions_temp.contains(e)) {
                        extensions_temp.add(e);
                    }
                }
            }
        });
        return extensions_temp.toArray(new String[extensions_temp.size()]);
    }

    private final String[] generateBlacklist() {
        final ArrayList<String> blacklist_temp = new ArrayList<>();
        loaders.stream().forEach((loader) -> {
            String[] bl = loader.getBlacklist();
            if (bl != null && bl.length > 0) {
                for (String b : bl) {
                    if (!blacklist_temp.contains(b)) {
                        blacklist_temp.add(b);
                    }
                }
            }
        });
        return blacklist_temp.toArray(new String[blacklist_temp.size()]);
    }

    public final <T extends ResourceObject>void actions(Class<T> resClass, Consumer<T> action) {
    	actions((o)->resClass.isInstance(o), action);
    }
    
    @SuppressWarnings("unchecked")
	public final <T extends ResourceObject>void actions(Predicate<T> pre, Consumer<T> action){
    	loadedData.values().stream().filter((Predicate<? super ResourceObject>) pre).forEach((Consumer<? super ResourceObject>) action);
    }
    
//    private final Loader resetExecutor() {
//        if(isLoading) {
//          return this;
//        }
//        if(executor != null) {
//            executor.shutdownNow();
//        }
//        executor = Executors.newFixedThreadPool(10);
//        return this;
//    }
}
