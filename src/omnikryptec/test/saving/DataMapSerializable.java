package omnikryptec.test.saving;

/**
 * DataMapSerializable
 * @author Panzer1119
 */
public interface DataMapSerializable {
    
    public DataMap toDataMap(DataMap data);
    public static Object fromDataMap(DataMap data) {
        return null;
    }
    
}
