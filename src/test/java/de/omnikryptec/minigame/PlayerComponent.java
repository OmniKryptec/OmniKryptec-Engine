package de.omnikryptec.minigame;

import de.omnikryptec.ecs.component.Component;

public class PlayerComponent implements Component {
    public float maxXv,maxYv;

    public final float shOffsetX,shOffsetY;
    
    public PlayerComponent(float maxXv, float maxYv, float ox, float oy) {
        this.maxXv = maxXv;
        this.maxYv = maxYv;
        this.shOffsetX = ox;
        this.shOffsetY = oy;
    }
    
   
}
