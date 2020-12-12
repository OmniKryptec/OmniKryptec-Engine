package de.omnikryptec.render3;

import java.util.function.Supplier;

public interface BatchedRenderer {
    
    void start();
    
    BatchCache end();
    
    void put(Iterable<? extends Supplier<? extends InstanceData>> list);
    
    void put(BatchCache cache);
    
}
