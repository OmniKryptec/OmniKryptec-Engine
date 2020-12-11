package de.omnikryptec.render3;

import java.util.function.Supplier;

import de.omnikryptec.render3.Batch2D.Target;

public interface BatchedRenderer {
    
    void start(Target target);
    
    BatchCache end();
    
    void put(Iterable<? extends Supplier<? extends InstanceData>> list);
    
    void put(BatchCache cache);
    
}
