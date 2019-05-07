package de.omnikryptec.render.batch.vertexmanager;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.batch.module.ModuleBatchingManager;

public interface VertexManager {

    void init(ModuleBatchingManager mgr);
    
    default void addData(final float... fs) {
        this.addData(fs, 0, fs.length);
    }
    
    void addData(float[] floats, int offset, int length);

    void prepareNext(Texture baseTexture, int requiredFloats);

    void forceFlush();

    void begin();

}
