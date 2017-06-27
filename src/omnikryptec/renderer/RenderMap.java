package omnikryptec.renderer;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class RenderMap<K, V> {

    private Class<K> keyclass;
    private Map<K, V> map = new HashMap<>(RenderChunk.DEFAULT_CAPACITY, 0.7f);
    private K[] keys;

    private boolean keysDirty = true;

    public RenderMap(Class<K> keyclass) {
        this.keyclass = keyclass;
    }

    public void put(K k, V v) {
        map.put(k, v);
        keysDirty = true;
    }

    public V get(K key) {
        return map.get(key);
    }

    @SuppressWarnings("unchecked")
    public K[] keysArray() {
        if (keysDirty) {
            keys = map.keySet().toArray((K[]) Array.newInstance(keyclass, map.size()));
            keysDirty = false;
        }
        return keys;
    }

    public void remove(K tm) {
        map.remove(tm);
        keysDirty = true;
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

}
