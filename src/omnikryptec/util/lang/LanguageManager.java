package omnikryptec.util.lang;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;

import omnikryptec.util.AdvancedFile;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

/**
 *
 * @author Panzer1119
 */
public class LanguageManager {

    public static final String STANDARDLANGUAGEPATH = "/omnikryptec/util/lang";

    private static final ArrayList<ILanguage> LANGUAGEINTERFACES = new ArrayList<>();
    public static final ArrayList<Language> LANGUAGES = new ArrayList<>();
    public static Language languageActive = null;

    private static final HashMap<String, String> languageDefault = new HashMap<>();
    private static boolean isCollecting = false;

    static {
        loadLanguages();
        setLanguage("EN");
    }

    public static final void initialize() {
        // Nothing, this function only loads automatically all standard
        // languages
    }

    public static final String getLang(String key, String defaultValue) {
        return getLang(languageActive, key, defaultValue);
    }

    public static final String getLang(Language language, String key, String defaultValue) {
        if (language == null) {
            return defaultValue;
        }
        if (isCollecting) {
            languageDefault.put(key, defaultValue);
        }
        return language.getProperty(key, defaultValue);
    }

    public static final Language getLanguageIdentifierByCode(String languageCodeShort) {
        for (Language l : LANGUAGES) {
            if (l.getLanguageCodeShort().equalsIgnoreCase(languageCodeShort)) {
                return l;
            }
        }
        return null;
    }

    public static final Language getLanguageIdentifierByName(String languageRawName) {
        for (Language l : LANGUAGES) {
            if (l.getLanguageRawName().equalsIgnoreCase(languageRawName)) {
                return l;
            }
        }
        return null;
    }

    public static final void loadLanguages() {
        setLanguages(Language.ofResources(STANDARDLANGUAGEPATH));
    }

    public static final void setLanguages(ArrayList<Language> LANGUAGES) {
        LanguageManager.LANGUAGES.clear();
        for (Language language : LANGUAGES) {
            LanguageManager.LANGUAGES.add(language);
        }
    }

    public static final void setLanguage(String languageCodeShort) {
        if (languageCodeShort == null) {
            return;
        }
        final Language language = getLanguageIdentifierByCode(languageCodeShort);
        if (language == null) {
            Logger.log(String.format("Language (%s) not found", languageCodeShort), LogLevel.WARNING);
        } else {
            setLanguage(language);
        }
    }

    public static final void setLanguage(Language language) {
        boolean newLanguage = (LanguageManager.languageActive != language);
        LanguageManager.languageActive = language;
        Logger.log(String.format("Changed Language to %s", language), LogLevel.FINE);
        if (newLanguage) {
            notifyAllILanguageInterfaces();
        }
    }

    protected static final void notifyAllILanguageInterfaces() {
        for (ILanguage l : LANGUAGEINTERFACES) {
            l.reloadLanguage();
        }
    }

    public static final void addLanguageListener(ILanguage l) {
        LANGUAGEINTERFACES.add(l);
    }

    public static final void removeLanguageListener(ILanguage l) {
        LANGUAGEINTERFACES.remove(l);
    }

    public static final void collectAllLanguageKeys(AdvancedFile file) {
        if (file == null || (file.exists() && !file.isFile())) {
            return;
        }
        new Thread(() -> {
            languageDefault.clear();
            try {
                isCollecting = true;
                notifyAllILanguageInterfaces();
                isCollecting = false;
                BufferedWriter bw = file.getWriter(false);
                for (String key : languageDefault.keySet()) {
                    bw.write(key + "=" + languageDefault.get(key));
                    bw.newLine();
                }
                bw.close();
            } catch (Exception ex) {
                Logger.logErr("Error while collecting all Language keys: " + ex, ex);
            }
            languageDefault.clear();
        }).start();
    }

}
