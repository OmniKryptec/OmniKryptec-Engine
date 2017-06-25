package omnikryptec.loader;

/**
 *
 * @author Panzer1119
 */
public class RessourceData {
    
    private final String name;
    private final ResourceObject data;
    
    public RessourceData(String name, ResourceObject data) {
        this.name = name;
        this.data = data;
    }

    public final String getName() {
        return name;
    }

    public final ResourceObject getData() {
        return data;
    }
    
}
