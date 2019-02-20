package de.omnikryptec.minigame;

import de.omnikryptec.ecs.component.Component;

public class MovementComponent implements Component{
public float dx,dy;
    
    public MovementComponent(float dx, float dy) {
        this.dx = dx;
        this.dy = dy;
    }
}
