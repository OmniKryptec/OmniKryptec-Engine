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
