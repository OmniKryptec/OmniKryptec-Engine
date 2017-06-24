package omnikryptec.loader;

/**
 *
 * @author Panzer1119
 */
public class Data {
    
    private final String name;
    private final Object data;
    
    public Data(String name, Object data) {
        this.name = name;
        this.data = data;
    }

    public final String getName() {
        return name;
    }

    public final Object getData() {
        return data;
    }
    
}
