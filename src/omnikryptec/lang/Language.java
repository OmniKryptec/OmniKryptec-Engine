package omnikryptec.lang;

import java.util.Properties;

/**
 *
 * @author Panzer1119
 */
public class Language extends Properties {
    
    private String languageCodeShort = "";
    private String languageRawName = "";

    public Language() {
        this("", "");
    }
    
    public Language(String languageCodeShort, String languageRawName) {
        this.languageCodeShort = languageCodeShort;
        this.languageRawName = languageRawName;
    }

    public Language setLanguageCodeShort(String languageCodeShort) {
        this.languageCodeShort = languageCodeShort;
        return this;
    }
    
    public String getLanguageCodeShort() {
        return languageCodeShort;
    }

    public Language setLanguageRawName(String languageRawName) {
        this.languageRawName = languageRawName;
        return this;
    }

    public String getLanguageRawName() {
        return languageRawName;
    }
    
    public String getLanguageName() {
        return getLanguageName(this);
    }
    
    public String getLanguageName(Language language) {
        return language.getProperty(languageRawName, languageRawName);
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(o instanceof Language) {
            return ((Language) o).languageCodeShort.equalsIgnoreCase(languageCodeShort);
        } else {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return getLanguageName();
    }
        
}
