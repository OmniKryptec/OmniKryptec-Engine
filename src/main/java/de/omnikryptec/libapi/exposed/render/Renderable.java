package de.omnikryptec.libapi.exposed.render;

import de.omnikryptec.resource.MeshData.PrimitiveType;

public interface Renderable {

    void bindRenderable();
    void unbindRenderable();
    PrimitiveType primitive();
    int elementCount();
    boolean hasIndexBuffer();
    
}
