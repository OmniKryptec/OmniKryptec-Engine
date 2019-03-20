package de.omnikryptec.core.update;

import de.omnikryptec.event.EventBus;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.updater.Time;

import java.util.ArrayList;
import java.util.Collection;

public class ULayer implements IUpdatable, ILayer {
    
    private EventBus eventBus;
    
    private boolean isInitialized;
    
    private Collection<IUpdatable> updatablesActive;
    private Collection<IUpdatable> updatablesPassive;
    
    public ULayer() {
        this.eventBus = new EventBus();
        this.updatablesActive = new ArrayList<>();
        this.updatablesPassive = new ArrayList<>();
    }
    
    @Override
    public EventBus getEventBus() {
        return eventBus;
    }
    
    public void addUpdatable(IUpdatable updatable) {
        Util.ensureNonNull(updatable);
        if (updatable.passive()) {
            updatablesPassive.add(updatable);
        } else {
            updatablesActive.add(updatable);
        }
        if (isInitialized) {
            updatable.init(this);
        }
    }
    
    public void removeUpdatable(IUpdatable updatable) {
        Util.ensureNonNull(updatable);
        if (updatable.passive()) {
            updatablesPassive.remove(updatable);
        } else {
            updatablesActive.remove(updatable);
        }
        if (isInitialized) {
            updatable.deinit(this);
        }
    }
    
    @Override
    public boolean passive() {
        return false;
    }
    
    @Override
    public void update(Time time) {
        for (IUpdatable updatable : updatablesActive) {
            updatable.update(time);
        }
    }
    
    @Override
    public void init(ILayer layer) {
        if (layer != null && layer.getEventBus() != eventBus) {
            layer.getEventBus().register(eventBus);
        }
        for (IUpdatable updatable : updatablesActive) {
            updatable.init(this);
        }
        for (IUpdatable updatable : updatablesPassive) {
            updatable.init(this);
        }
        isInitialized = true;
    }
    
    @Override
    public void deinit(ILayer layer) {
        if (layer != null && layer.getEventBus() != eventBus) {
            layer.getEventBus().unregister(eventBus);
        }
        for (IUpdatable updatable : updatablesActive) {
            updatable.deinit(this);
        }
        for (IUpdatable updatable : updatablesPassive) {
            updatable.deinit(this);
        }
        isInitialized = false;
    }
    
}
