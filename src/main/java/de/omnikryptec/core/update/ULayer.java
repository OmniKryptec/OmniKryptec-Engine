package de.omnikryptec.core.update;

import java.util.ArrayList;
import java.util.Collection;

import de.omnikryptec.event.EventBus;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.updater.Time;

public class ULayer extends AbstractUpdateable implements ILayer {
    
    private EventBus eventBus;
    
    private boolean isInitialized;
    
    private Collection<AbstractUpdateable> updateablesActive;
    private Collection<AbstractUpdateable> updateablesPassive;
    
    public ULayer() {
        super(false);
        this.eventBus = new EventBus();
        this.updateablesActive = new ArrayList<>();
        this.updateablesPassive = new ArrayList<>();
    }
    
    @Override
    public EventBus getEventBus() {
        return eventBus;
    }
    
    public void addUpdateable(AbstractUpdateable updateable) {
        Util.ensureNonNull(updateable);
        if (updateable.passive) {
            updateablesPassive.add(updateable);
        } else {
            updateablesActive.add(updateable);
        }
        if (isInitialized) {
            updateable.init(this);
        }
    }
    
    public void removeUodateable(AbstractUpdateable updateable) {
        Util.ensureNonNull(updateable);
        if (updateable.passive) {
            updateablesPassive.remove(updateable);
        } else {
            updateablesActive.remove(updateable);
        }
        if (isInitialized) {
            updateable.deinit(this);
        }
    }
    
    @Override
    public void update(Time time, UpdatePass pass) {
        for (AbstractUpdateable up : updateablesActive) {
            up.update(time, pass);
        }
    }
    
    @Override
    public void init(ILayer layer) {
        if (layer != null && layer.getEventBus() != eventBus) {
            layer.getEventBus().register(eventBus);
        }
        for (AbstractUpdateable up : updateablesActive) {
            up.init(this);
        }
        for (AbstractUpdateable up : updateablesPassive) {
            up.init(this);
        }
        isInitialized = true;
    }
    
    @Override
    public void deinit(ILayer layer) {
        if (layer != null && layer.getEventBus() != eventBus) {
            layer.getEventBus().unregister(eventBus);
        }
        for (AbstractUpdateable up : updateablesActive) {
            up.deinit(this);
        }
        for (AbstractUpdateable up : updateablesPassive) {
            up.deinit(this);
        }
        isInitialized = false;
    }
    
}
