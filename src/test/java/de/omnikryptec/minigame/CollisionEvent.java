package de.omnikryptec.minigame;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.event.Event;

public class CollisionEvent extends Event {
    public final Entity e1;
    public final Entity e2;
    
    public CollisionEvent(final Entity e1, final Entity e2) {
        this.e1 = e1;
        this.e2 = e2;
    }
    
    public boolean hasFlags(final int flags) {
        return this.e1.flags == flags || this.e2.flags == flags;
    }
    
    public Entity getEntity(final int flags) {
        return this.e1.flags == flags ? this.e1 : (this.e2.flags == flags ? this.e2 : null);
    }
}
