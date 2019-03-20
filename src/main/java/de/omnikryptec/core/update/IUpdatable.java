package de.omnikryptec.core.update;

import de.omnikryptec.util.updater.Time;

public interface IUpdatable {
    
    boolean passive();
    
    default void update(Time time) {
    }
    
    default void init(ILayer layer) {
    }
    
    default void deinit(ILayer layer) {
    }
    
}
