package de.omnikryptec.libapi.exposed.render;

public interface VertexArray {
    
    void bindArray();
    
    void addVertexBuffer(VertexBuffer buffer, VertexBufferLayout layout);
    
}
