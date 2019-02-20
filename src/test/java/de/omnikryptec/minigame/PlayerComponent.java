package de.omnikryptec.minigame;

import de.omnikryptec.ecs.component.Component;

public class PlayerComponent implements Component {
    public float maxXv,maxYv;

    public PlayerComponent(float maxXv, float maxYv) {
        this.maxXv = maxXv;
        this.maxYv = maxYv;
    }
    
   
}
