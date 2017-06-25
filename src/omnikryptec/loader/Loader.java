package omnikryptec.loader;

import omnikryptec.util.AdvancedFile;

/**
 *
 * @author Panzer1119
 */
public interface Loader {
    
    public RessourceObject load(AdvancedFile advancedFile);
    
    public String[] getExtensions();
    
    public String[] getBlacklist();
    
}
