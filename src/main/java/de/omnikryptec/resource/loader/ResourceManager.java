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

package de.omnikryptec.resource.loader;

import java.util.Collection;

public interface ResourceManager {

    <T extends ResourceObject> T getResource(long id);

    <T extends ResourceObject> T getResource(String name);

    <T extends ResourceObject> T getResource(Class<T> clazz, long id);

    <T extends ResourceObject> T getResource(Class<T> clazz, String name);

    <T extends ResourceObject> Collection<T> getResources(Class<T> clazz);

    Collection<ResourceObject> getAllResources();

    boolean clearResources();

    <T extends ResourceObject> boolean clearResources(Class<T> clazz);

    boolean removeResource(long id);

    boolean removeResource(String name);

    boolean addResources(ResourceObject... resourceObjects);

    <T extends ResourceObject> boolean addResources(Class<T> clazz, T... resourceObjects);

}
