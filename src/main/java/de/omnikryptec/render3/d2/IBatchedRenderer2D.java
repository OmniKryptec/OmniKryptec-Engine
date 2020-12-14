package de.omnikryptec.render3.d2;

import de.omnikryptec.render3.d2.sprites.IRenderer2D;

public interface IBatchedRenderer2D extends IRenderer2D {
    
    void start();
    
    @Override
    default void flush() {
        flushWithOptionalCache();
    }
    
    BatchCache flushWithOptionalCache();
    
    void put(InstanceDataProvider idp);
    
    void put(BatchCache cache);
    
}
