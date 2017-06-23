package omnikryptec.test.saving;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DataMap
 * @author Panzer1119
 */
public class DataMap extends HashMap<String, Object> {
    
    private final String name;
    
    public DataMap(String name) {
        this.name = name;
    }
    
    public final String getName() {
        return name;
    }
    
    public final <E> List<E> getList(String name, Class<? extends E> type) {
        if(name == null || type == null) {
            return null;
        }
        final Object object = get(name);
        if(object == null) {
            return null;
        }
        return (List<E>) (List<?>) object;
    }
    
    public final <K, V> Map<K, V> getMap(String name, Class<? extends K> typeKey, Class<? extends V> typeValue) {
        if(name == null || typeKey == null || typeValue == null) {
            return null;
        }
        final Object object = get(name);
        if(object == null) {
            return null;
        }
        return (Map<K, V>) (Map<?, ?>) object;
    }
    
    public final DataMap getDataMap(String name) {
        if(name == null) {
            return null;
        }
        final Object object = get(name);
        if(object == null) {
            return null;
        }
        return (DataMap) object;
    }
    
}
