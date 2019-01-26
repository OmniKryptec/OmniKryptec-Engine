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

package de.omnikryptec.ecs.component;

import java.util.HashMap;
import java.util.Map;

public class ComponentType {

    private static int next = 0;
    private static Map<Class<? extends Component>, ComponentType> mappings = new HashMap<>();

    public static ComponentType of(final Class<? extends Component> clazz) {
        ComponentType mapper = mappings.get(clazz);
        if (mapper == null) {
            mapper = new ComponentType();
            mappings.put(clazz, mapper);
        }
        return mapper;
    }

    public final int id;

    private ComponentType() {
        this.id = next;
        next++;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof ComponentType) {
            return ((ComponentType) obj).id == this.id;
        }
        return false;
    }

}
