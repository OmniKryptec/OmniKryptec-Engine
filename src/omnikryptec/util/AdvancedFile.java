package omnikryptec.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import omnikryptec.logger.Logger;

/**
 *
 * @author Panzer1119
 */
public class AdvancedFile {

    public static final String PATH_SEPARATOR = "/";
    public static final String FILE_SEPARATOR = File.separator;
    public static final String EXTENSION_SEPARATOR = "\\.";

    private File folder = null;
    private String[] paths = null;
    private String path = null;
    private String separator = PATH_SEPARATOR;
    private boolean shouldBeFile = true;
    
    /**
     * Creates an AdvancedFile which is relative
     *
     * @param paths String Array Paths
     */
    public AdvancedFile(String... paths) {
        this(true, paths);
        parseShouldBeFile();
    }
    
    /**
     * Creates an AdvancedFile which is relative
     *
     * @param shouldBeFile Boolean if this AdvancedFile should be a file or a directory
     * @param paths String Array Paths
     */
    public AdvancedFile(boolean shouldBeFile, String... paths) {
        this(shouldBeFile, null, paths);
    }

    /**
     * Creates an AdvancedFile which can be relative or absolute
     *
     * @param parent Object If File than it can be the Folder, where the Paths
     * is in (If null then this AdvancedFile is a relative path) or if it is an
     * AndvancedFile than this AdvancedFile is a child of the parent
     * AdvancedFile
     * @param paths String Array Paths
     */
    public AdvancedFile(Object parent, String... paths) {
        this(true, parent, paths);
        parseShouldBeFile();
    }
    
    /**
     * Creates an AdvancedFile which can be relative or absolute
     *
     * @param shouldBeFile Boolean if this AdvancedFile should be a file or a directory
     * @param parent Object If File than it can be the Folder, where the Paths
     * is in (If null then this AdvancedFile is a relative path) or if it is an
     * AndvancedFile than this AdvancedFile is a child of the parent
     * AdvancedFile
     * @param paths String Array Paths
     */
    public AdvancedFile(boolean shouldBeFile, Object parent, String... paths) {
        setParent(parent);
        addPaths(paths);
        this.shouldBeFile = shouldBeFile;
    }

    /**
     * Returns a copy of this AdvancedFile
     *
     * @return AdvancedFile AdvancedFile
     */
    public final AdvancedFile copy() {
        return new AdvancedFile(shouldBeFile, folder, paths);
    }

    /**
     * Adds Paths
     *
     * @param paths String Array Paths
     * @return A reference to this AdvancedFile
     */
    public final AdvancedFile addPaths(String... paths) {
        this.path = null;
        if (paths == null) {
            return this;
        }
        if (this.paths != null) {
            final String[] paths_new = Arrays.copyOf(this.paths, this.paths.length + paths.length);
            for (int i = this.paths.length; i < paths_new.length; i++) {
                paths_new[i] = paths[i - this.paths.length];
            }
            this.paths = paths_new;
        } else {
            this.paths = paths;
        }
        return this;
    }

    /**
     * Returns a copy of this AdvancedFile with the given Paths added
     *
     * @param paths String Array Paths
     * @return A copy of this AdvancedFile
     */
    public final AdvancedFile withPaths(String... paths) {
        return copy().addPaths(paths);
    }

    /**
     * Adds Paths before the other Paths
     *
     * @param paths String Array Paths
     * @return A reference to this AdvancedFile
     */
    public final AdvancedFile addPrePaths(String... paths) {
        this.path = null;
        if (paths == null) {
            return this;
        }
        if (this.paths != null) {
            final String[] paths_new = Arrays.copyOf(paths, paths.length + this.paths.length);
            for (int i = paths.length; i < paths_new.length; i++) {
                paths_new[i] = this.paths[i - paths.length];
            }
            this.paths = paths_new;
        } else {
            this.paths = paths;
        }
        return this;
    }

    /**
     * Returns a copy of this AdvancedFile with the given Paths added as prefix
     *
     * @param paths String Array Paths
     * @return A copy of this AdvancedFile
     */
    public final AdvancedFile withPrePaths(String... paths) {
        return copy().addPrePaths(paths);
    }

    /**
     * Sets the Paths
     *
     * @param paths String Array Paths
     * @return A reference to this AdvancedFile
     */
    public final AdvancedFile setPaths(String... paths) {
        this.path = null;
        this.paths = paths;
        return this;
    }

    /**
     * Returns the Paths
     *
     * @return Stirng Array Paths
     */
    public final String[] getPaths() {
        this.path = null;
        return paths;
    }

    /**
     * Returns the Path
     *
     * @return String Path
     */
    public final String getPath() {
        if (path == null) {
            path = createPath();
        }
        return path;
    }

    private final String createPath() {
        String path_new = (isRelative() ? "" : folder.getAbsolutePath());
        if (paths != null) {
            for (String path_temp : paths) {
                path_new += separator + path_temp;
            }
        }
        return path_new;
    }

    /**
     * Returns if this AdvancedFile is a relative path
     *
     * @return <tt>true</tt> if this AdvancedFile is a relative path
     */
    public final boolean isRelative() {
        return folder == null;
    }

    /**
     * Sets the folder
     *
     * @param folder File Folder (If null then this AdvancedFile is a relative
     * path)
     * @return A reference to this AdvancedFile
     */
    public final AdvancedFile setFolder(File folder) {
        this.path = null;
        this.folder = folder;
        separator = (folder == null ? PATH_SEPARATOR : FILE_SEPARATOR);
        return this;
    }

    /**
     * Returns the folder if this AdvancedFile is an absolute path
     *
     * @return File Folder
     */
    public final File getFolder() {
        return folder;
    }

    /**
     * Returns a File
     *
     * @return File File
     */
    public final File toFile() {
        return new File(getPath());
    }

    /**
     * Returns the parent AdvancedFile or null
     *
     * @return AdvancedFile Parent AdvancedFile
     */
    public final AdvancedFile getParent() {
        if (paths != null && paths.length > 1) {
            return new AdvancedFile(false, folder, ArrayUtil.copyOf(paths, paths.length - 1));
        } else if (paths != null && paths.length == 1 && !isRelative()) {
            return new AdvancedFile(false, folder);
        } else if (!isRelative()) {
            return new AdvancedFile(false, folder.getParentFile());
        } else {
            return null;
        }
    }

    /**
     * Sets the parent
     *
     * @param parent Object If File than it can be the Folder, where the Paths
     * is in (If null then this AdvancedFile is a relative path) or if it is an
     * AndvancedFile than this AdvancedFile is a child of the parent
     * AdvancedFile
     * @return A reference to this AdvancedFile
     */
    public final AdvancedFile setParent(Object parent) {
        return setParent(parent, true, true);
    }

    private final AdvancedFile setParent(Object parent, boolean withFolder, boolean withPaths) {
        if (withFolder && parent == null) {
            setFolder(null);
        } else if (parent instanceof File) {
            final File file = (File) parent;
            if (withFolder && file.isAbsolute()) {
                setFolder(file);
            }
            if (withPaths && !file.isAbsolute()) {
                addPrePaths(file.getPath().split("\\" + FILE_SEPARATOR));
            }
        } else if (parent instanceof AdvancedFile) {
            final AdvancedFile advancedFile = (AdvancedFile) parent;
            if (withFolder) {
                setFolder(advancedFile.getFolder());
            }
            if (withPaths) {
                addPrePaths(advancedFile.getPaths());
            }
        }
        return this;
    }

    /**
     * Returns a copy of this AdvancedFile with the given parent
     *
     * @param parent Object If File than it can be the Folder, where the Paths
     * is in (If null then this AdvancedFile is a relative path) or if it is an
     * AndvancedFile than this AdvancedFile is a child of the parent
     * AdvancedFile
     * @return A copy of this AdvancedFile
     */
    public final AdvancedFile withParent(Object parent) {
        return copy().setParent(parent);
    }

    /**
     * Sets the parents
     *
     * @param parents Object Array If File than it can be the Folder, where the
     * Paths is in (If null then this AdvancedFile is a relative path) or if it
     * is an AndvancedFile than this AdvancedFile is a child of the parent
     * AdvancedFile
     * @return A reference to this AdvancedFile
     */
    public final AdvancedFile setParents(Object... parents) {
        if (parents == null || parents.length == 0) {
            return this;
        }
        setParent(parents[0], true, false);
        for (int i = 0; i < parents.length; i++) {
            setParent(parents[parents.length - i - 1], false, true);
        }
        return this;
    }

    /**
     * Returns a copy of this AdvancedFile with the given parents
     *
     * @param parents Object Array If File than it can be the Folder, where the
     * Paths is in (If null then this AdvancedFile is a relative path) or if it
     * is an AndvancedFile than this AdvancedFile is a child of the parent
     * AdvancedFile
     * @return A copy of this AdvancedFile
     */
    public final AdvancedFile withParents(Object... parents) {
        return copy().setParents(parents);
    }

    /**
     * Creates the file or folder
     *
     * @return <tt>true</tt> if the file was successfully created or already
     * exists
     */
    public final boolean createFile() {
        if (isRelative()) {
            return false;
        }
        try {
            final File file = toFile();
            if (file.exists()) {
                return file.isFile() == shouldBeFile;
            }
            file.getParentFile().mkdirs();
            if (file.getParentFile().exists()) {
                if(shouldBeFile) {
                    file.createNewFile();
                }
            }
            if(!shouldBeFile) {
                file.mkdirs();
            }
            return exists();
        } catch (Exception ex) {
            Logger.logErr("Error while creating file: " + ex, ex);
            return false;
        }
    }
    
    /**
     * Returns if this AdvancedFile exists and is a file
     * @return <tt>true</tt> if this AdvancedFile exists and is a file
     */
    public final boolean isFile() {
        if(isRelative()) {
            return false; // FIXME Hier kann man noch gucken, ob das File auch in der Jar existiert
        } else {
            final File file = toFile();
            if(!file.exists()) {
                return false;
            }
            return file.isFile();
        }
    }
    
    /**
     * Returns if this AdvancedFile exists and is a directory
     * @return <tt>true</tt> if this AdvancedFile exists and is a file
     */
    public final boolean isDirectory() {
        if(isRelative()) {
            return false; // FIXME Hier kann man noch gucken, ob das File auch in der Jar existiert
        } else {
            final File file = toFile();
            if(!file.exists()) {
                return false;
            }
            return file.isDirectory();
        }
    }

    /**
     * Returns if this AdvancedFile exists
     * @return <tt>true</tt> if this AdvancedFile exists
     */
    public final boolean exists() {
        if (isRelative()) {
            return false; // FIXME Hier kann man noch gucken, ob das File auch in der Jar existiert
        }
        try {
            final File file = toFile();
            return file.exists();
        } catch (Exception ex) {
            Logger.logErr("Error while checking file existance: " + ex, ex);
            return false;
        }
    }

    /**
     * Creates an InputStream
     *
     * @return InputStream InputStream
     */
    public final InputStream createInputStream() {
        if (isRelative()) {
            return AdvancedFile.class.getResourceAsStream(getPath());
        } else {
            try {
                return new FileInputStream(getPath());
            } catch (Exception ex) {
                Logger.logErr("Could not create a FileInputStream for \"" + getPath() + "\": " + ex, ex);
                return null;
            }
        }
    }

    /**
     * Returns a BufferedReader
     *
     * @return BufferedReader BufferedReader
     */
    public final BufferedReader getReader() {
        InputStreamReader isr = new InputStreamReader(createInputStream());
        BufferedReader br = new BufferedReader(isr);
        return br;
    }

    /**
     * Creates an OutputStream
     *
     * @param append Boolean If anything should be added to file or should it
     * overwrite it
     * @return OutputStream OutputStream
     */
    public final OutputStream createOutputstream(boolean append) {
        if (isRelative()) {
            return null;
        } else {
            try {
                return new FileOutputStream(getPath(), append);
            } catch (Exception ex) {
                Logger.logErr("Could not create a FileOutputStream for \"" + getPath() + "\": " + ex, ex);
                return null;
            }
        }
    }

    /**
     * Returns a BufferedWriter
     *
     * @param append Boolean If anything should be added to file or should it
     * overwrite it
     * @return BufferedWriter BufferedWriter
     */
    public final BufferedWriter getWriter(boolean append) {
        OutputStreamWriter osw = new OutputStreamWriter(createOutputstream(append));
        BufferedWriter bw = new BufferedWriter(osw);
        return bw;
    }

    @Override
    public final String toString() {
        return getPath();
    }
    
    public final String getName() {
        return toFile().getName();
    }

    /**
     * Returns if this AdvancedFile should be a file or a directory
     * @return <tt>true</tt> if this AdvancedFile should be a file
     */
    public final boolean shouldBeFile() {
        return shouldBeFile;
    }

    /**
     * Sets if this AdvancedFile should be a file or a directory
     * @param file Boolean If this AdvancedFile should be a file
     * @return A reference to this AdvancedFile
     */
    public final AdvancedFile setShouldBeFile(boolean shouldBeFile) {
        this.shouldBeFile = shouldBeFile;
        return this;
    }
    
    public final boolean parseShouldBeFile() {
        return (shouldBeFile = toFile().getName().contains(EXTENSION_SEPARATOR));
    }

}
