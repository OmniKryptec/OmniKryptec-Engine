package de.omnikryptec.minigame;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.event.Event;

public class CollisionEvent extends Event{
    public final Entity e1;
    public final Entity e2;
    
    public CollisionEvent(Entity e1, Entity e2) {
        this.e1 = e1;
        this.e2 = e2;
    }
    
    public boolean hasFlags(int flags) {
        return e1.flags == flags || e2.flags == flags;
    }
    
    public Entity getEntity(int flags) {
        return e1.flags == flags ? e1 : (e2.flags == flags ? e2 : null);
    }
}
