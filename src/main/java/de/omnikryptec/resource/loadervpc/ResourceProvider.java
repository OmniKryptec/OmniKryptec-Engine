/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

public interface ResourceProvider {

    <T> T get(Class<T> clazz, String name);

    <T> Collection<T> getAll(Class<T> clazz);

    void add(Object resource, String name, boolean override);

    void clear();

    boolean contains(Class<?> clazz, String name);

    void remove(Class<?> clazz, String name);
}
