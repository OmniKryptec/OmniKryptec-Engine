package omnikryptec.util;

import java.util.function.Consumer;
import omnikryptec.util.OSUtil.OS;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

/**
 *
 * @author Panzer1119
 */
public class NativesLoader {

    private static final String NATIVESPATH = "/omnikryptec/res/natives/";
    private static final String LWJGLLIBRARYPATH = "org.lwjgl.librarypath";
    private static AdvancedFile NATIVESFOLDER = null;
    private static String OLDLWJGLLIBRARYPATH = "";

    private static boolean nativesLoaded = false;

    public static final boolean setNativesFolder(AdvancedFile file) {
        if (nativesLoaded) {
            return false;
        }
        NATIVESFOLDER = file;
        return true;
    }

    public static final boolean loadNatives(AdvancedFile ... totry) {
    	return loadNatives(true, totry);
    }
    
    public static final boolean loadNatives(boolean log, AdvancedFile ... totry) {
    	for(AdvancedFile f : totry) {
    		setNativesFolder(f);
    		if(loadNatives()) {
    			if(log) {
    				Logger.log("Loading natives to \""+f+"\" was successful", LogLevel.FINE);
    			}
    			return true;
    		}else {
    			if(log) {
    				Logger.log("Loading natives to \""+f+"\" failed", LogLevel.WARNING);
    			}
    		}
    	}
    	return false;
    }
    
    public static final boolean loadNatives() {
        return loadNatives((Consumer<AdvancedFile>)null);
    }

    public static final boolean loadNatives(Consumer<AdvancedFile> success) {
        return loadNatives(success, null);
    }

    public static final boolean loadNatives(Consumer<AdvancedFile> success, Consumer<Throwable> failure) {
        if (NATIVESFOLDER == null) {
            NATIVESFOLDER = getStandardNativesFolder();
        }
        if (NATIVESFOLDER == null) {
            Logger.log("Loaded natives not successfully, because there is some error with the standard natives folder", LogLevel.WARNING);
            Util.finish(false, success, failure, NATIVESFOLDER, null);
            return false;
        }
        NATIVESFOLDER.createAdvancedFile();
        if (NATIVESFOLDER.exists() && NATIVESFOLDER.isDirectory()) {
            boolean good = extractNatives(NATIVESFOLDER);
            if (good) {
                Logger.log("Loaded natives successfully", LogLevel.FINE);
                registerNatives();
                nativesLoaded = true;
                Util.finish(true, success, failure, NATIVESFOLDER, null);
                return true;
            } else {
                Logger.log("Loaded natives not successfully, because something with loading the natives went wrong", LogLevel.WARNING);
                Util.finish(false, success, failure, NATIVESFOLDER, null);
                return false;
            }
        } else {
            Logger.log("Loaded natives not successfully, because the natives folder is a file", LogLevel.WARNING);
            Util.finish(false, success, failure, NATIVESFOLDER, null);
            return false;
        }
    }

    public static final boolean unloadNatives() {
        if (!nativesLoaded) {
            return false;
        }
        try {
            if (!NATIVESFOLDER.isDirectory()) {
                Logger.log(
                        "Unloaded natives not successfully, because there is some error with the standard natives folder",
                        LogLevel.WARNING);
                return false;
            }
            unregisterNatives();
            boolean allGood = true;
            for (AdvancedFile av : NATIVESFOLDER.listAdvancedFiles()) {
                if (!av.toFile().delete()) {
                    av.toFile().deleteOnExit();
                    allGood = false;
                }
            }
            if (allGood) {
                nativesLoaded = false;
                return true;
            } else {
                Logger.log("Unloaded natives not successfully, because some files could not be deleted",
                        LogLevel.WARNING);
                return false;
            }
        } catch (Exception ex) {
            Logger.logErr("Error while unloading natives: " + ex, ex);
            return false;
        }
    }

    public static final boolean isNativesLoaded() {
        return nativesLoaded;
    }

    private static final boolean registerNatives() {
        try {
            OLDLWJGLLIBRARYPATH = System.getProperty(LWJGLLIBRARYPATH, "");
            System.setProperty(LWJGLLIBRARYPATH, NATIVESFOLDER.toFile().getAbsolutePath());
            Logger.log("Registered natives successfully", LogLevel.FINER);
            return true;
        } catch (Exception ex) {
            Logger.logErr("Error while registering natives: " + ex, ex);
            return false;
        }
    }

    private static final boolean unregisterNatives() {
        try {
            System.setProperty(LWJGLLIBRARYPATH, OLDLWJGLLIBRARYPATH);
            // GLContext.unloadOpenGLLibrary();
            System.gc();
            Logger.log("Unregistered natives successfully", LogLevel.FINER);
            return true;
        } catch (Exception ex) {
            Logger.logErr("Error while unregistering natives: " + ex, ex);
            return false;
        }
    }

    private static final boolean extractNatives(AdvancedFile nativesFolder) {
        try {
            if (OSUtil.OpSys == null || OSUtil.OpSys == OS.ERROR) {
                return false;
            }
            return extractNativesFromPath(nativesFolder, OSUtil.OpSys.toPathForResource(NATIVESPATH));
        } catch (Exception ex) {
            Logger.logErr("Error while extracting natives: " + ex, ex);
            return false;
        }
    }

    private static final boolean extractNativesFromPath(AdvancedFile nativesFolder, String path) {
        try {
            return OSUtil.extractFolderFromJar(nativesFolder, path);
        } catch (Exception ex) {
            Logger.logErr("Error while extracting natives from path: " + ex, ex);
            return false;
        }
    }

    private static final AdvancedFile getStandardNativesFolder() {
        boolean appDataCreated = OSUtil.createStandardFolders();
        if (appDataCreated) {
            return new AdvancedFile(false, OSUtil.STANDARDAPPDATA, "natives", OSUtil.OpSys.getName());
        } else {
            return null;
        }
    }

}
