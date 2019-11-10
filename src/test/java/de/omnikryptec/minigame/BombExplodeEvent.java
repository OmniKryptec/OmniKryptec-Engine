package de.omnikryptec.minigame;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.event.Event;

public class BombExplodeEvent extends Event {
    public final Entity bomb;

    public BombExplodeEvent(final Entity bomb) {
        this.bomb = bomb;
    }
}
