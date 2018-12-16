/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.resource.loadervpc;

import java.util.Collection;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import de.omnikryptec.util.Util;

public class DefaultResourceProvider implements ResourceProvider {
    
    private final Table<Class<?>, String, Object> resourceTable;
    
    public DefaultResourceProvider() {
        this.resourceTable = HashBasedTable.create();
    }
    
    @Override
    public <T> T get(final Class<T> clazz, final String name) {
        return (T) this.resourceTable.get(clazz, name);
    }
    
    @Override
    public <T> Collection<T> getAll(final Class<T> clazz) {
        return (Collection<T>) this.resourceTable.row(clazz).values();
    }
    
    @Override
    public void add(final Object resource, final String name, final boolean override) {
        Util.ensureNonNull(resource, "Resource must not be null!");
        Util.ensureNonNull(name, "Name must not be null!");
        final boolean contained = this.resourceTable.contains(resource.getClass(), name);
        if (!contained || override) {
            this.resourceTable.put(resource.getClass(), name, resource);
        }
    }
    
    @Override
    public void clear() {
        this.resourceTable.clear();
    }
    
}
