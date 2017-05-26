package omnikryptec.util;

import java.io.File;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.util.OSUtil.OS;

/**
 *
 * @author Panzer1119
 */
public class NativesLoader {
    
    private static final String NATIVESPATH = "/omnikryptec/natives/";
    private static final String LWJGLLIBRARYPATH = "org.lwjgl.librarypath";
    private static final File NATIVESFOLDER = getStandardNativesFolder();
    
    private static boolean nativesLoaded = false;
    
    public static final boolean loadNatives() {
        if(NATIVESFOLDER == null) {
            Logger.log("Loaded natives not successfully, because there is some error with the standard natives folder", LogLevel.WARNING);
            return false;
        }
        NATIVESFOLDER.mkdirs();
        if(NATIVESFOLDER.exists() && NATIVESFOLDER.isDirectory()) {
            boolean good = extractNatives(NATIVESFOLDER);
            if(good) {
                Logger.log("Loaded natives successfully", LogLevel.FINE);
                registerNatives();
                nativesLoaded = true;
                return true;
            } else {
                Logger.log("Loaded natives not successfully, because something with loading the natives went wrong", LogLevel.WARNING);
                return false;
            }
        } else {
            Logger.log("Loaded natives not successfully, because the natives folder is a file", LogLevel.WARNING);
            return false;
        }
    }
    
    public static final boolean unloadNatives() {
        if(!nativesLoaded) {
            return false;
        }
        try {
            if(!NATIVESFOLDER.isDirectory()) {
                Logger.log("Unloaded natives not successfully, because there is some error with the standard natives folder", LogLevel.WARNING);
                return false;
            }
            boolean allGood = true;
            for(File file : NATIVESFOLDER.listFiles()) {
                if(!file.delete()) {
                    file.deleteOnExit();
                    allGood = false;
                }
            }
            if(allGood) {
                nativesLoaded = false;
                return true;
            } else {
                Logger.log("Unloaded natives not successfully, because some files could not be deleted", LogLevel.WARNING);
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
    
    private static final void registerNatives() {
        System.setProperty(LWJGLLIBRARYPATH, NATIVESFOLDER.getAbsolutePath());
        Logger.log("Registered natives successfully", LogLevel.FINER);
    }
    
    private static final boolean extractNatives(File nativesFolder) {
        try {
            if(OSUtil.OpSys == null || OSUtil.OpSys == OS.ERROR) {
                return false;
            }
            return extractNativesFromPath(nativesFolder, OSUtil.OpSys.toPathForResource(NATIVESPATH));
        } catch (Exception ex) {
            Logger.logErr("Error while extracting natives: " + ex, ex);
            return false;
        }
    }
    
    private static final boolean extractNativesFromPath(File nativesFolder, String path) {
        try {
            return OSUtil.extractFolderFromJar(nativesFolder, path);
        } catch (Exception ex) {
            Logger.logErr("Error while extracting natives from path: " + ex, ex);
            return false;
        }
    }
    
    private static final File getStandardNativesFolder() {
        boolean appDataCreated = OSUtil.createStandardFolders();
        if(appDataCreated) {
            return new File(OSUtil.STANDARDAPPDATA.getAbsolutePath() + File.separator + "natives" + File.separator + OSUtil.OpSys.getName());
        } else {
            return null;
        }
    }
    
}
