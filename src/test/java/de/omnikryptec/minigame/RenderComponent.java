package de.omnikryptec.minigame;

import de.omnikryptec.ecs.component.Component;
import de.omnikryptec.util.data.Color;

public class RenderComponent implements Component {
    
    public float w, h;
    public Color color;
    
    public RenderComponent(float w, float h) {
        this.w = w;
        this.h = h;
    }
    
    public RenderComponent(float w, float h, Color color) {
        this(w,h);
        this.color = color;
    }
}
