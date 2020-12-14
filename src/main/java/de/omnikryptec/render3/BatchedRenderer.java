package de.omnikryptec.render3;

import de.omnikryptec.render3.sprites.AnotherFuckingRenderer;

public interface BatchedRenderer extends AnotherFuckingRenderer {
    
    void start();
    
    @Override
    default void flush() {
        flushWithOptionalCache();
    }
    
    BatchCache flushWithOptionalCache();
    
    void put(InstanceDataProvider idp);
    
    void put(BatchCache cache);
    
}
