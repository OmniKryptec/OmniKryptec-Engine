package omnikryptec.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import omnikryptec.logger.Logger;

/**
 *
 * @author Panzer1119
 */
public class AdvancedFile {
    
    public static final String PATH_SEPARATOR = "/";
    public static final String FILE_SEPARATOR = File.separator;

    private File folder = null;
    private String[] paths = null;
    private String path = null;
    private String separator = PATH_SEPARATOR;
    
    /**
     * Creates an AdvancedFile which is relative
     * @param paths String Array Paths
     */
    public AdvancedFile(String... paths) {
        this(null, paths);
    }
    
    /**
     * Creates an AdvancedFile which can be relative or absolute
     * @param parent Object If File than it can be the Folder, where the Paths is in (If null then this AdvancedFile is a relative path)
     * or if it is an AndvancedFile than this AdvancedFile is a child of the parent AdvancedFile
     * @param paths String Array Paths
     */
    public AdvancedFile(Object parent, String... paths) {
        setParent(parent);
        addPaths(paths);
    }
    
    /**
     * Returns a copy of this AdvancedFile
     * @return AdvancedFile AdvancedFile
     */
    public final AdvancedFile copy() {
        return new AdvancedFile(folder, paths);
    }
    
    /**
     * Adds Paths
     * @param paths String Array Paths
     * @return A reference to this AdvancedFile
     */
    public final AdvancedFile addPaths(String... paths) {
        this.path = null;
        if(paths == null) {
            return this;
        }
        if(this.paths != null) {
            final String[] paths_new = Arrays.copyOf(this.paths, this.paths.length + paths.length);
            for(int i = this.paths.length; i < paths_new.length; i++) {
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
     * @param paths String Array Paths
     * @return A copy of this AdvancedFile
     */
    public final AdvancedFile withPaths(String... paths) {
        return copy().addPaths(paths);
    }
    
    /**
     * Adds Paths before the other Paths
     * @param paths String Array Paths
     * @return A reference to this AdvancedFile
     */
    public final AdvancedFile addPrePaths(String... paths) {
        this.path = null;
        if(paths == null) {
            return this;
        }
        if(this.paths != null) {
            final String[] paths_new = Arrays.copyOf(paths, paths.length + this.paths.length);
            for(int i = paths.length; i < paths_new.length; i++) {
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
     * @param paths String Array Paths
     * @return A copy of this AdvancedFile
     */
    public final AdvancedFile withPrePaths(String... paths) {
        return copy().addPrePaths(paths);
    }
    
    /**
     * Sets the Paths
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
     * @return Stirng Array Paths
     */
    public final String[] getPaths() {
        this.path = null;
        return paths;
    }
    
    /**
     * Returns the Path
     * @return String Path
     */
    public final String getPath() {
        if(path == null) {
            path = createPath();
        }
        return path;
    }
    
    private final String createPath() {
        String path_new = (isRelative() ? "" : folder.getAbsolutePath());
        if(paths != null) {
            for(String path_temp : paths) {
                path_new += separator + path_temp;
            }
        }
        return path_new;
    }
    
    /**
     * Returns if this AdvancedFile is a relative path
     * @return <tt>true</tt> if this AdvancedFile is a relative path
     */
    public final boolean isRelative() {
        return folder == null;
    }
    
    /**
     * Sets the folder
     * @param folder File Folder (If null then this AdvancedFile is a relative path)
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
     * @return File Folder
     */
    public final File getFolder() {
        return folder;
    }
    
    /**
     * Returns a File
     * @return File File
     */
    public final File toFile() {
        return new File(folder, getPath());
    }
    
    /**
     * Returns the parent AdvancedFile or null
     * @return AdvancedFile Parent AdvancedFile
     */
    public final AdvancedFile getParent() {
        if(paths != null && paths.length > 1) {
            return new AdvancedFile(folder, ArrayUtil.copyOf(paths, paths.length - 1));
        } else if(paths != null && paths.length == 1 && !isRelative()) {
            return new AdvancedFile(folder);
        } else if(!isRelative()) {
            return new AdvancedFile(folder.getParentFile());
        } else {
            return null;
        }
    }
    
    /**
     * Sets the parent
     * @param parent Object If File than it can be the Folder, where the Paths is in (If null then this AdvancedFile is a relative path)
     * or if it is an AndvancedFile than this AdvancedFile is a child of the parent AdvancedFile
     * @return A reference to this AdvancedFile
     */
    public final AdvancedFile setParent(Object parent) {
        return setParent(parent, true, true);
    }
    
    private final AdvancedFile setParent(Object parent, boolean withFolder, boolean withPaths) {
        if(withFolder && parent == null) {
            setFolder(null);
        } else {
            if(parent instanceof File) {
                final File file = (File) parent;
                if(withFolder && file.isAbsolute()) {
                    setFolder(file);
                }
                if(withPaths && !file.isAbsolute()) {
                    addPrePaths(file.getPath().split("\\" + FILE_SEPARATOR));
                }
            } else if(parent instanceof AdvancedFile) {
                final AdvancedFile advancedFile = (AdvancedFile) parent;
                if(withFolder) {
                    setFolder(advancedFile.getFolder());
                }
                if(withPaths) {
                    addPrePaths(advancedFile.getPaths());
                }
            }
        }
        return this;
    }
    
    /**
     * Returns a copy of this AdvancedFile with the given parent
     * @param parent Object If File than it can be the Folder, where the Paths is in (If null then this AdvancedFile is a relative path)
     * or if it is an AndvancedFile than this AdvancedFile is a child of the parent AdvancedFile
     * @return A copy of this AdvancedFile
     */
    public final AdvancedFile withParent(Object parent) {
        return copy().setParent(parent);
    }
    
    /**
     * Sets the parents
     * @param parents Object Array If File than it can be the Folder, where the Paths is in (If null then this AdvancedFile is a relative path)
     * or if it is an AndvancedFile than this AdvancedFile is a child of the parent AdvancedFile
     * @return A reference to this AdvancedFile
     */
    public final AdvancedFile setParents(Object... parents) {
        if(parents == null || parents.length == 0) {
            return this;
        }
        setParent(parents[0], true, false);
        for(int i = 0; i < parents.length; i++) {
            setParent(parents[parents.length - i - 1], false, true);
        }
        return this;
    }
    
    /**
     * Returns a copy of this AdvancedFile with the given parents
     * @param parents Object Array If File than it can be the Folder, where the Paths is in (If null then this AdvancedFile is a relative path)
     * or if it is an AndvancedFile than this AdvancedFile is a child of the parent AdvancedFile
     * @return A copy of this AdvancedFile
     */
    public final AdvancedFile withParents(Object... parents) {
        return copy().setParents(parents);
    }
    
    /**
     * Creates an InputStream
     * @return InputStream InputStream
     */
    public final InputStream createInputStream() {
        if(isRelative()) {
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
     * @return BufferedReader BufferedReader
     */
    public final BufferedReader getReader() {
        InputStreamReader isr = new InputStreamReader(createInputStream());
        BufferedReader br = new BufferedReader(isr);
        return br;
    }

    @Override
    public String toString() {
        return getPath();
    }
    
}
