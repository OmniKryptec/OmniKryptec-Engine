package de.omnikryptec.resource.loadervpc;

import java.util.Collection;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import de.omnikryptec.util.Util;

public class DefaultResourceProvider implements ResourceProvider {

    private Table<Class<?>, String, Object> resourceTable;

    public DefaultResourceProvider() {
	this.resourceTable = HashBasedTable.create();
    }

    @Override
    public <T> T get(Class<T> clazz, String name) {
	return (T) resourceTable.get(clazz, name);
    }

    @Override
    public <T> Collection<T> getAll(Class<T> clazz) {
	return (Collection<T>) resourceTable.row(clazz).values();
    }

    @Override
    public void add(Object resource, String name, boolean override) {
	Util.ensureNonNull(resource, "Resource must not be null!");
	Util.ensureNonNull(name, "Name must not be null!");
	boolean contained = resourceTable.contains(resource.getClass(), name);
	if (!contained || override) {
	    resourceTable.put(resource.getClass(), name, resource);
	}
    }

    @Override
    public void clear() {
	resourceTable.clear();
    }

}
