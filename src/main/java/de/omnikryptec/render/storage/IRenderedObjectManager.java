package de.omnikryptec.render.storage;

import java.util.Collection;
import java.util.List;

public interface IRenderedObjectManager {
    
    <T extends RenderedObject> List<T> getFor(RenderedObjectType type);
    
    default void clear(RenderedObjectType type) {
        List<RenderedObject> list = getFor(type);
        for (RenderedObject obj : list) {
            remove(type, obj);
        }
    }
    
    void add(RenderedObjectType type, RenderedObject renderedObject);
    
    default void addAll(RenderedObjectType type, Collection<RenderedObject> collection) {
        for (RenderedObject obj : collection) {
            add(type, obj);
        }
    }
    
    void remove(RenderedObjectType type, RenderedObject renderedObject);
    
    void addListener(RenderedObjectType type, IRenderedObjectListener listener);
    
    void removeListener(RenderedObjectType type, IRenderedObjectListener listener);
}
