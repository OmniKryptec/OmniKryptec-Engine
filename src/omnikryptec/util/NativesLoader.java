package omnikryptec.util;

import java.io.File;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;

/**
 *
 * @author Panzer1119
 */
public class NativesLoader {
    
    private static final String NATIVESPATH = "/omnikryptec/natives/";
    private static final String LWJGLLIBRARYPATH = "org.lwjgl.librarypath";
    private static File nativesFolder = null;
    
    public static final boolean loadNatives() {
        final File nativesFolder = getStandardNativesFolder();
        if(nativesFolder == null) {
            Logger.log("Loaded natives not successfully, because there is some error with the standard natives folder", LogLevel.WARNING);
            return false;
        }
        nativesFolder.mkdirs();
        if(nativesFolder.exists() && nativesFolder.isDirectory()) {
            boolean good = extractNatives(nativesFolder);
            if(good) {
                NativesLoader.nativesFolder = nativesFolder;
                Logger.log("Loaded natives successfully", LogLevel.FINE);
                registerNatives();
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
    
    private static final void registerNatives() {
        System.setProperty(LWJGLLIBRARYPATH, nativesFolder.getAbsolutePath());
        Logger.log("Registered natives successfully", LogLevel.FINER);
    }
    
    private static final boolean extractNatives(File nativesFolder) {
        try {
            switch(OSUtil.OS) {
                case WINDOWS:
                    return extractNativesFromPath(nativesFolder, NATIVESPATH + OSUtil.OS.WINDOWS.getName());
                case MAC:
                    return extractNativesFromPath(nativesFolder, NATIVESPATH + OSUtil.OS.MAC.getName());
                case UNIX:
                    return extractNativesFromPath(nativesFolder, NATIVESPATH + OSUtil.OS.UNIX.getName());
                case SOLARIS:
                    return extractNativesFromPath(nativesFolder, NATIVESPATH + OSUtil.OS.SOLARIS.getName());
                case ERROR:
                    return false;
                default:
                    return false;
            }
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
            return new File(OSUtil.STANDARDAPPDATA.getAbsolutePath() + File.separator + "natives");
        } else {
            return null;
        }
    }
    
}
