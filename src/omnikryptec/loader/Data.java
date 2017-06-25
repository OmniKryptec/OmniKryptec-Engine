package omnikryptec.loader;

/**
 *
 * @author Panzer1119
 */
public class Data {
    
    private final String name;
    private final RessourceObject data;
    
    public Data(String name, RessourceObject data) {
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
