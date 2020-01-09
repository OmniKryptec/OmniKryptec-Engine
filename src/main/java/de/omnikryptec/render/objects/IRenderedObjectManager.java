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

package de.omnikryptec.render.objects;

import java.util.Collection;
import java.util.List;

public interface IRenderedObjectManager {
    
    <T extends RenderedObject> List<T> getFor(RenderedObjectType type);
    
    default void clear(final RenderedObjectType type) {
        final List<RenderedObject> list = getFor(type);
        for (final RenderedObject obj : list) {
            remove(type, obj);
        }
    }
    
    default void add(final RenderedObject renderedObject) {
        add(renderedObject.type(), renderedObject);
    }
    
    void add(RenderedObjectType type, RenderedObject renderedObject);
    
    default void addAll(final RenderedObjectType type, final Collection<RenderedObject> collection) {
        for (final RenderedObject obj : collection) {
            add(type, obj);
        }
    }
    
    default void remove(final RenderedObject renderedObject) {
        remove(renderedObject.type(), renderedObject);
    }
    
    void remove(RenderedObjectType type, RenderedObject renderedObject);
    
    void addListener(RenderedObjectType type, IRenderedObjectListener listener);
    
    void removeListener(RenderedObjectType type, IRenderedObjectListener listener);
}
