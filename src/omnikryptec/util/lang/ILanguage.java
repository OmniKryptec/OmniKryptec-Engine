package omnikryptec.util.lang;

/**
 * ILanguage used to globally reload every structure that uses the
 * LanguageManager
 *
 * @author Panzer1119
 */
public interface ILanguage {

    public void reloadLanguage();

    default public String getLang(Language language, String key, String defaultValue) {
        return LanguageManager.getLang(language, key, defaultValue);
    }

    default public String getLang(String key, String defaultValue) {
        return LanguageManager.getLang(key, defaultValue);
    }

}
