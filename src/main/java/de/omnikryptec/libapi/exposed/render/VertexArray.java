package de.omnikryptec.libapi.exposed.render;

import de.omnikryptec.libapi.exposed.render.VertexBufferLayout.VertexBufferElement;

public interface VertexArray {

    void bindArray();

    void unbindArray();
    
    void addVertexBuffer(VertexBuffer buffer, VertexBufferLayout layout);

    void addVertexBuffer(VertexBuffer buffer, VertexBufferElement element);
    
    void setIndexBuffer(IndexBuffer buffer);
}
