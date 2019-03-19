package de.omnikryptec.core.update;

import de.omnikryptec.event.EventBus;

public class ProvidingLayer implements ILayer {
    
    private EventBus eventbus;
    
    public ProvidingLayer(EventBus eventBus) {
        this.eventbus = eventBus;
    }
    
    @Override
    public EventBus getEventBus() {
        return eventbus;
    }
    
}
