package de.omnikryptec.util.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

//Surprisingly faster than a guava Multiset
public class CountingMap<T> implements Iterable<T> {

    private Map<T, Long> map = new HashMap<>();
    private boolean retainZeros = false;

    public CountingMap() {
	this(false);
    }

    public CountingMap(boolean retainZeros) {
	this.retainZeros = retainZeros;
    }

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
	if (!retainZeros && l == 0) {
	    remove(t);
	}
	return l;
    }

    public long remove(T t) {
	return map.remove(t);
    }

    public Set<T> keySet() {
	return map.keySet();
    }

    @Override
    public Iterator<T> iterator() {
	return map.keySet().iterator();
    }

}
