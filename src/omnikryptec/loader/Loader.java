package omnikryptec.loader;

import omnikryptec.util.AdvancedFile;

/**
 * Loader Interface
 * @author Panzer1119
 */
public interface Loader {

    public boolean load(AdvancedFile advancedFile, AdvancedFile superFile, ResourceLoader resourceLoader);

    public String[] getExtensions();

    public String[] getBlacklist();

    default String generateName(AdvancedFile advancedFile, AdvancedFile superFile) {
        String path = advancedFile.getPath();
        if(superFile.isDirectory() && !superFile.isIntern()) {
            path = path.replace(superFile.getPath(), "");
        }
        String s = path.replace(AdvancedFile.PATH_SEPARATOR, ":");
        if (s.startsWith(":")) {
            s = s.substring(1, s.length());
        }
        if (s.endsWith(":")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }
    
}
