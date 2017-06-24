package omnikryptec.loader;

/**
 *
 * @author Panzer1119
 */
public interface Loader {
    
    public Object load(Object object);
    
    public String[] getExtensions();
    
    public String[] getBlackList();
    
}
