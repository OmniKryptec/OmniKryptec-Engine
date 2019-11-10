package de.omnikryptec.minigame;

import de.omnikryptec.ecs.component.Component;

public class CollisionComponent implements Component {
    public float w, h;

    public CollisionComponent(final float w, final float h) {
        this.w = w;
        this.h = h;
    }
}
