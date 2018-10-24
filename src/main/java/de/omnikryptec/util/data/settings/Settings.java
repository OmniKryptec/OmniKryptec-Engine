package de.omnikryptec.util.data.settings;

import java.util.HashMap;

public class Settings<K> {

	private final HashMap<K, Object> settings_objects = new HashMap<>();

	public <T> T get(K key) {
		Object obj = settings_objects.get(key);
		if (obj == null) {
			if (key instanceof Defaultable) {
				return (T) ((Defaultable) key).getDefault();
			}
		}
		return (T) obj;
	}

	public final Settings<K> set(K key, Object value) {
		settings_objects.put(key, value);
		return this;
	}

	public final boolean has(K key) {
		return settings_objects.containsKey(key);
	}

}
