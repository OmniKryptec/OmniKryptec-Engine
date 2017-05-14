package omnikryptec.test;

/**
 *
 * @author Panzer1119
 */
public interface Saveable {
    
    public Object[] toData();

    default public String getName() {
        return getClass().getSimpleName();
    }
    
}
