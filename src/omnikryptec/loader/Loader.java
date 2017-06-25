package omnikryptec.loader;

import omnikryptec.util.AdvancedFile;

/**
 * Loader Interface
 * @author Panzer1119
 */
public interface Loader {

    public ResourceObject load(AdvancedFile advancedFile);

    public String[] getExtensions();

    public String[] getBlacklist();

    default String generateName(AdvancedFile advancedFile, AdvancedFile superfile) {
        String s = advancedFile.getPath().replace(superfile.getPath(), "").replace(AdvancedFile.PATH_SEPARATOR, ":");
        if (s.startsWith(":")) {
            s = s.substring(1, s.length());
        }
        if (s.endsWith(":")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }
}
