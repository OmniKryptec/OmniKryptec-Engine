package omnikryptec.renderer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import omnikryptec.entity.Entity;
import omnikryptec.model.TexturedModel;

public class RenderMap<K,V>{
	
	private Map<K, V> map = new HashMap<>();
	private K[] keys;
	
	private boolean keysDirty=true;
	
	public void put(K k, V v){
		map.put(k, v);
		keysDirty = true;
	}
	
	public V get(K key){
		return map.get(key);
	}
	
	@SuppressWarnings("unchecked")
	public K[] keysArray(){
		if(keysDirty){
			keys = map.keySet().toArray((K[])new Object[map.size()]);
			keysDirty = false;
		}
		return keys;
	}
	
	public void remove(K tm){
		map.remove(tm);
		keysDirty = true;
	}
	
	public boolean isEmpty(){
		return map.isEmpty();
	}
	
}	
