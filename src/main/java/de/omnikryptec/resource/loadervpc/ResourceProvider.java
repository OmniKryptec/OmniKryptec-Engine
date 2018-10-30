package de.omnikryptec.resource.loadervpc;

import java.util.Collection;

public interface ResourceProvider {

    <T> T get(Class<T> clazz, String name);

    <T> Collection<T> getAll(Class<T> clazz);

    void add(Object resource, String name, boolean override);

    void clear();
    
}
