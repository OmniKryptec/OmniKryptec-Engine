package de.omnikryptec.render.objects;

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
    
    default void add(RenderedObject renderedObject) {
        add(renderedObject.type(), renderedObject);
    }
    
    void add(RenderedObjectType type, RenderedObject renderedObject);
    
    default void addAll(RenderedObjectType type, Collection<RenderedObject> collection) {
        for (RenderedObject obj : collection) {
            add(type, obj);
        }
    }
    
    default void remove(RenderedObject renderedObject) {
        remove(renderedObject.type(), renderedObject);
    }
    
    void remove(RenderedObjectType type, RenderedObject renderedObject);
    
    void addListener(RenderedObjectType type, IRenderedObjectListener listener);
    
    void removeListener(RenderedObjectType type, IRenderedObjectListener listener);
}
