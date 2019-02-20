package de.omnikryptec.minigame;

import de.omnikryptec.ecs.component.Component;

public class HitBoxComponent implements Component {
    public float w, h;
    
    public HitBoxComponent(float w, float h) {
        this.w = w;
        this.h = h;
    }
}
