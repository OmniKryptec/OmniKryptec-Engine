package de.omnikryptec.resource.loadervpc;

import java.util.Collection;

public interface ResourceProvider {

	<T extends Resource> T get(Class<T> clazz, String name);

	<T extends Resource> Collection<T> getAll(Class<T> clazz);

}
