package de.omnikryptec.render.objects;

public interface IRenderedObjectListener {
    
    void onAdd(RenderedObject mgr);
    
    void onRemove(RenderedObject mgr);
    
}
