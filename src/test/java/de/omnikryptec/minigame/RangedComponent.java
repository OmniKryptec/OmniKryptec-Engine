package de.omnikryptec.minigame;

import de.omnikryptec.ecs.component.Component;

public class RangedComponent implements Component {
    
    public final float maxrange;
    public final float startX,startY;
    
    public RangedComponent(float max, float startX, float startY) {
        this.maxrange = max;
        this.startX = startX;
        this.startY = startY;
    }
    
}
