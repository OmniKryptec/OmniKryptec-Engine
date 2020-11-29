package de.omnikryptec.render3;

import java.util.List;
import java.util.function.Supplier;

public interface BatchedRenderer {
    
    void render(List<? extends Supplier<InstanceData>> list);
    
    void render(BatchCache cache);
    
    BatchCache prepare(List<InstanceData> list);
    
}
