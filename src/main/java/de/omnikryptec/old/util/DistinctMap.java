/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.old.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * DistinctMap
 *
 * @author Panzer1119
 */
public class DistinctMap<A extends Object, B extends Object> {

    public static class DistinctEntry<A extends Object, B extends Object> {

	private DistinctMap<A, B> map = null;
	public A a;
	public B b;

	public DistinctEntry(A a, B b) {
	    this.a = a;
	    this.b = b;
	}

	public final A getA() {
	    return a;
	}

	public final DistinctEntry<A, B> setA(A a) {
	    this.a = a;
	    return this;
	}

	public final B getB() {
	    return b;
	}

	public final DistinctEntry<A, B> setB(B b) {
	    this.b = b;
	    return this;
	}

	public final DistinctMap<A, B> getDistinctMap() {
	    return map;
	}

	private final DistinctEntry<A, B> setDistinctMap(DistinctMap<A, B> map) {
	    this.map = map;
	    return this;
	}

	public final DistinctMap<A, B> remove() {
	    if (map != null) {
		map.remove(a, b);
		map = null;
	    }
	    return map;
	}

	public final boolean equals(A a, B b) {
	    return (a == null ? this.a == null : a.equals(this.a)) && (b == null ? this.b == null : b.equals(this.b));
	}

	@Override
	public final boolean equals(Object object) {
	    if (object == null) {
		return false;
	    }
	    if (this == object) {
		return true;
	    }
	    if (object instanceof DistinctEntry) {
		final DistinctEntry<?, ?> entry = (DistinctEntry<?, ?>) object;
		try {
		    final DistinctEntry<A, B> entry_ = (DistinctEntry<A, B>) entry;
		    return entry_.equals(a, b);
		} catch (Exception ex) {
		    return false;
		}
	    } else if (object.equals(a) || object.equals(b)) {
		return true;
	    }
	    return false;
	}

	static final <A, B> DistinctEntry<A, B> of(DistinctMap<A, B> map, A a, B b) {
	    return new DistinctEntry<>(a, b).setDistinctMap(map);
	}

    }

    private final ConcurrentLinkedQueue<DistinctEntry<A, B>> entries = new ConcurrentLinkedQueue<>();

    public DistinctMap() {
	this(null, null);
    }

    public DistinctMap(List<A> as, List<B> bs) {
	if (as != null && bs != null) {
	    if (as.size() >= bs.size()) {
		as.stream().forEach((a) -> add(a, bs.isEmpty() ? null : bs.remove(0)));
	    } else {
		bs.stream().forEach((b) -> add(as.isEmpty() ? null : as.remove(0), b));
	    }
	} else if (as != null) {
	    as.stream().forEach((a) -> addA(a));
	} else if (bs != null) {
	    bs.stream().forEach((b) -> addB(b));
	}
    }

    public final Set<DistinctEntry<A, B>> entrySet() {
	return new HashSet<>(entries);
    }

    public final int size() {
	return entries.size();
    }

    public final boolean isEmpty() {
	return entries.isEmpty();
    }

    public final void clear() {
	entries.clear();
    }

    public final DistinctEntry<A, B> add(A a, B b) {
	DistinctEntry<A, B> entry = get(a, b);
	if (entry == null) {
	    entry = DistinctEntry.of(this, a, b);
	    entries.add(entry);
	}
	return entry;
    }

    public final DistinctEntry<A, B> addA(A a) {
	DistinctEntry<A, B> entry = getA(a);
	if (entry == null) {
	    entry = DistinctEntry.of(this, a, null);
	    entries.add(entry);
	}
	return entry;
    }

    public final DistinctEntry<A, B> addB(B b) {
	DistinctEntry<A, B> entry = getB(b);
	if (entry == null) {
	    entry = DistinctEntry.of(this, null, b);
	    entries.add(entry);
	}
	return entry;
    }

    public final DistinctEntry<A, B> add(Object object) {
	DistinctEntry<A, B> entry = get(object);
	if (entry == null) {
	    try {
		entry = DistinctEntry.of(this, (A) object, null);
	    } catch (Exception ex) {
		try {
		    entry = DistinctEntry.of(this, null, (B) object);
		} catch (Exception ex2) {
		    entry = null;
		}
	    }
	    if (entry != null) {
		entries.add(entry);
	    } else {
		throw new ClassCastException("Wrong Generic Type!");
	    }
	}
	return entry;
    }

    public final DistinctEntry<A, B> put(A a_old, B b_old, A a_new, B b_new) {
	DistinctEntry<A, B> entry = getOr(a_old, b_old);
	if (entry == null) {
	    entry = DistinctEntry.of(this, a_new, b_new);
	    entries.add(entry);
	    return entry;
	}
	if (entry.equals(a_old)) {
	    entry.setA(a_new);
	}
	if (entry.equals(b_old)) {
	    entry.setB(b_new);
	}
	return entry;
    }

    public final DistinctEntry<A, B> putA(A a_old, A a_new, B b_new) {
	return put(a_old, null, a_new, b_new);
    }

    public final DistinctEntry<A, B> putB(B b_old, A a_new, B b_new) {
	return put(null, b_old, a_new, b_new);
    }

    public final DistinctEntry<A, B> getOr(A a, B b) {
	return entries.stream().filter((entry) -> entry.equals(a) || entry.equals(b)).findFirst().orElse(null);
    }

    public final DistinctEntry<A, B> get(A a, B b) {
	return entries.stream().filter((entry) -> entry.equals(a, b)).findFirst().orElse(null);
    }

    public final DistinctEntry<A, B> getA(A a) {
	return entries.stream().filter((entry) -> entry.equals(a)).findFirst().orElse(null);
    }

    public final DistinctEntry<A, B> getB(B b) {
	return entries.stream().filter((entry) -> entry.equals(b)).findFirst().orElse(null);
    }

    public final DistinctEntry<A, B> get(Object object) {
	return entries.stream().filter((entry) -> entry.equals(object)).findFirst().orElse(null);
    }

    public final DistinctMap<A, B> removeOr(A a, B b) {
	entries.removeAll(
		entries.stream().filter((entry) -> entry.equals(a) || entry.equals(b)).collect(Collectors.toList()));
	return this;
    }

    public final DistinctMap<A, B> remove(A a, B b) {
	entries.removeAll(entries.stream().filter((entry) -> entry.equals(a, b)).collect(Collectors.toList()));
	return this;
    }

    public final DistinctMap<A, B> removeA(A a) {
	entries.removeAll(entries.stream().filter((entry) -> entry.equals(a)).collect(Collectors.toList()));
	return this;
    }

    public final DistinctMap<A, B> removeB(B b) {
	entries.removeAll(entries.stream().filter((entry) -> entry.equals(b)).collect(Collectors.toList()));
	return this;
    }

    public final DistinctMap<A, B> remove(Object object) {
	entries.removeAll(entries.stream().filter((entry) -> entry.equals(object)).collect(Collectors.toList()));
	return this;
    }

    public final boolean contains(A a, B b) {
	return get(a, b) != null;
    }

    public final boolean containsA(A a) {
	return getA(a) != null;
    }

    public final boolean containsB(B b) {
	return getB(b) != null;
    }

    public final boolean contains(Object object) {
	return get(object) != null;
    }

    public final A getAbyB(B b) {
	final DistinctEntry<A, B> entry = getB(b);
	if (entry == null) {
	    return null;
	}
	return entry.getA();
    }

    public final B getBbyA(A a) {
	final DistinctEntry<A, B> entry = getA(a);
	if (entry == null) {
	    return null;
	}
	return entry.getB();
    }

    @Override
    public boolean equals(Object object) {
	if (object == null) {
	    return false;
	}
	if (this == object) {
	    return true;
	}
	if (object instanceof DistinctMap) {
	    final DistinctMap<?, ?> map = (DistinctMap<?, ?>) object;
	    try {
		final DistinctMap<A, B> map_ = (DistinctMap<A, B>) map;
		if (map_.entries.equals(entries)) {
		    return true;
		}
		return map_.entries.stream().allMatch((entry) -> entries.contains(entry));
	    } catch (Exception ex) {
		return false;
	    }
	}
	return true;
    }

}
