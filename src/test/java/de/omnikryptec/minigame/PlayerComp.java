package de.omnikryptec.minigame;

import de.omnikryptec.ecs.component.Component;

public class PlayerComp implements Component{
    public float vx,vy;
    
    public PlayerComp(float vx, float vy) {
        this.vx = vx;
        this.vy = vy;
    }
}
