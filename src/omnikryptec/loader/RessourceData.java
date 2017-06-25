package omnikryptec.loader;

/**
 *
 * @author Panzer1119
 */
public class RessourceData {
    
    private final String name;
    private final RessourceObject data;
    
    public RessourceData(String name, RessourceObject data) {
        this.name = name;
        this.data = data;
    }

    public final String getName() {
        return name;
    }

    public final RessourceObject getData() {
        return data;
    }
    
}
