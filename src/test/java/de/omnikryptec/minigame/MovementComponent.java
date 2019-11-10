package de.omnikryptec.minigame;

import de.omnikryptec.ecs.component.Component;

public class MovementComponent implements Component {
    public float dx, dy;

    public MovementComponent(final float dx, final float dy) {
        this.dx = dx;
        this.dy = dy;
    }
}
