package omnikryptec.loader;

/**
 *
 * @author Panzer1119
 */
public class ResourceData {
    
    private final String name;
    private final ResourceObject data;
    
    public ResourceData(String name, ResourceObject data) {
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
