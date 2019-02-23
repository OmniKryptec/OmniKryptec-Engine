package de.omnikryptec.render.storage;

import java.util.HashMap;
import java.util.Map;

public class RenderedObjectType {
    private static int next = 0;
    private static Map<Class<? extends RenderedObject>, RenderedObjectType> mappings = new HashMap<>();
    
    public static RenderedObjectType of(final Class<? extends RenderedObject> clazz) {
        RenderedObjectType mapper = mappings.get(clazz);
        if (mapper == null) {
            mapper = new RenderedObjectType();
            mappings.put(clazz, mapper);
        }
        return mapper;
    }
    
    public final int id;
    
    private RenderedObjectType() {
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
        if (obj instanceof RenderedObjectType) {
            return ((RenderedObjectType) obj).id == this.id;
        }
        return false;
    }
}
