package de.omnikryptec.libapi.exposed.render;

import java.nio.IntBuffer;

public interface IndexBuffer {
    
    void bindBuffer();
    
    void unbindBuffer();
    
    void storeData(IntBuffer data, boolean dynamic);
    
}
