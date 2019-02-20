package de.omnikryptec.minigame;

import de.omnikryptec.ecs.component.Component;

public class MovementComp implements Component{
public float dx,dy;
    
    public MovementComp(float dx, float dy) {
        this.dx = dx;
        this.dy = dy;
    }
}
