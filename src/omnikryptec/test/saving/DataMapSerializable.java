package omnikryptec.test.saving;

/**
 * DataMapSerializable
 * @author Panzer1119
 */
public interface DataMapSerializable {
    
    public String getName();
    public DataMap toDataMap(DataMap data);
    public Object fromDataMap(DataMap data);
    public static Object newInstanceFromDataMap(DataMap data) {
        return null;
    }
    
}
