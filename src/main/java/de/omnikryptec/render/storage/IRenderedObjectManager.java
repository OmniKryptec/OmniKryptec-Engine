package de.omnikryptec.render.storage;

import java.util.Collection;
import java.util.List;

public interface IRenderedObjectManager {
    
    <T extends RenderedObject> List<T> getFor(RenderedObjectType type);
    void clear(RenderedObjectType type);    
    
    void add(RenderedObjectType type, RenderedObject renderedObject);
    void addAll(RenderedObjectType type, Collection<RenderedObject> collection);
    
    void remove(RenderedObjectType type, RenderedObject renderedObject);
    
    void addListener(RenderedObjectType type, IRenderedObjectListener listener);
    void removeListener(RenderedObjectType type, IRenderedObjectListener listener);
}
