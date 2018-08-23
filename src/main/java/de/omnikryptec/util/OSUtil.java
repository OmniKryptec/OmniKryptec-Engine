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

package de.omnikryptec.util;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.main.OmniKryptecEngine;
import de.omnikryptec.util.logger.LogLevel;
import de.omnikryptec.util.logger.Logger;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Panzer1119
 */
public class OSUtil {
    
    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();
    private static final AdvancedFile USER_HOME = AdvancedFile.folderOfPath(System.getProperty("user.home"));
    private static final String ENGINE_FOLDER_NAME = "." + OmniKryptecEngine.class.getSimpleName() + "_3-1-5";
    private static final String PATHSEPARATOR = "/";
    
    public static final OS OPERATING_SYSTEM = detectOS();
    public static final AdvancedFile STANDARD_APPDATA_FOLDER = getStandardAppDataEngineFolder();
    
    public static enum OS {
        WINDOWS("windows"),
        MAC("macosx"),
        UNIX("linux"),
        SOLARIS("solaris"),
        ERROR(null);
        
        private final String name;
        
        OS(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public String toPathForResource(String nativesPath) {
            return (nativesPath.startsWith(PATHSEPARATOR) ? "" : PATHSEPARATOR) + nativesPath + (nativesPath.endsWith(PATHSEPARATOR) ? "" : PATHSEPARATOR) + name;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    public static final OS getOS() {
        return OPERATING_SYSTEM;
    }
    
    private static final OS detectOS() {
        if (OS_NAME.contains("win")) {
            return OS.WINDOWS;
        } else if (OS_NAME.contains("mac")) {
            return OS.MAC;
        } else if (OS_NAME.contains("nix") || OS_NAME.contains("nux") || OS_NAME.contains("aix")) {
            return OS.UNIX;
        } else if (OS_NAME.contains("sunos")) {
            return OS.SOLARIS;
        } else {
            return OS.ERROR;
        }
    }
    
    public static final boolean createStandardFolders() {
        try {
            return (STANDARD_APPDATA_FOLDER.createAdvancedFile() && STANDARD_APPDATA_FOLDER.isDirectory());
        } catch (Exception ex) {
            Logger.logErr("Error while creating standard folders: " + ex, ex);
            return false;
        }
    }
    
    public static final AdvancedFile getStandardAppDataEngineFolder() {
        return getAppDataFolder(ENGINE_FOLDER_NAME);
    }
    
    public static final AdvancedFile getAppDataFolder(String folderName) {
        AdvancedFile file = null;
        switch (OPERATING_SYSTEM) {
            case WINDOWS:
                file = new AdvancedFile(false, USER_HOME, "AppData", "Roaming", folderName);
                break;
            case MAC:
                file = new AdvancedFile(false, USER_HOME, "Library", "Application Support", folderName); // TODO Needs confirmation!
                break;
            case UNIX:
                file = new AdvancedFile(false, USER_HOME, folderName);
                break;
            case SOLARIS:
                file = new AdvancedFile(false, USER_HOME, folderName);
                break;
            case ERROR:
                break;
            default:
                break;
        }
        if (file != null) {
            file.setShouldBeFile(false);
        }
        return file;
    }
    
    public static final boolean extractFileFromJar(AdvancedFile file, String path) {
        if (file.exists()) {
            return true;
        }
        try {
            final InputStream inputStream = OSUtil.class.getResourceAsStream(path);
            Files.copy(inputStream, file.toFile().getAbsoluteFile().toPath());
            inputStream.close();
            return file.exists();
        } catch (Exception ex) {
            Logger.logErr("Error while extracting file from jar: " + ex, ex);
            return false;
        }
    }
    
    public static final boolean extractFolderFromJar(AdvancedFile folder, String path) {
        try {
            boolean allGood = true;
            final AdvancedFile jarFile = getJarFile();
            if (jarFile.isFile()) {
                final JarFile jar = new JarFile(jarFile.toFile());
                final Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    final JarEntry jarEntry = entries.nextElement();
                    final String name = jarEntry.getName();
                    if (name.startsWith(path)) {
                        boolean good = extractFileFromJar(getFileOfPath(folder, name), name);
                        if (!good) {
                            allGood = false;
                        }
                    }
                }
                jar.close();
            } else {
                final URL url = OSUtil.class.getResource(path);
                if (url != null) {
                    final AdvancedFile apps = AdvancedFile.folderOfPath(url.toURI().getPath());
                    for (AdvancedFile app : apps.listAdvancedFiles()) {
                        try {
                            Files.copy(app.toFile().toPath(), new AdvancedFile(false, folder, app.getName()).toFile().toPath(), StandardCopyOption.COPY_ATTRIBUTES);
                        } catch (java.nio.file.FileAlreadyExistsException faex) {
                        } catch (Exception ex) {
                            allGood = false;
                            Logger.log("Error while extracting file from folder from jar: " + ex, LogLevel.WARNING);
                        }
                    }
                } else {
                    allGood = false;
                }
            }
            return allGood;
        } catch (Exception ex) {
            Logger.logErr("Error while extracting folder from jar: " + ex, ex);
            return false;
        }
    }
    
    public static final AdvancedFile getFileOfPath(AdvancedFile folder, String path) {
        String name = path;
        if (path.contains(PATHSEPARATOR)) {
            name = name.substring(name.lastIndexOf(PATHSEPARATOR) + PATHSEPARATOR.length());
        }
        return new AdvancedFile(false, folder, name);
    }
    
    public static final AdvancedFile getJarFile() {
        return AdvancedFile.fileOfPath(OSUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    }
    
    public static final boolean isJarFile() {
        return getJarFile().isFile();
    }
    
    public static final boolean isIDE() {
        return !isJarFile();
    }
    
}