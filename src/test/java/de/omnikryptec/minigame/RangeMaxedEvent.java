package de.omnikryptec.minigame;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.event.Event;

public class RangeMaxedEvent extends Event{
    
    public final Entity entity;
    
    public RangeMaxedEvent(Entity e) {
        this.entity = e;
    }
    
}
