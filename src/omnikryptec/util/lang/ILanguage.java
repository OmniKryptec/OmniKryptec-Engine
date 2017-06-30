package omnikryptec.util.lang;

/**
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
