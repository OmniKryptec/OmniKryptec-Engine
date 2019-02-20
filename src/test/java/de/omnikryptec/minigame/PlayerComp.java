package de.omnikryptec.minigame;

import de.omnikryptec.ecs.component.Component;

public class PlayerComp implements Component {
    public float maxXv,maxYv;

    public PlayerComp(float maxXv, float maxYv) {
        this.maxXv = maxXv;
        this.maxYv = maxYv;
    }
    
   
}
