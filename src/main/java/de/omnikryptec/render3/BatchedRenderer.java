package de.omnikryptec.render3;

import java.util.List;

public interface BatchedRenderer {
    
    void render(List<InstanceData> list);
    
    void render(BatchCache cache);
    
    BatchCache prepare(List<InstanceData> list);
    
}
