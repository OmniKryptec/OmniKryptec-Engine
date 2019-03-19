package de.omnikryptec.core.update;

import de.omnikryptec.util.updater.Time;

public abstract class AbstractUpdateable {
    
    public final boolean passive;
    
    public AbstractUpdateable(boolean passive) {
        this.passive = passive;
    }
    
    public void update(Time time, UpdatePass pass) {
        
    }
    
    public void init(ILayer layer) {
        
    }
    
    public void deinit(ILayer layer) {
        
    }
}
