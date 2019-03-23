package de.omnikryptec.minigame;

import org.joml.Vector2f;

import de.omnikryptec.ecs.component.Component;

public class PositionComponent implements Component {
    
    public final Vector2f pos;
    
    public PositionComponent(float x, float y) {
        this.pos = new Vector2f(x, y);
    }
    
}
