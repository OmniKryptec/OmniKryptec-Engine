package de.omnikryptec.libapi.exposed.render;

import de.omnikryptec.resource.MeshData.Primitive;

public interface Renderable {

    void bindRenderable();

    void unbindRenderable();

    Primitive primitive();

    int elementCount();

    boolean hasIndexBuffer();
    
}
