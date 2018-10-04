package de.omnikryptec.util.data;

import java.util.HashMap;
import java.util.Map;

public class CountingMap<T> {

	private Map<T, Long> map = new HashMap<>();

	public long get(T t) {
		return map.containsKey(t) ? map.get(t) : 0;
	}

	public long increment(T t) {
		return increment(t, 1);
	}
	
	public long increment(T t, long amount) {
		long l;
		map.put(t, l = get(t) + amount);
		return l;
	}

	public long decrement(T t) {
		return decrement(t, 1);
	}
	
	public long decrement(T t, long amount) {
		long l;
		map.put(t, l = get(t) - amount);
		return l;
	}

	public long remove(T t) {
		return map.remove(t);
	}
}
