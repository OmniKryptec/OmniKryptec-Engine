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

package de.omnikryptec.ecs.component;

import java.util.HashMap;
import java.util.Map;

public class ComponentType {
    
    private static int index = 0;
    private static Map<Class<? extends Component>, ComponentType> componentTypes = new HashMap<>();
    private int id;
    
    private ComponentType() {
        id = index++;
    }
    
    public static ComponentType of(Class<? extends Component> clazz) {
        if (!componentTypes.containsKey(clazz)) {
            synchronized (componentTypes) {
                componentTypes.put(clazz, new ComponentType());
            }
        }
        return componentTypes.get(clazz);
    }
    
    public static ComponentType ofExisting(Class<? extends Component> clazz) {
        return componentTypes.get(clazz);
    }
    
    public static int getTypes() {
        return index;
    }
    
    public int getId() {
        return id;
    }
    
    @Override
    public int hashCode() {
        return id;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof ComponentType) {
            if (id == ((ComponentType) obj).id) {
                return true;
            }
        }
        return false;
    }
    
}
