package omnikryptec.lang;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
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
import java.util.HashMap;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;

/**
 *
 * @author Panzer1119
 */
public class LanguageManager {
    
    private static final String LANGUAGEFILEPREFIX = "lang_";
    private static final int CODESHORTLENGTH = 2;
    private static final String LANGUAGEPATHSEPARATOR = "/";
    public static final String STANDARDLANGUAGEPATH = "/omnikryptec/lang";
    
    private static final ArrayList<ILanguage> LANGUAGEINTERFACES = new ArrayList<>();
    public static final ArrayList<Language> LANGUAGES = new ArrayList<>();
    public static Language languageActive = null;
    
    private static final HashMap<String, String> languageDefault = new HashMap<>();
    private static boolean isCollecting = false;
    
    static {
        loadLanguages();
        setLanguage("EN");
    }
    
    public static final String getLang(String key, String defaultValue) {
        return getLang(languageActive, key, defaultValue);
    }
    
    public static final String getLang(Language language, String key, String defaultValue) {
        if(language == null) {
            return null;
        }
        if(isCollecting) {
            languageDefault.put(key, defaultValue);
        }
        return language.getProperty(key, defaultValue);
    }
    
    public static final Language getLanguageIdentifierByCode(String languageCodeShort) {
        for(Language l : LANGUAGES) {
            if(l.getLanguageCodeShort().equalsIgnoreCase(languageCodeShort)) {
                return l;
            }
        }
        return null;
    }
    
    public static final Language getLanguageIdentifierByName(String languageRawName) {
        for(Language l : LANGUAGES) {
            if(l.getLanguageRawName().equalsIgnoreCase(languageRawName)) {
                return l;
            }
        }
        return null;
    }
    
    public static final ArrayList<String> getAvailableLanguagesResource(String path) {
        final ArrayList<String> output = new ArrayList<>();
        try {
            final URI uri = LanguageManager.class.getResource(path).toURI();
            FileSystem fileSystem = null;
            Path myPath;
            if(uri.getScheme().equalsIgnoreCase("jar")) {
                fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object> emptyMap());
                myPath = fileSystem.getPath(path);
            } else {
                myPath = Paths.get(uri);
            }
            FileVisitor<Path> fv = new SimpleFileVisitor<Path>() {
                
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String path_temp = file.toString();
                    if(path_temp.contains(path.replaceAll(LANGUAGEPATHSEPARATOR, File.separator + File.separator))) {
                        path_temp = path_temp.substring(path_temp.indexOf(path.replaceAll(LANGUAGEPATHSEPARATOR, File.separator + File.separator)) + path.replaceAll(LANGUAGEPATHSEPARATOR, File.separator + File.separator).length() + 1).replaceAll(File.separator + File.separator, LANGUAGEPATHSEPARATOR);
                    }
                    if(path_temp.startsWith(LANGUAGEFILEPREFIX)) {
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
            if(fileSystem != null) {
                fileSystem.close();
            }
        } catch (Exception ex) {
            Logger.logErr("Error while getting all available languages: " + ex, ex);
        }
        return output;
    }
    
    public static final void loadLanguages() {
        setLanguages(loadLanguagesFromResource(STANDARDLANGUAGEPATH));
    }
    
    public static final ArrayList<Language> loadLanguagesFromResource(String path) {
        if(path == null) {
            Logger.log("Error while loading Languages from resource, path is invalid", LogLevel.ERROR);
            return null;
        }
        final ArrayList<Language> languagesLoaded = new ArrayList<>();
        final ArrayList<String> paths = getAvailableLanguagesResource(path);
        for(String p : paths) {
            languagesLoaded.add(loadLanguageFromResource(p));
        }
        Logger.log(String.format("Loaded %d Language%s", languagesLoaded.size(), (languagesLoaded.size() != 1 ? "s" : "")), LogLevel.FINE);
        return languagesLoaded;
    }
    
    public static final String fileToLanguageCodeShort(File file) {
        return file.getName().substring(LANGUAGEFILEPREFIX.length(), LANGUAGEFILEPREFIX.length() + CODESHORTLENGTH).toUpperCase();
    }
    
    public static final Language loadLanguageFromResource(String path) {
        if(path == null || path.isEmpty()) {
            Logger.log("Error while loading Language from resource, path is invalid", LogLevel.ERROR);
            return null;
        }
        try {
            String languageCodeShort = fileToLanguageCodeShort(new File(path));
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
    
    public static final Language loadLanguageFromFile(File file) {
        if(file == null) {
            return null;
        } else if(!file.exists()) {
            Logger.log("Error while loading Language from file, file does not exist", LogLevel.ERROR);
            return null;
        } else if(!file.isFile()) {
            Logger.log("Error while loading Language from file, file is not a file", LogLevel.ERROR);
            return null;
        }
        try {
            final String languageCodeShort = fileToLanguageCodeShort(file);
            final FileInputStream fis = new FileInputStream(file);
            final BufferedInputStream bis = new BufferedInputStream(fis);
            final Language language = loadLanguageFromInputStream(bis);
            language.setLanguageCodeShort(languageCodeShort);
            language.setLanguageRawName(language.getProperty(languageCodeShort, languageCodeShort));
            bis.close();
            fis.close();
            Logger.log(String.format("Loaded Language \"%s\" from file", language.toString()), LogLevel.FINE);
            return language;
        } catch (Exception ex) {
            Logger.logErr("Error while loading Language from file: " + ex, ex);
            return null;
        }
    }
    
    public static final Language loadLanguageFromInputStream(InputStream is, String languageCodeShort, String languageRawName) {
        return loadLanguageFromInputStream(is).setLanguageCodeShort(languageCodeShort).setLanguageRawName(languageRawName);
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
    
    public static final void setLanguages(ArrayList<Language> LANGUAGES) {
        LanguageManager.LANGUAGES.clear();
        for(Language language : LANGUAGES) {
            LanguageManager.LANGUAGES.add(language);
        }
    }
    
    public static final void setLanguage(String languageCodeShort) {
        if(languageCodeShort == null) {
            return;
        }
        final Language language = getLanguageIdentifierByCode(languageCodeShort);
        if(language == null) {
            Logger.log(String.format("Language (%s) not found", languageCodeShort), LogLevel.WARNING);
        } else {
            setLanguage(language);
        }
    }
    
    public static final void setLanguage(Language language) {
        boolean newLanguage = (LanguageManager.languageActive != language);
        LanguageManager.languageActive = language;
        Logger.log(String.format("Changed Language to %s", language), LogLevel.FINE);
        if(newLanguage) {
            notifyAllILanguageInterfaces();
        }
    }
    
    protected static final void notifyAllILanguageInterfaces() {
        for(ILanguage l : LANGUAGEINTERFACES) {
            l.reloadLanguage();
        }
    }
    
    public static final void addLanguageListener(ILanguage l) {
        LANGUAGEINTERFACES.add(l);
    }
    
    public static final void removeLanguageListener(ILanguage l) {
        LANGUAGEINTERFACES.remove(l);
    }
    
    public static final void collectAllLanguageKeys(File file) {
        if(file == null || (file.exists() && !file.isFile())) {
            return;
        }
        new Thread(() -> {
            languageDefault.clear();
            try {
                isCollecting = true;
                notifyAllILanguageInterfaces();
                isCollecting = false;
                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);
                for(String key : languageDefault.keySet()) {
                    bw.write(key + "=" + languageDefault.get(key));
                    bw.newLine();
                }
                bw.close();
                fw.close();
            } catch (Exception ex) {
                Logger.logErr("Error while collecting all Language keys: " + ex, ex);
            }
            languageDefault.clear();
        }).start();
    }
    
}
