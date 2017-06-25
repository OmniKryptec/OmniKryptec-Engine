package omnikryptec.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.util.AdvancedFile;
import omnikryptec.util.ArrayUtil;

/**
 * ResourceLoader
 * @author Panzer1119 &amp; pcfreak9000
 */
public class ResourceLoader implements Loader {
    
//    private ExecutorService executor = null;
    private final HashMap<String, RessourceObject> loadedData = new HashMap<>();
    private final HashMap<Integer, ArrayList<AdvancedFile>> priorityStagedAdvancedFiles = new HashMap<>();
    private final ArrayList<Loader> loaders = new ArrayList<>();
    private String[] extensions = null;
    private String[] blacklist = null;
    
    @Override
    public RessourceObject load(AdvancedFile advancedFile){
    	return loadp(advancedFile, advancedFile);
    }
    
    public void addRessourceObject(String name, RessourceObject obj){
    	loadedData.put(name, obj);
    }
    
    final RessourceObject loadp(AdvancedFile advancedFile, AdvancedFile superfile) {
        try {
            if(advancedFile == null || !advancedFile.exists()) {
                return null;
            }
            if(advancedFile.isDirectory()) {
                advancedFile.listAdvancedFiles().stream().forEach((af) -> {
                    loadp(af, superfile);
                });
                return null;
            } else {
                final List<Loader> loadersForExtension = getLoaderForExtensions(advancedFile.getExtension());
                RessourceObject dataObj = null;
                for(Loader loader : loadersForExtension) {
                	dataObj = loader.load(advancedFile);
                    if(dataObj != null) {
                        break;
                    }
                }
                final RessourceData data = new RessourceData(generateName(advancedFile, superfile), dataObj);
                if(data != null && data.getName() != null && !data.getName().isEmpty() && data.getData() != null) {
                	addRessourceObject(data.getName(), data.getData());
                } else {
                    Logger.log("Failed to load: " + advancedFile, LogLevel.WARNING);
                }
                return data.getData();
            }
        } catch (Exception ex) {
            Logger.logErr("Error while loading staged advanced file: " + ex, ex);
            return null;
        }
    }
    
    private String generateName(AdvancedFile advancedFile, AdvancedFile superfile) {
		String s = advancedFile.getPath().replace(superfile.getPath(), "").replace(AdvancedFile.PATH_SEPARATOR, ":");
		if(s.startsWith(":")){
			s = s.substring(1, s.length());
		}
		if(s.endsWith(":")){
			s = s.substring(0, s.length()-1);
		}
		return s;
	}

	public final void loadStagedAdvancedFiles(boolean clearData/*, long timeout, TimeUnit unit*/) {
        try {
//         	resetExecutor();
            if(clearData){
            	loadedData.clear();
            }
            final ArrayList<AdvancedFile> stagedAdvancedFiles = getStagedAdvancedFilesSorted();
            stagedAdvancedFiles.stream().forEach((advancedFile) -> {
            	load(advancedFile);
//                executor.submit(() -> {
//                    load(advancedFile);
//                });
            });
//          executor.shutdown();
//          executor.awaitTermination(timeout, unit);
        } catch (Exception ex) {
            Logger.logErr("Error while loading staged advanced files: " + ex, ex);
        }
    }

    @Override
    public final String[] getExtensions() {
        if(extensions == null) {
            extensions = generateExtensions();
        }
        return extensions;
    }

    @Override
    public final String[] getBlacklist() {
        if(blacklist == null) {
            blacklist = generateBlacklist();
        }
        return blacklist;
    }
    
    public final Loader addLoader(Loader loader) {
        if(loader == null || loader.equals(this) || loader instanceof ResourceLoader) {
            return this;
        }
        resetValues();
        loaders.add(loader);
        return this;
    }
    
    public final Loader removeLoader(Loader loader) {
        if(loader == null || loader.equals(this) || loader instanceof ResourceLoader) {
            return this;
        }
        resetValues();
        loaders.remove(loader);
        return this;
    }
    
    public final Loader clearLoaders() {
        resetValues();
        loaders.clear();
        return this;
    }
    
    public final Loader stageAdvancedFiles(AdvancedFile... advancedFiles) {
        return stageAdvancedFiles(0, advancedFiles);
    }
    
    public final Loader stageAdvancedFiles(int priority, AdvancedFile... advancedFiles) {
        if(advancedFiles == null || advancedFiles.length == 0) {
            return this;
        }
        ArrayList<AdvancedFile> stagedAdvancedFiles = priorityStagedAdvancedFiles.get(priority);
        if(stagedAdvancedFiles == null) {
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
        return loaders.stream().filter((loader) -> ArrayUtil.contains(loader.getExtensions(), extensions)).collect(Collectors.toList());
    }
    
    @SuppressWarnings("unchecked")
	public final <T> T getData(Class<? extends T> c, String name) {
    	if(!RessourceObject.class.isAssignableFrom(c)){
    		return null;
    	}
    	if(c == null || name == null || name.isEmpty()) {
            return null;
        }
        RessourceObject data = loadedData.get(name);
        if(data == null || !c.isAssignableFrom(data.getClass())) { //TODO Gucken ob das isAssignableFrom so richtig herum ist
            return null;
        }
        return (T) data;
    }
    
    @SuppressWarnings("unchecked")
	public final <T> ArrayList<T> getAllData(Class<? extends T> c) {
    	if(RessourceObject.class.isAssignableFrom(c)){
    		return new ArrayList<T>();
    	}
    	final ArrayList<T> data = new ArrayList<>();
        List<RessourceObject> d = loadedData.values().stream().filter((object) -> (object != null && c.isAssignableFrom(object.getClass()))).collect(Collectors.toList()); //TODO Gucken ob das isAssignableFrom so richtig herum ist
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
            if(ext != null && ext.length > 0) {
                for(String e : ext) {
                    if(!extensions_temp.contains(e)) {
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
            if(bl != null && bl.length > 0) {
                for(String b : bl) {
                    if(!blacklist_temp.contains(b)) {
                        blacklist_temp.add(b);
                    }
                }
            }
        });
        return blacklist_temp.toArray(new String[blacklist_temp.size()]);
    }
    
//    private final Loader resetExecutor() {
//        if(executor != null) {
//            executor.shutdownNow();
//        }
//        executor = Executors.newFixedThreadPool(10);
//        return this;
//    }
    
    
}
