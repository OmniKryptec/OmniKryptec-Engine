package de.omnikryptec.minigame;

import de.omnikryptec.ecs.component.Component;

public class PlayerComponent implements Component {
    
    public float vx, vy;
    
    public PlayerComponent(float vx, float vy) {
        this.vx = vx;
        this.vy = vy;
    }
    
}
