package omnikryptec.loader;

import omnikryptec.util.AdvancedFile;

/**
 *
 * @author Panzer1119
 */
public interface Loader {
    
    public ResourceObject load(AdvancedFile advancedFile);
    
    public String[] getExtensions();
    
    public String[] getBlacklist();
    
}
