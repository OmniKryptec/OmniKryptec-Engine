package de.omnikryptec.render3;

public interface BatchedRenderer {
    
    void start();
    
    BatchCache end();
    
    void put(Iterable<? extends InstanceDataProvider> list);
    
    void put(BatchCache cache);
    
}
