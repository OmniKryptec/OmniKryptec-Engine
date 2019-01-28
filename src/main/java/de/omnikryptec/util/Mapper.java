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
