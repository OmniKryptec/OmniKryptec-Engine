package omnikryptec.lang;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import omnikryptec.logger.LogEntry;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;

/**
 *
 * @author Panzer1119
 */
public class LanguageManager {
    
    private static final String languageFilePrefix = "lang_";
    private static final int codeShortLength = 2;
    
    public static final ArrayList<Language> languages = new ArrayList<>();
    public static Language languageActive = null;
    
    public static final String getLang(String key, String defaultValue) {
        return getLang(languageActive, key, defaultValue);
    }
    
    public static final String getLang(Language language, String key, String defaultValue) {
        if(language == null) {
            return null;
        }
        return language.getProperty(key, defaultValue);
    }
    
    public static final Language getLanguageIdentifierByCode(String languageCodeShort) {
        for(Language l : languages) {
            if(l.getLanguageCodeShort().equalsIgnoreCase(languageCodeShort)) {
                return l;
            }
        }
        return null;
    }
    
    public static final Language getLanguageIdentifierByName(String languageRawName) {
        for(Language l : languages) {
            if(l.getLanguageRawName().equalsIgnoreCase(languageRawName)) {
                return l;
            }
        }
        return null;
    }
    
    public static final String fileToLanguageCodeShort(File file) {
        return file.getName().substring(languageFilePrefix.length(), languageFilePrefix.length() + codeShortLength);
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
            String languageCodeShort = fileToLanguageCodeShort(file);
            String languageRawName = "";
            FileInputStream fis = new FileInputStream(file);
            Language language = loadLanguageFromInputStream(fis, languageCodeShort, languageRawName);
            fis.close();
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
        Language language = new Language();
        try {
            language.load(is);
            return language;
        } catch (Exception ex) {
            Logger.logErr("Error while loading Language: " + ex, ex);
            return null;
        }
    }
    
}
