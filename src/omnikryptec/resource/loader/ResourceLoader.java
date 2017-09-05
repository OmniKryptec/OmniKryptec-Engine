package omnikryptec.resource.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Generated;

import omnikryptec.resource.texture.SimpleTexture;
import omnikryptec.resource.texture.Texture;
import omnikryptec.util.AdvancedFile;
import omnikryptec.util.ArrayUtil;
import omnikryptec.util.ArrayUtil.Filter;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

/**
 * ResourceLoader
 *
 * @author Panzer1119 &amp; pcfreak9000
 */
public class ResourceLoader implements Loader {
	
	public static final SimpleTexture MISSING_TEXTURE = SimpleTexture.newTexture("/omnikryptec/resource/loader/missing_texture.png");
	
	
    public static final ResourceLoader resourceLoader = new ResourceLoader();

    public static final ResourceLoader getInstance() {
        return resourceLoader;
    }
	
    //less code
    public final static <T extends ResourceObject> T getRes(String name) {
    	return getInstance().getResource(name);
    }
    
    //less code
    public final static <T extends ResourceObject> T getRes(Class<? extends T> clazz, String name) {
    	return getInstance().getResource(clazz, name);
    }
    
//    private ExecutorService executor = null;
    private final HashMap<String, ResourceObject> loadedData = new HashMap<>();
    private final HashMap<Integer, ArrayList<AdvancedFile>> priorityStagedAdvancedFiles = new HashMap<>();
    private final ArrayList<Loader> loaders = new ArrayList<>();
    private String[] extensions = null;
    private String[] blacklist = null;
    private boolean isLoading = false;

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
                    Logger.log(String.format("Failed to load, no Loaders available: \"%s\"%s", advancedFile, (AdvancedFile.isEqual(advancedFile, superFile) ? "" : String.format(" (in \"%s\")", superFile))), LogLevel.WARNING);
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
                    Logger.log(String.format("Failed to load: \"%s\"%s", advancedFile, (AdvancedFile.isEqual(advancedFile, superFile) ? "" : String.format(" (in \"%s\")", superFile))), LogLevel.WARNING);
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
//         	resetExecutor();
            if (clearData) {
                loadedData.clear();
            }
            final ArrayList<AdvancedFile> stagedAdvancedFiles = getStagedAdvancedFilesSorted();
            stagedAdvancedFiles.stream().forEach((advancedFile) -> {
                load(advancedFile, advancedFile, this);
//                executor.submit(() -> {
//                    loadIntern(advancedFile, advancedFile);
//                });
            });
//          executor.shutdown();
//          executor.awaitTermination(timeout, unit);
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
    
    @SuppressWarnings("unchecked")
    public final <T extends ResourceObject> T getResource(Class<? extends T> c, String name) {
        if (c == null || name == null || name.isEmpty()) {
            return null;
        }
        ResourceObject data = loadedData.get(name);
        if(Logger.isDebugMode()){
	        Logger.log("FOUND: " + data + " FOR: " + name);
	        Logger.log(c + " VS " + (data == null ? "null" : data.getClass()));
        }
        if(data == null && Texture.class.isAssignableFrom(c)){
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
