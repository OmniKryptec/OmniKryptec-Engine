package de.omnikryptec.core.update;

import de.omnikryptec.util.updater.Time;

public interface IUpdateable {
    
    void update(Time time, UpdatePass pass);
    
    boolean passive();
    
    default void init(ILayer layer) {
        
    }
    
    default void deinit(ILayer layer) {
        
    }
}
