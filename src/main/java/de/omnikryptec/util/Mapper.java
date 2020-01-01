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

package de.omnikryptec.util;

import java.util.HashMap;
import java.util.Map;

public class Mapper<T> {
    
    private int next = 0;
    private final Map<Class<? extends T>, Mapping> mappings = new HashMap<>();
    
    public Mapping of(final Class<? extends T> clazz) {
        Mapping mapper = this.mappings.get(clazz);
        if (mapper == null) {
            mapper = new Mapping();
            this.mappings.put(clazz, mapper);
        }
        return mapper;
    }
    
    public class Mapping {
        public final int id;
        private final Mapper<T> mapper = Mapper.this;
        
        private Mapping() {
            this.id = Mapper.this.next;
            Mapper.this.next++;
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
            if (obj instanceof Mapper.Mapping) {
                final Mapping other = (Mapping) obj;
                return other.mapper == this.mapper && other.id == this.id;
            }
            return false;
        }
    }
}
