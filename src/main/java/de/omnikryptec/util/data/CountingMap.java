package de.omnikryptec.util.data;

import java.util.HashMap;
import java.util.Map;

public class CountingMap<T> {

	private Map<T, Long> map = new HashMap<>();

	public long get(T t) {
		return map.containsKey(t)?map.get(t):0;
	}

	public long increment(T t) {
		long l;
		map.put(t, l = get(t) + 1);
		return l;
	}

	public long decrement(T t) {
		long l;
		map.put(t, l = get(t) - 1);
		return l;
	}

	public long remove(T t) {
		return map.remove(t);
	}
}
