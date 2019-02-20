package de.omnikryptec.minigame;

import de.omnikryptec.ecs.component.Component;

public class HitBoxComp implements Component {
    public float w, h;
    
    public HitBoxComp(float w, float h) {
        this.w = w;
        this.h = h;
    }
}
