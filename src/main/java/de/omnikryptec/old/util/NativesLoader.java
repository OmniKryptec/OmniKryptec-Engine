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

package de.omnikryptec.old.util;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.old.util.OSUtil.OS;
import de.omnikryptec.old.util.logger.LogLevel;
import de.omnikryptec.old.util.logger.Logger;

import java.util.function.Consumer;

/**
 * NativesLoader
 *
 * @author Panzer1119
 */
public class NativesLoader {

    private static final String NATIVESPATH = "/de/omnikryptec/res/natives/";
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

    /**
     * Use null as argument for the default natives folder
     *
     * @param folders Folders to load natives to
     *
     * @return <tt>true</tt> if it worked
     */
    public static final boolean loadNatives(AdvancedFile... folders) {
	return loadNatives(true, folders);
    }

    /**
     * Use null as argument for the default natives folder
     *
     * @param log     If this should get logged
     * @param folders Folders to load natives to
     *
     * @return <tt>true</tt> if it worked
     */
    public static final boolean loadNatives(boolean log, AdvancedFile... folders) {
	for (AdvancedFile folder : folders) {
	    setNativesFolder(folder);
	    if (loadNatives()) {
		if (log) {
		    Logger.log("Loading natives to \"" + NATIVESFOLDER + "\" was successful", LogLevel.FINE);
		}
		return true;
	    } else if (log) {
		Logger.log("Loading natives to \"" + folder + "\" failed", LogLevel.WARNING);
	    }
	}
	return false;
    }

    /**
     * @return <tt>true</tt> if it worked
     */
    public static final boolean loadNatives() {
	return loadNatives((Consumer<AdvancedFile>) null);
    }

    /**
     * @param success
     *
     * @return <tt>true</tt> if it worked
     */
    public static final boolean loadNatives(Consumer<AdvancedFile> success) {
	return loadNatives(success, null);
    }

    /**
     * @param success
     * @param failure
     *
     * @return <tt>true</tt> if it worked
     */
    public static final boolean loadNatives(Consumer<AdvancedFile> success, Consumer<Throwable> failure) {
	if (NATIVESFOLDER == null) {
	    NATIVESFOLDER = getStandardNativesFolder();
	}
	if (NATIVESFOLDER == null) {
	    Logger.log("Loaded natives not successfully, because there is some error with the standard natives folder",
		    LogLevel.WARNING);
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
		Logger.log("Loaded natives not successfully, because something with loading the natives went wrong",
			LogLevel.WARNING);
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
	    if (OSUtil.OPERATING_SYSTEM == null || OSUtil.OPERATING_SYSTEM == OS.ERROR) {
		return false;
	    }
	    return extractNativesFromPath(nativesFolder, OSUtil.OPERATING_SYSTEM.toPathForResource(NATIVESPATH));
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
	    return new AdvancedFile(false, OSUtil.STANDARD_APPDATA_FOLDER, "natives",
		    OSUtil.OPERATING_SYSTEM.getName());
	} else {
	    return null;
	}
    }

}
