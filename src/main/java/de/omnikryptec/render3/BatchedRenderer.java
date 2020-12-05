package de.omnikryptec.render3;

import java.util.function.Supplier;

public interface BatchedRenderer {
    
    void render(Iterable<? extends Supplier<InstanceData>> list);
    
    void render(BatchCache cache);
    
    BatchCache prepare(Iterable<InstanceData> list);
    
}
