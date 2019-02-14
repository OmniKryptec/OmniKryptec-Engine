package de.omnikryptec.render.batch;

import de.omnikryptec.libapi.exposed.render.Texture;

public interface VertexManager {
    
    default void addVertex(float... fs) {
        this.addVertex(fs, 0, fs.length);
    }
    
    void addVertex(float[] floats, int offset, int length);
    
    void prepareNext(Texture baseTexture, int requiredFloats);
    
    void forceFlush();
    
}
