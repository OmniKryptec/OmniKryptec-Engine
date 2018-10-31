package de.omnikryptec.util.data.settings;

import java.util.HashMap;

public class Settings<K> {

    private final HashMap<K, Object> settings_objects = new HashMap<>();

    public <T> T get(K key) {
	Object obj = settings_objects.get(key);
	if (obj == null) {
	    if (key instanceof Defaultable) {
		obj = ((Defaultable) key).getDefault();
	    }
	}
	return (T) obj;
    }

    public <T> T getOrDefault(K key, T def) {
	T t = get(key);
	return t == null ? def : t;
    }

    public Settings<K> set(K key, Object value) {
	settings_objects.put(key, value);
	return this;
    }

    public boolean has(K key) {
	return settings_objects.containsKey(key);
    }

}
