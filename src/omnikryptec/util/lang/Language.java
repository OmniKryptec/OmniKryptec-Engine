package omnikryptec.util.lang;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

import omnikryptec.util.AdvancedFile;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

/**
 *
 * @author Panzer1119
 */
public class Language extends Properties {

    /**
     *
     */
    private static final long serialVersionUID = -5071523675384082116L;
    private static final String LANGUAGEFILEPREFIX = "lang_";
    //private static final int CODESHORTLENGTH = 2;
    private static final String LANGUAGEPATHSEPARATOR = "/";

    private String languageCodeShort = "";
    private String languageRawName = "";

    public Language() {
        this("", "");
    }

    public Language(String languageCodeShort, String languageRawName) {
        this.languageCodeShort = languageCodeShort;
        this.languageRawName = languageRawName;
    }

    public Language(Properties language) {
        addToLanguage(language);
    }

    public final Language setLanguageCodeShort(String languageCodeShort) {
        this.languageCodeShort = languageCodeShort;
        return this;
    }

    public final String getLanguageCodeShort() {
        return languageCodeShort;
    }

    public final Language setLanguageRawName(String languageRawName) {
        this.languageRawName = languageRawName;
        return this;
    }

    public final String getLanguageRawName() {
        return languageRawName;
    }

    public final String getLanguageName() {
        return getLanguageName(this);
    }

    public final String getLanguageName(Language language) {
        return language.getProperty(languageRawName, languageRawName);
    }

    public final Language addToLanguage(Properties language) {
        return addToLanguage(language, false);
    }

    public final Language addToLanguage(Properties language, boolean reloadAll) {
        putAll(language);
        if (reloadAll && LanguageManager.languageActive == this) {
            LanguageManager.notifyAllILanguageInterfaces();
        }
        return this;
    }

    @Override
    public final boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Language) {
            return ((Language) o).languageCodeShort.equalsIgnoreCase(languageCodeShort);
        } else {
            return false;
        }
    }

    @Override
    public final String toString() {
        return String.format("%s (%s)", getLanguageName(), getLanguageCodeShort());
    }

    public static final String fileToLanguageCodeShort(AdvancedFile file) {
        String restName = file.getName().substring(LANGUAGEFILEPREFIX.length());
        int indexPoint = restName.indexOf(".");
        if (indexPoint != -1) {
            restName = restName.substring(0, indexPoint);
        }
        return restName.toUpperCase();
    }

    public static final Language ofResource(String path) {
        if (path == null || path.isEmpty()) {
            Logger.log("Error while loading Language from resource, path is invalid", LogLevel.WARNING);
            return null;
        }
        try {
            String languageCodeShort = fileToLanguageCodeShort(new AdvancedFile(path));
            final Language language = loadLanguageFromInputStream(LanguageManager.class.getResourceAsStream(path));
            language.setLanguageCodeShort(languageCodeShort);
            language.setLanguageRawName(language.getProperty(languageCodeShort, languageCodeShort));
            Logger.log(String.format("Loaded Language \"%s\" from resource", language.toString()), LogLevel.FINE);
            return language;
        } catch (Exception ex) {
            Logger.logErr("Error while loading Language from resource: " + ex, ex);
            return null;
        }
    }

    public static final ArrayList<Language> ofResources(String path) {
        if (path == null) {
            Logger.log("Error while loading Languages from resource, path is invalid", LogLevel.WARNING);
            return null;
        }
        try {
            final ArrayList<Language> languagesLoaded = new ArrayList<>();
            final ArrayList<String> paths = getAvailableLanguagesResource(path);
            for (String p : paths) {
                languagesLoaded.add(ofResource(p));
            }
            Logger.log(String.format("Loaded %d Language%s", languagesLoaded.size(),
                    (languagesLoaded.size() != 1 ? "s" : "")), LogLevel.FINE);
            return languagesLoaded;
        } catch (Exception ex) {
            Logger.logErr("Error while loading Language from resources: " + ex, ex);
            return null;
        }
    }

    public static final Language ofFile(AdvancedFile file) {
        if (file == null) {
            return null;
        } else if (!file.exists()) {
            Logger.log("Error while loading Language from file, file does not exist", LogLevel.WARNING);
            return null;
        } else if (!file.isFile()) {
            Logger.log("Error while loading Language from file, file is not a file", LogLevel.WARNING);
            return null;
        }
        try {
            final String languageCodeShort = fileToLanguageCodeShort(file);
            final BufferedInputStream bis = new BufferedInputStream(file.createInputStream());
            final Language language = loadLanguageFromInputStream(bis);
            language.setLanguageCodeShort(languageCodeShort);
            language.setLanguageRawName(language.getProperty(languageCodeShort, languageCodeShort));
            bis.close();
            Logger.log(String.format("Loaded Language \"%s\" from file", language.toString()), LogLevel.FINE);
            return language;
        } catch (Exception ex) {
            Logger.logErr("Error while loading Language from file: " + ex, ex);
            return null;
        }
    }

    public static final ArrayList<Language> ofFiles(AdvancedFile folder) {
        if (folder == null) {
            return null;
        } else if (!folder.exists()) {
            Logger.log("Error while loading Language from folder, folder does not exist", LogLevel.WARNING);
            return null;
        } else if (!folder.isDirectory()) {
            Logger.log("Error while loading Language from folder, folder is not a folder", LogLevel.WARNING);
            return null;
        }
        try {
            final ArrayList<Language> languagesLoaded = new ArrayList<>();
            folder.listAdvancedFiles().stream().forEach((file) -> {
                languagesLoaded.add(ofFile(file));
            });
            Logger.log(String.format("Loaded %d Language%s", languagesLoaded.size(),
                    (languagesLoaded.size() != 1 ? "s" : "")), LogLevel.FINE);
            return languagesLoaded;
        } catch (Exception ex) {
            Logger.logErr("Error while loading Language from folder: " + ex, ex);
            return null;
        }
    }

    public static final Language loadLanguageFromInputStream(InputStream is, String languageCodeShort,
            String languageRawName) {
        return loadLanguageFromInputStream(is).setLanguageCodeShort(languageCodeShort)
                .setLanguageRawName(languageRawName);
    }

    public static final Language loadLanguageFromInputStream(InputStream is) {
        final Language language = new Language();
        try {
            language.load(is);
            Logger.log("Loaded Language", LogLevel.FINER);
            return language;
        } catch (Exception ex) {
            Logger.logErr("Error while loading Language: " + ex, ex);
            return null;
        }
    }

    public static final ArrayList<String> getAvailableLanguagesResource(String path) {
        final ArrayList<String> output = new ArrayList<>();
        try {
            final URI uri = LanguageManager.class.getResource(path).toURI();
            FileSystem fileSystem = null;
            Path myPath;
            if (uri.getScheme().equalsIgnoreCase("jar")) {
                fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
                myPath = fileSystem.getPath(path);
            } else {
                myPath = Paths.get(uri);
            }
            FileVisitor<Path> fv = new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String path_temp = file.toString();
                    if (path_temp.contains(path.replaceAll(LANGUAGEPATHSEPARATOR, "\\" + AdvancedFile.WINDOWS_SEPARATOR))) {
                        path_temp = path_temp.substring(path_temp.indexOf(path.replaceAll(LANGUAGEPATHSEPARATOR, "\\" + AdvancedFile.WINDOWS_SEPARATOR)) + path.replaceAll(LANGUAGEPATHSEPARATOR, "\\" + AdvancedFile.WINDOWS_SEPARATOR).length() + 1).replaceAll("\\" + AdvancedFile.WINDOWS_SEPARATOR, LANGUAGEPATHSEPARATOR);
                    }
                    if (path_temp.startsWith(path)) {
                        path_temp = path_temp.substring(path.length() + 1);
                    }
                    if (path_temp.startsWith(LANGUAGEFILEPREFIX)) {
                        output.add((path + LANGUAGEPATHSEPARATOR + path_temp));
                    }
                    return FileVisitResult.CONTINUE;
                }

            };
            try {
                Files.walkFileTree(myPath, fv);
            } catch (Exception ex) {
                Logger.logErr("Error while walking through the Language files: " + ex, ex);
            }
            if (fileSystem != null) {
                fileSystem.close();
            }
        } catch (Exception ex) {
            Logger.logErr("Error while getting all available languages: " + ex, ex);
        }
        return output;
    }

}
