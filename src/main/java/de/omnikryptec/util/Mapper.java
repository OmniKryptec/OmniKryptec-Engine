package de.omnikryptec.util;

import java.util.HashMap;
import java.util.Map;

public class Mapper<T> {
    
    private int next = 0;
    private Map<Class<? extends T>, Mapping> mappings = new HashMap<>();
    
    public Mapping of(Class<? extends T> clazz) {
        Mapping mapper = mappings.get(clazz);
        if (mapper == null) {
            mapper = new Mapping();
            mappings.put(clazz, mapper);
        }
        return mapper;
    }
    
    public class Mapping {
        public final int id;
        private final Mapper<T> mapper = Mapper.this;
        
        private Mapping() {
            this.id = next;
            next++;
        }
        
        @Override
        public int hashCode() {
            return id;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (obj instanceof Mapper.Mapping) {
                Mapping other = (Mapping) obj;
                return other.mapper == this.mapper && other.id == this.id;
            }
            return false;
        }
    }
}
