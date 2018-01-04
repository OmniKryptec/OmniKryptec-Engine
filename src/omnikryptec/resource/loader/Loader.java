package omnikryptec.resource.loader;

import de.codemakers.io.file.AdvancedFile;

/**
 * Loader Interface
 *
 * @author Panzer1119
 */
public interface Loader {

    public boolean load(AdvancedFile advancedFile, AdvancedFile superFile, ResourceLoader resourceLoader);

    public LoadingType accept(AdvancedFile advancedFile, AdvancedFile superFile, ResourceLoader resourceLoader);

    default String generateName(AdvancedFile advancedFile, AdvancedFile superFile) {
        String path = advancedFile.getPath();
        if (superFile.isDirectory() /*&& !superFile.isIntern()*/) {
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
