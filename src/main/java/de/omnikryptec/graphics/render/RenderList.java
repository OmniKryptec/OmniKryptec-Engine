package de.omnikryptec.graphics.render;

public interface RenderList<T> {
    
    void addObject(Object o);
    
    T get();
}
